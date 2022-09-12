package com.turikhay.mc.mapmodcompanion.bungee;

import com.turikhay.mc.mapmodcompanion.worldid.WorldId;
import net.md_5.bungee.api.connection.Server;
import org.jetbrains.annotations.Nullable;

import static com.turikhay.mc.mapmodcompanion.worldid.WorldIdCompanion.WORLD_ID_CHANNEL_NAME;
import static com.turikhay.mc.mapmodcompanion.worldid.WorldIdCompanion.WORLD_ID_LEGACY_CHANNEL_NAME;

public class WorldIdPacketHandler extends PacketHandler<WorldId, WorldId> {

    private final boolean legacy;

    public WorldIdPacketHandler(CompanionBungee plugin, boolean legacy) {
        super(legacy ? WORLD_ID_LEGACY_CHANNEL_NAME : WORLD_ID_CHANNEL_NAME, plugin);
        this.legacy = legacy;
        this.logUnconditionally = true;
    }

    @Nullable
    @Override
    public WorldId tryRead(byte[] data) {
        return WorldId.tryRead(data, legacy);
    }

    @Override
    public WorldId getId(Server server) {
        return WorldId.textual(server.getInfo().getName());
    }
}
