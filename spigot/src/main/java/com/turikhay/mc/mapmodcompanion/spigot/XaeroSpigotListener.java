package com.turikhay.mc.mapmodcompanion.spigot;

import com.turikhay.mc.mapmodcompanion.InitializationException;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerEvent;
import org.bukkit.event.player.PlayerJoinEvent;

import java.util.Set;
import java.util.logging.Logger;

class XaeroSpigotListener implements XaeroListener, Listener {
    public static final String NAME = "Spigot";

    private final Logger logger;
    private final MapModCompanion plugin;
    private final String channelName;
    private final XaeroLevelMapSender sender;

    XaeroSpigotListener(Logger logger, MapModCompanion plugin, String channelName, XaeroLevelMapSender sender) {
        this.logger = logger;
        this.plugin = plugin;
        this.channelName = channelName;
        this.sender = sender;
    }

    private boolean ignoreWorldChange;

    @Override
    public void init(Set<String> neighbors) throws InitializationException {
        plugin.registerOutgoingChannel(channelName);
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
        logger.fine("Event listener has been registered");

        if (neighbors.contains(XaeroRespawnPacketListener.NAME)) {
            logger.info("Spigot listener will ignore world change events because respawn packets are already being listened");
            ignoreWorldChange = true;
        }
    }

    @Override
    public void cleanUp() {
        plugin.unregisterOutgoingChannel(channelName);
        HandlerList.unregisterAll(this);
        logger.fine("Event listener has been unregistered");
    }


    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerJoined(PlayerJoinEvent event) {
        sendPacket(event, XaeroLevelMapSender.EventSource.JOIN);
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onWorldChanged(PlayerChangedWorldEvent event) {
        if (ignoreWorldChange) {
            return;
        }
        sendPacket(event, XaeroLevelMapSender.EventSource.WORLD_CHANGE);
    }

    private void sendPacket(PlayerEvent event, XaeroLevelMapSender.EventSource source) {
        sender.sendPacket(event.getPlayer(), source);
    }
}
