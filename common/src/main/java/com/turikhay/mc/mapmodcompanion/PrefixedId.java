package com.turikhay.mc.mapmodcompanion;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Objects;

public class PrefixedId implements Id {
    private final int padding;
    private final int id;

    public PrefixedId(int padding, int id) {
        this.padding = padding;
        this.id = id;
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
        return new PrefixedId(this.padding, id);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PrefixedId that = (PrefixedId) o;
        return padding == that.padding && id == that.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(padding, id);
    }

    @Override
    public String toString() {
        return "PrefixedId{" +
                "prefixLength=" + padding +
                ", id=" + id +
                '}';
    }

    public static class Deserializer implements Id.Deserializer<PrefixedId> {
        private final boolean legacy;

        public Deserializer(boolean legacy) {
            this.legacy = legacy;
        }

        @Override
        public PrefixedId deserialize(byte[] data) throws MalformedPacketException {
            DataInputStream in = new DataInputStream(new ByteArrayInputStream(data));
            try {
                int prefixSize;
                if (legacy) {
                    prefixSize = 0;
                    in.readByte(); // always skip first byte
                } else {
                    prefixSize = -1;
                    int c;
                    do {
                        prefixSize++;
                        c = in.readByte();
                    } while(c == 0);
                    if (c != MAGIC_MARKER) {
                        throw new MalformedPacketException("missing magic marker in the prefixed id packet");
                    }
                }
                int length = in.readByte();
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
                return new PrefixedId(prefixSize, numeric);
            } catch (IOException e) {
                throw new MalformedPacketException("unexpected error reading prefixed id packet", e);
            }
        }

        public static Deserializer ofModern() {
            return new Deserializer(false);
        }

        public static Deserializer ofLegacy() {
            return new Deserializer(true);
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
                out.writeByte(MAGIC_MARKER); // 42 (literally)
                byte[] data = String.valueOf(id.getId()).getBytes(StandardCharsets.UTF_8);
                out.write(data.length);      // length
                out.write(data);             // UTF
            } catch (IOException e) {
                throw new RuntimeException("unexpected error", e);
            }
            return array.toByteArray();
        }

        public static Serializer ofAny() {
            return INSTANCE == null ? INSTANCE = new Serializer() : INSTANCE;
        }
    }
}
