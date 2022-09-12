package com.turikhay.mc.mapmodcompanion.bungee;

import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.api.plugin.Plugin;

import java.util.Arrays;
import java.util.List;

public class CompanionBungee extends Plugin implements Listener {

    List<PacketHandler<?, ?>> handlers = Arrays.asList(
            new XaerosMinimapPacketHandler(this),
            new XaerosWorldMapPacketHandler(this),
            new WorldIdPacketHandler(this, false),
            new WorldIdPacketHandler(this, true)
    );

    @Override
    public void onEnable() {
        handlers.forEach(PacketHandler::init);
    }
}
