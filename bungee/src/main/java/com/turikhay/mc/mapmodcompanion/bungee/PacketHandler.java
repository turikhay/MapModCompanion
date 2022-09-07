package com.turikhay.mc.mapmodcompanion.bungee;

import com.turikhay.mc.mapmodcompanion.IdMessagePacket;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.connection.Server;
import net.md_5.bungee.api.event.PluginMessageEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.Locale;
import java.util.logging.Level;

public abstract class PacketHandler<Id2 extends IdMessagePacket<?>, Id extends IdMessagePacket<Id2>> implements Listener {
    private final String channelName;
    private final CompanionBungee plugin;

    public PacketHandler(String channelName, CompanionBungee plugin) {
        this.channelName = channelName;
        this.plugin = plugin;
    }

    public void init() {
        plugin.getProxy().registerChannel(channelName);
        plugin.getProxy().getPluginManager().registerListener(plugin, this);
    }

    /*
      Intercept world IDs received from the downstream servers
      in order to (hopefully) resolve ID conflicts that may occur
    */
    @EventHandler
    public void onPluginMessageSentToPlayer(PluginMessageEvent event) {
        if (!event.getTag().equals(channelName)) {
            return;
        }
        if (plugin.getLogger().isLoggable(Level.FINEST)) {
            plugin.getLogger().finest(String.format(Locale.ROOT,
                    "Data sent from %s to %s (channel %s):",
                    event.getSender(), event.getReceiver(), channelName
            ));
            plugin.getLogger().finest("Data (0): " + Arrays.toString(event.getData()));
        }
        if (!(event.getSender() instanceof Server)) {
            return;
        }
        Server server = (Server) event.getSender();
        if (!(event.getReceiver() instanceof ProxiedPlayer)) {
            return;
        }
        ProxiedPlayer player = (ProxiedPlayer) event.getReceiver();
        byte[] oldData = event.getData();
        Id oldId = tryRead(oldData);
        if (oldId == null) {
            plugin.getLogger().warning("Possibly corrupted packet from " + channelName + ": " + Arrays.toString(oldData));
            return;
        }
        event.setCancelled(true);
        Id2 newId = oldId.combineWith(getId(server));
        byte[] newData = IdMessagePacket.bytesPacket(newId);
        if (plugin.getLogger().isLoggable(Level.FINEST)) {
            plugin.getLogger().finest(String.format(Locale.ROOT,
                    "Changing data sent to %s (channel %s):", player.getName(), channelName
            ));
            plugin.getLogger().finest("Data (1): " + Arrays.toString(newData));
        }
        if (plugin.getLogger().isLoggable(Level.FINE)) {
            plugin.getLogger().fine(String.format(Locale.ROOT,
                    "Intercepting world id sent to %s (channel %s): %s -> %s",
                    player.getName(), channelName, oldId, newId
            ));
        }
        player.sendData(channelName, newData);
    }

    @Nullable
    public abstract Id tryRead(byte[] data);
    public abstract Id2 getId(Server server);
}
