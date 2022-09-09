package com.turikhay.mc.mapmodcompanion.spigot;

import com.turikhay.mc.mapmodcompanion.worldid.WorldId;
import com.turikhay.mc.mapmodcompanion.worldid.WorldIdRequest;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.plugin.messaging.PluginMessageListener;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.Arrays;
import java.util.Locale;
import java.util.Optional;
import java.util.UUID;

import static com.turikhay.mc.mapmodcompanion.worldid.WorldIdCompanion.WORLD_ID_CHANNEL_NAME;

public class WorldIdHandler extends Handler<WorldId, WorldIdHandler.PlayerWorldIdRequest> implements Listener, PluginMessageListener {

    public WorldIdHandler(CompanionSpigot plugin) {
        super(WORLD_ID_CHANNEL_NAME, plugin);
        this.logUnconditionally = true;
    }

    @Override
    public void init() {
        super.init();
        plugin.getServer().getMessenger().registerIncomingPluginChannel(plugin, WORLD_ID_CHANNEL_NAME, this);
    }

    @Override
    public void scheduleLevelIdPacket(Runnable r, Context<PlayerWorldIdRequest> context) {
        if (context.getSource() != EventSource.PLUGIN_MESSAGE) {
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
    protected Handler.IdRef<WorldId> processRef(Handler.IdRef<WorldId> idRef, Context<PlayerWorldIdRequest> context) {
        PlayerWorldIdRequest pr = context.getAux();
        if (pr == null) {
            return idRef; // no request context, return as-is
        }
        int prefixLength = pr.getRequest().getPrefixLength();
        if (idRef.getId().getPrefixLength() == prefixLength) {
            return idRef; // ok
        }
        if (CompanionSpigot.ENABLE_LOGGING) {
            plugin.getLogger().info(String.format(Locale.ROOT,
                    "Modifying response packet for %s (different prefix length)",
                    pr.getPlayer().getName()
            ));
        }
        return IdRef.of(idRef.getId().withPrefixLength(prefixLength));
    }

    @Override
    public void onPluginMessageReceived(@NotNull String channel, @NotNull Player player, @NotNull byte[] message) {
        if (!channel.equals(WORLD_ID_CHANNEL_NAME)) {
            return;
        }
        WorldIdRequest request;
        try {
            request = WorldIdRequest.parse(message);
        } catch (IOException e) {
            plugin.getLogger().info(String.format(Locale.ROOT,
                    "Received possibly corrupted world id request from %s: %s",
                    player.getName(),
                    Arrays.toString(message)
            ));
            plugin.getLogger().info("Error message: " + e);
            return;
        }
        if (CompanionSpigot.ENABLE_LOGGING) {
            plugin.getLogger().info(String.format(Locale.ROOT,
                    "Responding to %s's request (channel %s): %s",
                    player.getName(), channel, Arrays.toString(message)
            ));
        }
        sendLevelId(player, new Context<>(EventSource.PLUGIN_MESSAGE, new PlayerWorldIdRequest(player, request)));
    }

    static class PlayerWorldIdRequest {
        private final Player player;
        private final WorldIdRequest request;

        public PlayerWorldIdRequest(Player player, WorldIdRequest request) {
            this.player = player;
            this.request = request;
        }

        public Player getPlayer() {
            return player;
        }

        public WorldIdRequest getRequest() {
            return request;
        }
    }
}
