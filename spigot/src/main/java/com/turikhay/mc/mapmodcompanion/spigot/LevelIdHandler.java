package com.turikhay.mc.mapmodcompanion.spigot;

import com.turikhay.mc.mapmodcompanion.*;
import org.bukkit.entity.Player;
import org.bukkit.plugin.messaging.PluginMessageListener;

import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;

public class LevelIdHandler implements Handler, PluginMessageListener {
    private final Logger logger;
    private final String channelName;
    private final boolean legacyChannel;
    private final MapModCompanion plugin;

    public LevelIdHandler(Logger logger, String channelName, boolean legacyChannel, MapModCompanion plugin) {
        this.logger = logger;
        this.channelName = channelName;
        this.legacyChannel = legacyChannel;
        this.plugin = plugin;
    }

    public void init() throws InitializationException {
        plugin.registerIncomingChannel(channelName, legacyChannel, this);
        plugin.registerOutgoingChannel(channelName);
    }

    @Override
    public void cleanUp() {
        plugin.unregisterIncomingChannel(channelName, this);
        plugin.unregisterOutgoingChannel(channelName);
    }

    @Override
    public void onPluginMessageReceived(String channel, Player player, byte[] requestBytes) {
        Integer protocolVersion = plugin.getProtocolLib().map(lib -> lib.getProtocolVersion(player)).orElse(null);
        int id = plugin.getRegistry().getId(player.getWorld());
        PrefixedIdRequest request;
        try {
            request = PrefixedIdRequest.parse(requestBytes, protocolVersion);
        } catch (MalformedPacketException e) {
            logger.log(Level.WARNING, "world_id request from " + player.getName() + " might be corrupted", e);
            logger.fine(() -> "Payload: " + Arrays.toString(requestBytes));
            return;
        }
        PrefixedId prefixedId = request.constructId(id);
        byte[] responseBytes = PrefixedId.Serializer.instance().serialize(prefixedId);
        logger.fine(() -> "Sending world_id packet to " + player.getName() + ": " + Arrays.toString(responseBytes));
        player.sendPluginMessage(plugin, channelName, responseBytes);
    }

    public static class Factory implements Handler.Factory<MapModCompanion> {
        private final String configPath;
        private final String channelName;
        private final boolean legacyChannel;

        public Factory(String configPath, String channelName, boolean legacyChannel) {
            this.configPath = configPath;
            this.channelName = channelName;
            this.legacyChannel = legacyChannel;
        }

        @Override
        public String getName() {
            return channelName;
        }

        @Override
        public LevelIdHandler create(MapModCompanion plugin) throws InitializationException {
            plugin.checkEnabled(configPath);
            LevelIdHandler handler = new LevelIdHandler(
                    new PrefixLogger(plugin.getVerboseLogger(), channelName),
                    channelName, legacyChannel, plugin
            );
            handler.init();
            return handler;
        }
    }
}
