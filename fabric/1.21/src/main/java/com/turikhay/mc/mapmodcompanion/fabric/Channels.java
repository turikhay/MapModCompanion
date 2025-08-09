package com.turikhay.mc.mapmodcompanion.fabric;

import net.minecraft.util.Identifier;

import static com.turikhay.mc.mapmodcompanion.Channels.*;

public interface Channels {
    Identifier WORLD_ID = of(WORLDID_CHANNEL);
    Identifier XAERO_MINIMAP = of(XAERO_MINIMAP_CHANNEL);
    Identifier XAERO_WORLDMAP = of(XAERO_WORLDMAP_CHANNEL);

    static Identifier of(String channel) {
        return Identifier.tryParse(channel);
    }
}
