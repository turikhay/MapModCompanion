package com.turikhay.mc.mapmodcompanion.bungee;

import com.turikhay.mc.mapmodcompanion.xaeros.LevelMapProperties;
import net.md_5.bungee.api.connection.Server;
import net.md_5.bungee.api.plugin.Listener;
import org.jetbrains.annotations.Nullable;

import static com.turikhay.mc.mapmodcompanion.xaeros.XaerosCompanion.XAEROS_CHANNEL_NAME;

public class XaerosPacketHandler extends PacketHandler<LevelMapProperties, LevelMapProperties> implements Listener {

    public XaerosPacketHandler(CompanionBungee plugin) {
        super(XAEROS_CHANNEL_NAME, plugin);
    }

    @Nullable
    @Override
    public LevelMapProperties tryRead(byte[] data) {
        return LevelMapProperties.tryRead(data);
    }

    @Override
    public LevelMapProperties getId(Server server) {
        return new LevelMapProperties(server.getInfo().getName().hashCode());
    }
}
