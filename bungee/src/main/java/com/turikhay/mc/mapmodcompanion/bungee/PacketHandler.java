package com.turikhay.mc.mapmodcompanion.bungee;

import com.turikhay.mc.mapmodcompanion.*;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.connection.Server;
import net.md_5.bungee.api.event.PluginMessageEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

import java.util.Arrays;
import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;

public class PacketHandler<IdType extends Id> implements Handler, Listener {
    private final MapModCompanion plugin;
    private final Logger logger;
    private final String channelName;
    private final Id.Deserializer<IdType> deserializer;
    private final Id.Serializer<IdType> serializer;

    public PacketHandler(MapModCompanion plugin, Logger logger, String channelName,
                         Id.Deserializer<IdType> deserializer, Id.Serializer<IdType> serializer) {
        this.plugin = plugin;
        this.logger = logger;
        this.channelName = channelName;
        this.deserializer = deserializer;
        this.serializer = serializer;
    }

    public void init() {
        logger.fine("Registering channel " + channelName);
        plugin.getProxy().registerChannel(channelName);
        plugin.getProxy().getPluginManager().registerListener(plugin, this);
    }

    @Override
    public void cleanUp() {
        logger.fine("Unregistering channel " + channelName);
        plugin.getProxy().unregisterChannel(channelName);
        plugin.getProxy().getPluginManager().unregisterListener(this);
    }

    @EventHandler
    public void onPluginMessageSentToPlayer(PluginMessageEvent event) {
        if (!event.getTag().equals(channelName)) {
            return;
        }

        if (logger.isLoggable(Level.FINEST)) {
            logger.finest(String.format(Locale.ROOT,
                    "Data sent from %s to %s (channel %s):",
                    event.getSender(), event.getReceiver(), channelName
            ));
            logger.finest("Data (0): " + Arrays.toString(event.getData()));
        }

        if (!(event.getSender() instanceof Server)) {
            return;
        }
        Server server = (Server) event.getSender();

        if (!(event.getReceiver() instanceof ProxiedPlayer)) {
            return;
        }
        ProxiedPlayer player = (ProxiedPlayer) event.getReceiver();

        IdType id;
        try {
            id = deserializer.deserialize(event.getData());
        } catch (MalformedPacketException e) {
            if (logger.isLoggable(Level.FINE)) {
                logger.log(Level.FINE, "Received possibly malformed packet in " + channelName, e);
                logger.fine("Packet data: " + Arrays.toString(event.getData()));
            }
            return;
        }

        event.setCancelled(true);

        IdType newId = plugin.getConverter()
                .findMatch(id.getId())
                .map(id::<IdType>withId)
                .orElseGet(() -> IdBlender.DEFAULT.blend(id, server.getInfo().getName().hashCode()));

        logger.fine(String.format(Locale.ROOT,
                "Intercepting world_id packet sent to %s (channel %s): %s -> %s",
                player.getName(), channelName, id, newId
        ));

        player.sendData(channelName, serializer.serialize(newId));
    }

    @Override
    public String toString() {
        return "PacketHandler{" +
                "channelName='" + channelName + '\'' +
                '}';
    }

    public static class Factory<IdType extends Id> implements Handler.Factory<MapModCompanion> {
        private final String configPath;
        private final String channelName;
        private final Id.Deserializer<IdType> deserializer;
        private final Id.Serializer<IdType> serializer;

        public Factory(String configPath, String channelName,
                       Id.Deserializer<IdType> deserializer, Id.Serializer<IdType> serializer) {
            this.configPath = configPath;
            this.channelName = channelName;
            this.deserializer = deserializer;
            this.serializer = serializer;
        }

        @Override
        public String getName() {
            return channelName;
        }

        @Override
        public PacketHandler<IdType> create(MapModCompanion plugin) throws InitializationException {
            if (!plugin.getConfig().getBoolean(configPath + ".enabled", true)) {
                throw new InitializationException("disabled in the config");
            }
            PacketHandler<IdType> handler = new PacketHandler<>(
                    plugin,
                    new PrefixLogger(plugin.getVerboseLogger(), channelName),
                    channelName,
                    deserializer,
                    serializer
            );
            handler.init();
            return handler;
        }
    }
}
