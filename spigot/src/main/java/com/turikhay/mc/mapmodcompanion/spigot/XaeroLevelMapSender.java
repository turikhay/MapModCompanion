package com.turikhay.mc.mapmodcompanion.spigot;

import com.turikhay.mc.mapmodcompanion.LevelMapProperties;
import org.bukkit.World;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.Locale;
import java.util.UUID;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

class XaeroLevelMapSender {
    private final Logger logger;
    private final MapModCompanion plugin;
    private final String channelName;
    private final String configPath;
    private final ScheduledExecutorService scheduler;

    XaeroLevelMapSender(Logger logger, MapModCompanion plugin, String channelName, String configPath, ScheduledExecutorService scheduler) {
        this.logger = logger;
        this.plugin = plugin;
        this.channelName = channelName;
        this.configPath = configPath;
        this.scheduler = scheduler;
    }

    void sendPacket(Player p, EventSource source) {
        World world = p.getWorld();
        int id = plugin.getRegistry().getId(world);
        byte[] payload = LevelMapProperties.Serializer.instance().serialize(id);
        SendPayloadTask task = new SendPayloadTask(logger, plugin, p.getUniqueId(), channelName, payload, world.getUID());
        int repeatTimes = plugin.getConfig().getInt(
                configPath + ".events." + source.name().toLowerCase(Locale.ROOT) + ".repeat_times",
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

    enum EventSource {
        JOIN,
        WORLD_CHANGE,
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
