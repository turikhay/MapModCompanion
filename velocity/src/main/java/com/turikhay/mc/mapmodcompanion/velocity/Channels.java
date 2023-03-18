package com.turikhay.mc.mapmodcompanion.velocity;

import com.velocitypowered.api.proxy.messages.ChannelIdentifier;
import com.velocitypowered.api.proxy.messages.LegacyChannelIdentifier;
import com.velocitypowered.api.proxy.messages.MinecraftChannelIdentifier;

import static com.turikhay.mc.mapmodcompanion.Channels.*;

public interface Channels {
    ChannelIdentifier WORLD_ID = MinecraftChannelIdentifier.from(WORLDID_CHANNEL);
    ChannelIdentifier WORLD_ID_LEGACY = new LegacyChannelIdentifier(WORLDID_LEGACY_CHANNEL);
    ChannelIdentifier XAERO_MINIMAP = MinecraftChannelIdentifier.from(XAERO_MINIMAP_CHANNEL);
    ChannelIdentifier XAERO_WORLDMAP = MinecraftChannelIdentifier.from(XAERO_WORLDMAP_CHANNEL);
}
