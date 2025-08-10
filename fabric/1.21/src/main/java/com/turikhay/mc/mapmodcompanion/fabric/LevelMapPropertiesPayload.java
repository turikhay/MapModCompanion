package com.turikhay.mc.mapmodcompanion.fabric;

import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;

public class LevelMapPropertiesPayload {

    private interface LevelIdPayload extends CustomPayload {
        int id();
    }

    public record Minimap(int id) implements LevelIdPayload {
        public static final Id<Minimap> ID = new Id<>(Channels.XAERO_MINIMAP);

        @Override
        public Id<? extends CustomPayload> getId() {
            return ID;
        }
    }

    public record WorldMap(int id) implements LevelIdPayload {
        public static final Id<WorldMap> ID = new Id<>(Channels.XAERO_WORLDMAP);

        @Override
        public Id<? extends CustomPayload> getId() {
            return ID;
        }
    }

    public static void register() {
        register(Minimap.ID);
        register(WorldMap.ID);
    }

    private static <T extends LevelIdPayload> void register(CustomPayload.Id<T> id) {
        PayloadTypeRegistry.playS2C().register(id, new Codec<>());
    }

    public static class Codec<T extends LevelIdPayload> implements PacketCodec<RegistryByteBuf, T> {

        @Override
        public T decode(RegistryByteBuf buf) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void encode(RegistryByteBuf buf, T value) {
            buf.writeByte(0);
            buf.writeInt(value.id());
        }
    }

    private LevelMapPropertiesPayload() {
    }
}
