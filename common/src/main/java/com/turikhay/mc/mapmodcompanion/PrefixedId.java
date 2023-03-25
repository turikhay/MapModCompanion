package com.turikhay.mc.mapmodcompanion;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Objects;

public class PrefixedId implements Id {
    private final int padding;
    private final boolean usesMagicByte;
    private final int id;

    public PrefixedId(int padding, boolean usesMagicByte, int id) {
        this.padding = padding;
        this.usesMagicByte = usesMagicByte;
        this.id = id;
    }

    @Deprecated
    public PrefixedId(int padding, int id) {
        this(padding, true, id);
    }

    @Override
    public int getId() {
        return id;
    }

    public int getPadding() {
        return padding;
    }

    @Override
    public PrefixedId withIdUnchecked(int id) {
        return new PrefixedId(this.padding, this.usesMagicByte, id);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PrefixedId that = (PrefixedId) o;
        return padding == that.padding && usesMagicByte == that.usesMagicByte && id == that.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(padding, usesMagicByte, id);
    }

    @Override
    public String toString() {
        return "PrefixedId{" +
                "padding=" + padding +
                ", usesMagicByte=" + usesMagicByte +
                ", id=" + id +
                '}';
    }

    public static class Deserializer implements Id.Deserializer<PrefixedId> {
        private static Deserializer INSTANCE;

        @Override
        public PrefixedId deserialize(byte[] data) throws MalformedPacketException {
            DataInputStream in = new DataInputStream(new ByteArrayInputStream(data));
            try {
                int length;
                int prefixSize = -1;
                int c;
                try {
                    do {
                        prefixSize++;
                        c = in.readByte();
                    } while (c == 0);
                } catch (EOFException e) {
                    // zero-filled response
                    throw new MalformedPacketException("zero-filled prefixed id packet");
                }
                boolean usesMagicNumber = true;
                if (c != MAGIC_MARKER) {
                    // 1.12.2 <= VoxelMap Forge <= 1.16.3 doesn't use MAGIC_MARKER in the response
                    usesMagicNumber = false;
                    length = c;
                } else {
                    length = in.readByte();
                }
                byte[] buf = new byte[length];
                int read = in.read(buf, 0, length);
                if (read < length) {
                    throw new MalformedPacketException("incorrect length (" + read + " < " + length + ") in the prefixed id packet");
                }
                String id = new String(buf, StandardCharsets.UTF_8);
                int numeric;
                try {
                    numeric = Integer.parseInt(id);
                } catch (NumberFormatException e) {
                    throw new MalformedPacketException("couldn't parse an integer from prefixed id packet", e);
                }
                return new PrefixedId(prefixSize, usesMagicNumber, numeric);
            } catch (IOException e) {
                throw new MalformedPacketException("unexpected error reading prefixed id packet", e);
            }
        }

        public static Deserializer instance() {
            return INSTANCE == null ? INSTANCE = new Deserializer() : INSTANCE;
        }
    }

    public static class Serializer implements Id.Serializer<PrefixedId> {
        private static Serializer INSTANCE;

        @Override
        public byte[] serialize(PrefixedId id) {
            ByteArrayOutputStream array = new ByteArrayOutputStream();
            try (DataOutputStream out = new DataOutputStream(array)) {
                for (int i = 0; i < id.getPadding(); i++) {
                    out.writeByte(0);      // packetId, or prefix
                }
                if (id.usesMagicByte) {
                    out.writeByte(MAGIC_MARKER); // 42 (literally)
                }
                byte[] data = String.valueOf(id.getId()).getBytes(StandardCharsets.UTF_8);
                out.write(data.length);      // length
                out.write(data);             // UTF
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
