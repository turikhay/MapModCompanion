package com.turikhay.mc.mapmodcompanion.spigot;

import com.turikhay.mc.mapmodcompanion.Handler;
import com.turikhay.mc.mapmodcompanion.InitializationException;
import com.turikhay.mc.mapmodcompanion.LevelMapProperties;
import com.turikhay.mc.mapmodcompanion.PrefixLogger;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerEvent;
import org.bukkit.event.player.PlayerJoinEvent;

import java.util.Arrays;
import java.util.Locale;
import java.util.logging.Logger;

public class XaeroHandler implements Handler, Listener {
    private final Logger logger;
    private final String configPath;
    private final String channelName;
    private final MapModCompanion plugin;

    public XaeroHandler(Logger logger, String configPath, String channelName, MapModCompanion plugin) {
        this.logger = logger;
        this.configPath = configPath;
        this.channelName = channelName;
        this.plugin = plugin;
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
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerJoined(PlayerJoinEvent event) {
        sendPacket(event, Type.JOIN);
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onWorldChanged(PlayerChangedWorldEvent event) {
        sendPacket(event, Type.WORLD_CHANGE);
    }

    private void sendPacket(PlayerEvent event, Type type) {
        Player player = event.getPlayer();
        int id = plugin.getRegistry().getId(player.getWorld());
        byte[] payload = LevelMapProperties.Serializer.instance().serialize(id);
        Runnable task = () -> {
            logger.fine(() -> "Sending Xaero's LevelMapProperties to " + player.getName() + ": " + Arrays.toString(payload));
            player.sendPluginMessage(plugin, channelName, payload);
        };
        int repeatTimes = plugin.getConfig().getInt(
                configPath + ".events." + type.name().toLowerCase(Locale.ROOT) + ".repeat_times",
                1
        );
        if (repeatTimes > 1) {
            for (int i = 0; i < repeatTimes; i++) {
                plugin.getServer().getScheduler().runTaskLater(plugin, task, 20L * i);
            }
        } else {
            task.run();
        }
    }

    private enum Type {
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
}
