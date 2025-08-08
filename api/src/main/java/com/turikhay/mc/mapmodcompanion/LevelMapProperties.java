package com.turikhay.mc.mapmodcompanion;

import java.io.*;

/**
 * Binary format used by VoxelMap to communicate the world id.
 */
public interface LevelMapProperties {

    /**
     * Deserializes standard id packets.
     *
     * <pre>{@code
     * StandardId id = LevelMapProperties.Deserializer.instance().deserialize(data);
     * }</pre>
     */
    class Deserializer implements Id.Deserializer<StandardId> {
        private static Deserializer INSTANCE;

        /** {@inheritDoc} */
        @Override
        public StandardId deserialize(byte[] data) throws MalformedPacketException {
            DataInputStream in = new DataInputStream(new ByteArrayInputStream(data));
            try {
                int marker = in.readByte();
                if (marker == 0) {
                    return new StandardId(in.readInt());
                }
                throw new MalformedPacketException("invalid marker byte (0x" + Integer.toHexString(marker) + ") in the level map properties packet");
            } catch (IOException e) {
                throw new MalformedPacketException("unexpected error reading level map properties packet", e);
            }
        }

        /**
         * Returns a shared instance of the deserializer.
         */
        public static Deserializer instance() {
            return INSTANCE == null ? INSTANCE = new Deserializer() : INSTANCE;
        }
    }

    /**
     * Serializes standard ids into packet byte arrays.
     *
     * <pre>{@code
     * byte[] data = LevelMapProperties.Serializer.instance().serialize(id);
     * }</pre>
     */
    class Serializer implements Id.Serializer<StandardId> {
        private static Serializer INSTANCE;

        /** {@inheritDoc} */
        @Override
        public byte[] serialize(StandardId id) {
            return serialize(id.getId());
        }

        /**
         * Encodes the supplied id into the binary representation.
         *
         * @param id numeric world id
         * @return encoded packet bytes
         */
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

        /**
         * Returns a shared instance of the serializer.
         */
        public static Serializer instance() {
            return INSTANCE == null ? INSTANCE = new Serializer() : INSTANCE;
        }
    }
}
