package com.turikhay.mc.mapmodcompanion.velocity;

import com.turikhay.mc.mapmodcompanion.*;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.connection.PluginMessageEvent;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ServerConnection;
import com.velocitypowered.api.proxy.messages.ChannelIdentifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;

public class MessageHandler<IdType extends Id> implements Handler {
    private final Logger logger;
    private final MapModCompanion plugin;
    private final ChannelIdentifier channelId;
    private final Id.Deserializer<IdType> deserializer;
    private final Id.Serializer<IdType> serializer;

    public MessageHandler(Logger logger, MapModCompanion plugin, ChannelIdentifier channelId, Id.Deserializer<IdType> deserializer, Id.Serializer<IdType> serializer) {
        this.logger = logger;
        this.plugin = plugin;
        this.channelId = channelId;
        this.deserializer = deserializer;
        this.serializer = serializer;
    }

    public void init() {
        logger.debug("Registering the channel: {}", channelId);
        plugin.getServer().getChannelRegistrar().register(channelId);
        plugin.getServer().getEventManager().register(plugin, this);
    }

    @Subscribe
    public void onPluginMessage(PluginMessageEvent event) {
        if (!event.getIdentifier().equals(channelId)) {
            return;
        }

        if (logger.isDebugEnabled()) {
            logger.debug("Data sent from {} to {} (channel {}): {}", event.getSource(), event.getTarget(),
                    channelId, Arrays.toString(event.getData()));
        }

        if (!(event.getSource() instanceof ServerConnection)) {
            return;
        }
        var server = (ServerConnection) event.getSource();

        if (!(event.getTarget() instanceof Player)) {
            return;
        }
        var player = (Player) event.getTarget();

        IdType id;
        try {
            id = deserializer.deserialize(event.getData());
        } catch (MalformedPacketException e) {
            logger.warn("Received possibly malformed packet in {}", channelId, e);
            if (logger.isDebugEnabled()) {
                logger.debug("Packet data: {}", Arrays.toString(event.getData()));
            }
            return;
        }

        event.setResult(PluginMessageEvent.ForwardResult.handled());

        var newId = plugin.getConverter()
                .findMatch(id.getId())
                .map(id::<IdType>withId)
                .orElseGet(() -> IdBlender.DEFAULT.blend(id, server.getServerInfo().getName().hashCode()));

        logger.debug("Intercepting world_id packet sent to {} (channel {}): {} -> {}",
                player.getGameProfile().getName(), channelId.getId(), id, newId);

        player.sendPluginMessage(channelId, serializer.serialize(newId));
    }

    @Override
    public void cleanUp() {
        logger.debug("Unregistering the channel: {}", channelId);
        plugin.getServer().getChannelRegistrar().unregister(channelId);
        plugin.getServer().getEventManager().unregisterListener(plugin, this);
    }

    public static class Factory<IdType extends Id> implements Handler.Factory<MapModCompanion> {
        private final String configPath;
        private final ChannelIdentifier channelId;
        private final Id.Deserializer<IdType> deserializer;
        private final Id.Serializer<IdType> serializer;

        public Factory(String configPath, ChannelIdentifier channelId, Id.Deserializer<IdType> deserializer, Id.Serializer<IdType> serializer) {
            this.configPath = configPath;
            this.channelId = channelId;
            this.deserializer = deserializer;
            this.serializer = serializer;
        }

        @Override
        public String getName() {
            return channelId.getId();
        }

        @Override
        public MessageHandler<IdType> create(MapModCompanion plugin) throws InitializationException {
            if (!plugin.getConfig().getBoolean(configPath + ".enabled", true)) {
                throw new InitializationException("disabled in the config");
            }
            var handler = new MessageHandler<>(
                    LoggerFactory.getLogger(plugin.getClass().getName() + ":" + channelId.getId()),
                    plugin,
                    channelId,
                    deserializer,
                    serializer
            );
            handler.init();
            return handler;
        }
    }
}
