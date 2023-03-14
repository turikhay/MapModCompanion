package com.turikhay.mc.mapmodcompanion;

import java.io.*;

public interface LevelMapProperties {
    class Deserializer implements Id.Deserializer<StandardId> {
        private static Deserializer INSTANCE;

        @Override
        public StandardId deserialize(byte[] data) throws MalformedPacketException {
            DataInputStream in = new DataInputStream(new ByteArrayInputStream(data));
            try {
                int marker = in.readByte();
                if (marker == 0) {
                    return new StandardId(in.readInt());
                }
                throw new MalformedPacketException("invalid marker byte (0x" + Integer.toHexString(marker) + ") in the standard id packet");
            } catch (IOException e) {
                throw new MalformedPacketException("unexpected error reading standard id packet", e);
            }
        }

        public static Deserializer instance() {
            return INSTANCE == null ? INSTANCE = new Deserializer() : INSTANCE;
        }
    }

    class Serializer implements Id.Serializer<StandardId> {
        private static Serializer INSTANCE;

        @Override
        public byte[] serialize(StandardId id) {
            return serialize(id.getId());
        }

        public byte[] serialize(int id) {
            ByteArrayOutputStream array = new ByteArrayOutputStream();
            try(DataOutputStream out = new DataOutputStream(array)) {
                out.write(0);           // LevelMapProperties {
                out.writeInt(id);         // id }
            } catch (IOException e) {
                throw new RuntimeException("unexpected error", e);
            }
            return array.toByteArray();
        }

        public static Serializer instance() {
            return INSTANCE == null ? INSTANCE = new Serializer() : INSTANCE;
        }
    }
}
