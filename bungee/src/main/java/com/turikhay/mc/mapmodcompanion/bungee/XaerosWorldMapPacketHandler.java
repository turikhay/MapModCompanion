package com.turikhay.mc.mapmodcompanion.bungee;

import static com.turikhay.mc.mapmodcompanion.xaeros.XaerosCompanion.XAEROS_WORLD_MAP_CHANNEL_NAME;

public class XaerosWorldMapPacketHandler extends XaerosAbstractPacketHandler {
    public XaerosWorldMapPacketHandler(CompanionBungee plugin) {
        super(XAEROS_WORLD_MAP_CHANNEL_NAME, plugin);
    }
}
