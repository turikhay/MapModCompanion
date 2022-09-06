package com.turikhay.mc.mapmodcompanion.bungee;

import com.turikhay.mc.mapmodcompanion.IdMessagePacket;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.connection.Server;
import net.md_5.bungee.api.event.PluginMessageEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

import javax.annotation.Nullable;
import java.util.Arrays;

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
        if (!(event.getSender() instanceof Server)) {
            return;
        }
        Server server = (Server) event.getSender();
        if (!(event.getReceiver() instanceof ProxiedPlayer)) {
            return;
        }
        ProxiedPlayer player = (ProxiedPlayer) event.getReceiver();
        byte[] data = event.getData();
        Id oldId = tryRead(data);
        if (oldId == null) {
            plugin.getLogger().warning("Possibly corrupted packet from " + channelName + ": " + Arrays.toString(data));
            return;
        }
        event.setCancelled(true);
        Id2 newId = oldId.combineWith(getId(server));
        player.sendData(channelName, IdMessagePacket.bytesPacket(newId));
    }

    @Nullable
    public abstract Id tryRead(byte[] data);
    public abstract Id2 getId(Server server);
}
