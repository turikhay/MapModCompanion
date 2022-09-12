package com.turikhay.mc.mapmodcompanion.worldid;

import com.turikhay.mc.mapmodcompanion.IdMessagePacket;

import javax.annotation.Nullable;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Objects;

public /* sealed */ abstract class WorldId implements IdMessagePacket<WorldId> {
    public static final int MAGIC_MARKER = 42;
    private static final int DEFAULT_PREFIX_LENGTH = 1;

    protected final boolean isNumeric;
    @Nullable
    protected final Integer prefixLength;

    public WorldId(boolean isNumeric, @Nullable Integer prefixLength) {
        this.isNumeric = isNumeric;
        this.prefixLength = prefixLength;
    }

    public int getPrefixLength() {
        return prefixLength == null ? DEFAULT_PREFIX_LENGTH : prefixLength;
    }

    public WorldId withPrefixLength(int prefixLength) {
        return new Delegating(this, prefixLength);
    }

    @Override
    public WorldId combineWith(WorldId packet) {
        Integer newPrefixLength = selectPrefixLength(this, packet);
        if (this instanceof Numeric || packet instanceof Numeric) {
            // combining with numeric id always "pollutes" the result
            return new WorldId.Numeric(
                    Objects.hash(getNumericId(), packet.getNumericId()),
                    newPrefixLength
            );
        }
        return new WorldId.Textual(
                packet.getStringId() + '_' + getStringId(),
                newPrefixLength
        );
    }

    @Override
    public void constructPacket(DataOutputStream out) throws IOException {
        int prefixBytesCount = prefixLength == null ? DEFAULT_PREFIX_LENGTH : prefixLength;
        for (int i = 0; i < prefixBytesCount; i++) {
            out.writeByte(0);     // packetId, or a prefix
        }
        out.writeByte(MAGIC_MARKER); // 42 (literally)
        byte[] data = getStringId().getBytes(StandardCharsets.UTF_8);
        out.write(data.length);     // length
        out.write(data);            // UTF
    }

    @Nullable
    public static WorldId tryRead(byte[] data, boolean legacy) {
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
                    return null;
                }
            }
            int length = in.readByte();
            byte[] buf = new byte[length];
            int read = in.read(buf, 0, length);
            if (read < length) {
                return null;
            }
            String id = new String(buf, StandardCharsets.UTF_8);
            Numeric possiblyNumeric = Numeric.tryNumeric(id, prefixSize);
            if (possiblyNumeric != null) {
                return possiblyNumeric;
            }
            return new Textual(id, prefixSize);
        } catch (IOException ignored) {
        }
        return null;
    }

    public static WorldId textual(String id) {
        return new Textual(id, null);
    }

    public static WorldId numeric(int id) {
        return new Numeric(id, null);
    }

    protected abstract int getNumericId();
    protected abstract String getStringId();

    static class Numeric extends WorldId {
        private final int id;

        private Numeric(int id, @Nullable Integer prefixLength) {
            super(true, prefixLength);
            this.id = id;
        }

        @Override
        protected int getNumericId() {
            return id;
        }

        @Override
        protected String getStringId() {
            return String.valueOf(id);
        }

        @Override
        public String toString() {
            return "Numeric{" +
                    "id=" + id +
                    (prefixLength == null ? "" : ", prefix=" + prefixLength) +
                    '}';
        }

        @Nullable
        private static Numeric tryNumeric(String value, int prefixSize) {
            int numeric;
            try {
                numeric = Integer.parseInt(value);
            } catch (NumberFormatException ignored) {
                return null;
            }
            return new Numeric(numeric, prefixSize);
        }
    }

    static class Textual extends WorldId {
        private final String id;

        public Textual(String id, @Nullable Integer prefixLength) {
            super(false, prefixLength);
            this.id = id;
        }

        @Override
        protected int getNumericId() {
            return id.hashCode();
        }

        @Override
        protected String getStringId() {
            return id;
        }

        @Override
        public String toString() {
            return "WorldId.Textual{" +
                    "id='" + id + '\'' +
                    (prefixLength == null ? "" : ", prefix=" + prefixLength) +
                    '}';
        }
    }

    private static class Delegating extends WorldId {
        private final WorldId parent;

        public Delegating(WorldId parent, @Nullable Integer prefixLength) {
            super(parent.isNumeric, prefixLength);
            this.parent = parent;
        }

        @Override
        protected int getNumericId() {
            return parent.getNumericId();
        }

        @Override
        protected String getStringId() {
            return parent.getStringId();
        }

        @Override
        public String toString() {
            return "Delegating{" +
                    "parent=" + parent +
                    ", isNumeric=" + isNumeric +
                    ", prefixLength=" + prefixLength +
                    '}';
        }
    }

    @Nullable
    private static Integer selectPrefixLength(WorldId id0, WorldId id1) {
        Integer length0 = id0.prefixLength;
        Integer length1 = id1.prefixLength;
        if (Objects.equals(length0, length1)) {
            return length0;
        }
        if (length0 != null && length1 != null) {
            throw new IllegalArgumentException("trying to combine IDs with different prefix sizes");
        }
        if (length0 != null) {
            return length0;
        }
        return length1;
    }
}
