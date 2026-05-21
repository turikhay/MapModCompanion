package com.turikhay.mc.mapmodcompanion.spigot;

import com.github.retrooper.packetevents.PacketEvents;
import com.github.retrooper.packetevents.event.*;
import com.github.retrooper.packetevents.protocol.packettype.PacketType;

import java.util.Set;

class XaeroRespawnPacketListener implements XaeroListener, PacketListener {
    public static final String NAME = "PacketEvents";

    private final XaeroLevelMapSender sender;
    private PacketListenerCommon listenerRef;

    XaeroRespawnPacketListener(XaeroLevelMapSender sender) {
        this.sender = sender;
    }

    @Override
    public void init(Set<String> neighbors) {
        this.listenerRef = events().registerListener(this, PacketListenerPriority.MONITOR);
    }

    @Override
    public void cleanUp() {
        if (listenerRef != null) {
            events().unregisterListener(listenerRef);
        }
    }

    @Override
    public void onPacketSend(PacketSendEvent event) {
        if (event.getPacketType() == PacketType.Play.Server.RESPAWN) {
            sender.sendPacket(event.getPlayer(), XaeroLevelMapSender.EventSource.RESPAWN_PACKET);
        }
    }

    private static EventManager events() {
        return PacketEvents.getAPI().getEventManager();
    }
}
