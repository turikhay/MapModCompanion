package com.turikhay.mc.mapmodcompanion.spigot;

import com.turikhay.mc.mapmodcompanion.*;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerRegisterChannelEvent;

import java.util.Arrays;
import java.util.Locale;
import java.util.UUID;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

public class XaeroHandler implements Handler, Listener {
    private final Logger logger;
    private final String configPath;
    private final String channelName;
    private final MapModCompanion plugin;
    private final ScheduledExecutorService scheduler;

    public XaeroHandler(Logger logger, String configPath, String channelName, MapModCompanion plugin) {
        this.logger = logger;
        this.configPath = configPath;
        this.channelName = channelName;
        this.plugin = plugin;

        this.scheduler = Executors.newSingleThreadScheduledExecutor(
                new DaemonThreadFactory(ILogger.ofJava(logger), XaeroHandler.class)
        );
    }

    public void init() throws InitializationException {
        plugin.registerOutgoingChannel(channelName);
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
        logger.fine("Event listener has been registered");
    }

    @Override
    public void cleanUp() {
        plugin.unregisterOutgoingChannel(channelName);
        HandlerList.unregisterAll(this);
        logger.fine("Event listener has been unregistered");
        scheduler.shutdown();
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerJoined(PlayerJoinEvent event) {
        sendPacket(event, Type.JOIN);
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onWorldChanged(PlayerChangedWorldEvent event) {
        sendPacket(event, Type.WORLD_CHANGE);
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPacketRegistered(PlayerRegisterChannelEvent event) {
        if (event.getChannel().equals(channelName)) {
            sendPacket(event, Type.REGISTER);
        }
    }

    private void sendPacket(PlayerEvent event, Type type) {
        Player p = event.getPlayer();
        World world = p.getWorld();
        int id = plugin.getRegistry().getId(world);
        byte[] payload = LevelMapProperties.Serializer.instance().serialize(id);
        SendPayloadTask task = new SendPayloadTask(logger, plugin, p.getUniqueId(), channelName, payload, world.getUID());
        int repeatTimes = plugin.getConfig().getInt(
                configPath + ".events." + type.name().toLowerCase(Locale.ROOT) + ".repeat_times",
                1
        );
        if (repeatTimes > 1) {
            for (int i = 0; i < repeatTimes; i++) {
                scheduler.schedule(task, i, TimeUnit.SECONDS);
            }
        } else {
            task.run();
        }
    }

    private enum Type {
        REGISTER,
        JOIN,
        WORLD_CHANGE,
    }

    public static class Factory implements Handler.Factory<MapModCompanion> {
        private final String configPath;
        private final String channelName;

        public Factory(String configPath, String channelName) {
            this.configPath = configPath;
            this.channelName = channelName;
        }

        @Override
        public String getName() {
            return channelName;
        }

        @Override
        public XaeroHandler create(MapModCompanion plugin) throws InitializationException {
            plugin.checkEnabled(configPath);
            XaeroHandler handler = new XaeroHandler(
                    new PrefixLogger(plugin.getVerboseLogger(), channelName),
                    configPath, channelName, plugin
            );
            handler.init();
            return handler;
        }
    }

    private static class SendPayloadTask implements Runnable {
        private final Logger logger;
        private final MapModCompanion plugin;
        private final UUID playerId;
        private final String channelName;
        private final byte[] payload;
        private final UUID expectedWorld;

        public SendPayloadTask(Logger logger, MapModCompanion plugin, UUID playerId, String channelName, byte[] payload,
                               UUID expectedWorld) {
            this.logger = logger;
            this.plugin = plugin;
            this.playerId = playerId;
            this.channelName = channelName;
            this.payload = payload;
            this.expectedWorld = expectedWorld;
        }

        @Override
        public void run() {
            Player player = plugin.getServer().getPlayer(playerId);
            if (player == null) {
                return;
            }
            UUID world = player.getWorld().getUID();
            if (!world.equals(expectedWorld)) {
                logger.fine("Skipping sending Xaero's LevelMapProperties to " + player.getName() + ": unexpected world");
                return;
            }
            logger.fine(() -> "Sending Xaero's LevelMapProperties to " + player.getName() + ": " + Arrays.toString(payload));
            player.sendPluginMessage(plugin, channelName, payload);
        }
    }
}
