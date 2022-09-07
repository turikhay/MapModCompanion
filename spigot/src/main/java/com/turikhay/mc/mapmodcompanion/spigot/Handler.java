package com.turikhay.mc.mapmodcompanion.spigot;

import com.turikhay.mc.mapmodcompanion.IdMessagePacket;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerJoinEvent;

import java.util.Arrays;
import java.util.Locale;

public abstract class Handler<Id extends IdMessagePacket<?>> implements Listener {
    private final String channelName;
    protected final CompanionSpigot plugin;

    private IdRef<Id> defaultId;

    public Handler(String channelName, CompanionSpigot plugin) {
        this.channelName = channelName;
        this.plugin = plugin;
    }

    public void init() {
        plugin.getServer().getMessenger().registerOutgoingPluginChannel(plugin, channelName);
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
        defaultId = plugin.getDefaultWorld().map(world -> IdRef.of(getId(world))).orElse(null);
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerJoined(PlayerJoinEvent event) {
        sendLevelId(event.getPlayer(), EventSource.JOIN);
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onWorldChanged(PlayerChangedWorldEvent event) {
        sendLevelId(event.getPlayer(), EventSource.WORLD_CHANGE);
    }

    public void sendLevelId(Player player, EventSource source) {
        scheduleLevelIdPacket(
                () -> {
                    IdRef<Id> idRef;
                    if (defaultId == null) {
                        idRef = IdRef.of(getId(player.getWorld()));
                    } else {
                        idRef = defaultId;
                    }
                    if (CompanionSpigot.ENABLE_LOGGING) {
                        plugin.getLogger().info(String.format(Locale.ROOT,
                                "Sending world id to %s (channel: %s): %s. Data: %s",
                                player.getName(), channelName, idRef.id,
                                Arrays.toString(idRef.data)
                        ));
                    }
                    player.sendPluginMessage(plugin, channelName, idRef.data);
                },
                source
        );
    }

    public abstract void scheduleLevelIdPacket(Runnable r, EventSource source);
    public abstract Id getId(World world);

    public enum EventSource {
        JOIN,
        WORLD_CHANGE,
        PLUGIN_MESSAGE
    }

    private static class IdRef<Id extends IdMessagePacket<?>> {
        private final Id id;
        private final byte[] data;

        private IdRef(Id id, byte[] data) {
            this.id = id;
            this.data = data;
        }

        @Override
        public String toString() {
            return "DefaultId{" +
                    "id=" + id +
                    ", data=" + Arrays.toString(data) +
                    '}';
        }

        private static <Id extends IdMessagePacket<?>> IdRef<Id> of(Id id) {
            return new IdRef<>(id, IdMessagePacket.bytesPacket(id));
        }
    }
}
