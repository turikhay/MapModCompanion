package com.turikhay.mc.mapmodcompanion.spigot;

import com.turikhay.mc.mapmodcompanion.worldid.WorldId;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.plugin.messaging.PluginMessageListener;
import org.jetbrains.annotations.NotNull;

import static com.turikhay.mc.mapmodcompanion.worldid.WorldIdCompanion.WORLD_ID_CHANNEL_NAME;
import static com.turikhay.mc.mapmodcompanion.worldid.WorldIdCompanion.WORLD_ID_PACKET_DELAY;

public class WorldIdHandler extends Handler<WorldId> implements Listener, PluginMessageListener {

    public WorldIdHandler(CompanionSpigot plugin) {
        super(WORLD_ID_CHANNEL_NAME, plugin);
    }

    @Override
    public void init() {
        super.init();
        plugin.getServer().getMessenger().registerIncomingPluginChannel(plugin, WORLD_ID_CHANNEL_NAME, this);
    }

    @Override
    public void scheduleLevelIdPacket(Runnable r, EventSource source) {
        plugin.getServer().getScheduler().runTaskLater(plugin, r, 20L * WORLD_ID_PACKET_DELAY);
    }

    @Override
    public WorldId getId(World world) {
        return WorldId.createTruncatingLength(world.getUID().toString());
    }

    @Override
    public void onPluginMessageReceived(@NotNull String channel, @NotNull Player player, @NotNull byte[] message) {
        if (!channel.equals(WORLD_ID_CHANNEL_NAME)) {
            return;
        }
        // JourneyMap Server also sends this packet unconditionally
        sendLevelId(player, EventSource.PLUGIN_MESSAGE);
    }
}
