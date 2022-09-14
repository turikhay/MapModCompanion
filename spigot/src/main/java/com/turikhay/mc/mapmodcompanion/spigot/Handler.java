package com.turikhay.mc.mapmodcompanion.spigot;

import com.turikhay.mc.mapmodcompanion.IdMessagePacket;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerJoinEvent;

import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.Locale;
import java.util.Optional;

public abstract class Handler<Id extends IdMessagePacket<?>, A> implements Listener {
    protected final String channelName;
    protected final CompanionSpigot plugin;
    protected boolean logUnconditionally;

    private IdRef<Id> defaultId;

    public Handler(String channelName, CompanionSpigot plugin) {
        this.channelName = channelName;
        this.plugin = plugin;
    }

    public void init() throws InitializationException {
        try {
            plugin.getServer().getMessenger().registerOutgoingPluginChannel(plugin, channelName);
        } catch (Exception e) {
            throw new InitializationException("Couldn't register plugin channel", e);
        }
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
        defaultId = plugin.getDefaultWorld().map(world -> IdRef.of(getId(world))).orElse(null);
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerJoined(PlayerJoinEvent event) {
        sendLevelId(event.getPlayer(), Context.of(EventSource.JOIN));
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onWorldChanged(PlayerChangedWorldEvent event) {
        sendLevelId(event.getPlayer(), Context.of(EventSource.WORLD_CHANGE));
    }

    public void sendLevelId(Player player, Context<A> context) {
        scheduleLevelIdPacket(
                () -> {
                    IdRef<Id> idRef;
                    if (defaultId == null) {
                        idRef = IdRef.of(getId(player.getWorld()));
                    } else {
                        idRef = defaultId;
                    }
                    idRef = processRef(idRef, context);
                    if (CompanionSpigot.ENABLE_LOGGING) {
                        plugin.getLogger().info(String.format(Locale.ROOT,
                                "Sending world id to %s (channel: %s): %s. Data: %s",
                                player.getName(), channelName, idRef.id,
                                Arrays.toString(idRef.data)
                        ));
                    } else if(logUnconditionally) {
                        plugin.getLogger().info(String.format(Locale.ROOT,
                                "Sending world id to %s (channel: %s): %s",
                                player.getName(), channelName, idRef.id
                        ));
                    }
                    player.sendPluginMessage(plugin, channelName, idRef.data);
                },
                context
        );
    }

    protected IdRef<Id> processRef(IdRef<Id> idRef, Context<A> context) {
        return idRef;
    }

    public abstract void scheduleLevelIdPacket(Runnable r, Context<A> context);
    public abstract Id getId(World world);

    public enum EventSource {
        JOIN,
        WORLD_CHANGE,
        PLUGIN_MESSAGE
    }

    static class IdRef<Id extends IdMessagePacket<?>> {
        private final Id id;
        private final byte[] data;

        private IdRef(Id id, byte[] data) {
            this.id = id;
            this.data = data;
        }

        public Id getId() {
            return id;
        }

        @Override
        public String toString() {
            return "DefaultId{" +
                    "id=" + id +
                    ", data=" + Arrays.toString(data) +
                    '}';
        }

        static <Id extends IdMessagePacket<?>> IdRef<Id> of(Id id) {
            return new IdRef<>(id, IdMessagePacket.bytesPacket(id));
        }
    }

    static class Context<A> {
        private final EventSource source;
        @Nullable
        private final A aux;

        Context(EventSource source, @Nullable A aux) {
            this.source = source;
            this.aux = aux;
        }

        public EventSource getSource() {
            return source;
        }

        @Nullable
        public A getAux() {
            return aux;
        }

        private static <A> Context<A> of(EventSource source) {
            return new Context<>(source, null);
        }
    }

    public static class InitializationException extends Exception {
        public InitializationException(String message, Throwable cause) {
            super(message, cause, false, false);
        }
    }
}
