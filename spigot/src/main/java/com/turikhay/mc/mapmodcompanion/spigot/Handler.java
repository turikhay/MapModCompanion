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

    public Handler(String channelName, CompanionSpigot plugin) {
        this.channelName = channelName;
        this.plugin = plugin;
    }

    public void init() {
        plugin.getServer().getMessenger().registerOutgoingPluginChannel(plugin, channelName);
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
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
                    Id id = getId(player.getWorld());
                    byte[] data = IdMessagePacket.bytesPacket(id);
                    if (CompanionSpigot.ENABLE_LOGGING) {
                        plugin.getLogger().info(String.format(Locale.ROOT,
                                "Sending world id to %s (channel: %s): %s. Data: %s",
                                player.getName(), channelName, id, Arrays.toString(data)
                        ));
                    }
                    player.sendPluginMessage(plugin, channelName, data);
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
}
