package com.turikhay.mc.mapmodcompanion.bungee;

import static com.turikhay.mc.mapmodcompanion.xaeros.XaerosCompanion.XAEROS_MINIMAP_CHANNEL_NAME;

public class XaerosMinimapPacketHandler extends XaerosAbstractPacketHandler {
    public XaerosMinimapPacketHandler(CompanionBungee plugin) {
        super(XAEROS_MINIMAP_CHANNEL_NAME, plugin);
    }
}
