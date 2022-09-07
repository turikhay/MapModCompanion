package com.turikhay.mc.mapmodcompanion.bungee;

import com.turikhay.mc.mapmodcompanion.worldid.WorldId;
import net.md_5.bungee.api.connection.Server;
import org.jetbrains.annotations.Nullable;

import static com.turikhay.mc.mapmodcompanion.worldid.WorldIdCompanion.WORLD_ID_CHANNEL_NAME;

public class WorldIdPacketHandler extends PacketHandler<WorldId, WorldId> {

    public WorldIdPacketHandler(CompanionBungee plugin) {
        super(WORLD_ID_CHANNEL_NAME, plugin);
    }

    @Nullable
    @Override
    public WorldId tryRead(byte[] data) {
        return WorldId.tryRead(data);
    }

    @Override
    public WorldId getId(Server server) {
        return new WorldId(server.getInfo().getName());
    }
}
