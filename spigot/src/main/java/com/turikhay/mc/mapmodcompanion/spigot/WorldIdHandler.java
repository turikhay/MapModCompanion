package com.turikhay.mc.mapmodcompanion.spigot;

import com.turikhay.mc.mapmodcompanion.worldid.WorldId;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.plugin.messaging.PluginMessageListener;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.Locale;
import java.util.UUID;

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
        if (source != EventSource.PLUGIN_MESSAGE) {
            // This handler should only send worldId on a request
            return;
        }
        r.run();
    }

    @Override
    public WorldId getId(World world) {
        UUID uuid = world.getUID();
        if (CompanionSpigot.USE_TEXTUAL_WORLD_ID) {
            return WorldId.textual(uuid.toString());
        } else {
            return WorldId.numeric(uuid.hashCode());
        }
    }

    @Override
    public void onPluginMessageReceived(@NotNull String channel, @NotNull Player player, @NotNull byte[] message) {
        if (!channel.equals(WORLD_ID_CHANNEL_NAME)) {
            return;
        }
        if (CompanionSpigot.ENABLE_LOGGING) {
            plugin.getLogger().info(String.format(Locale.ROOT,
                    "Responding to %s's request (channel %s): %s",
                    player.getName(), channel, Arrays.toString(message)
            ));
        }
        // JourneyMap Server also sends this packet unconditionally
        sendLevelId(player, EventSource.PLUGIN_MESSAGE);
    }
}
