package com.turikhay.mc.mapmodcompanion.spigot;

import com.turikhay.mc.mapmodcompanion.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerEvent;
import org.bukkit.event.player.PlayerJoinEvent;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.logging.Logger;

public class XaeroHandler implements Handler, Listener {
    private final Logger logger;
    private final String channelName;
    private final MapModCompanion plugin;
    private final ScheduledExecutorService scheduler;
    private final XaeroLevelMapSender sender;

    public XaeroHandler(Logger logger, String configPath, String channelName, MapModCompanion plugin) {
        this.logger = logger;
        this.channelName = channelName;
        this.plugin = plugin;

        this.scheduler = Executors.newSingleThreadScheduledExecutor(
                new DaemonThreadFactory(ILogger.ofJava(logger), XaeroHandler.class)
        );
        this.sender = new XaeroLevelMapSender(logger, plugin, channelName, configPath, scheduler);
    }

    public void init() throws InitializationException {
        plugin.registerOutgoingChannel(channelName);
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
        logger.fine("Event listener has been registered");
    }

    @Override
    public void cleanUp() {
        plugin.unregisterOutgoingChannel(channelName);
        HandlerList.unregisterAll(this);
        logger.fine("Event listener has been unregistered");
        scheduler.shutdown();
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerJoined(PlayerJoinEvent event) {
        sendPacket(event, XaeroLevelMapSender.EventSource.JOIN);
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onWorldChanged(PlayerChangedWorldEvent event) {
        sendPacket(event, XaeroLevelMapSender.EventSource.WORLD_CHANGE);
    }

    private void sendPacket(PlayerEvent event, XaeroLevelMapSender.EventSource source) {
        sender.sendPacket(event.getPlayer(), source);
    }

    public static class Factory implements Handler.Factory<MapModCompanion> {
        private final String configPath;
        private final String channelName;

        public Factory(String configPath, String channelName) {
            this.configPath = configPath;
            this.channelName = channelName;
        }

        @Override
        public String getName() {
            return channelName;
        }

        @Override
        public XaeroHandler create(MapModCompanion plugin) throws InitializationException {
            plugin.checkEnabled(configPath);
            XaeroHandler handler = new XaeroHandler(
                    new PrefixLogger(plugin.getVerboseLogger(), channelName),
                    configPath, channelName, plugin
            );
            handler.init();
            return handler;
        }
    }


}
