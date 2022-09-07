package com.turikhay.mc.mapmodcompanion.worldid;

import com.turikhay.mc.mapmodcompanion.IdMessagePacket;

import javax.annotation.Nullable;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Objects;

public /* sealed */ abstract class WorldId implements IdMessagePacket<WorldId> {
    private static final int MAGIC_MARKER = 42;

    @Override
    public WorldId combineWith(WorldId packet) {
        if (this instanceof Numeric || packet instanceof Numeric) {
            // combining with numeric id always "pollutes" the result
            return new WorldId.Numeric(
                    Objects.hash(getNumericId(), packet.getNumericId())
            );
        }
        return new WorldId.Textual(packet.getStringId() + '_' + getStringId());
    }

    @Override
    public void constructPacket(DataOutputStream out) throws IOException {
        out.writeByte(0);         // packetId
        out.writeByte(MAGIC_MARKER); // 42 (literally)
        byte[] data = getStringId().getBytes(StandardCharsets.UTF_8);
        out.write(data.length);     // length
        out.write(data);            // UTF
    }

    @Nullable
    public static WorldId tryRead(byte[] data) {
        DataInputStream in = new DataInputStream(new ByteArrayInputStream(data));
        try {
            if (in.readByte() != 0) {
                return null;
            }
            if (in.readByte() != MAGIC_MARKER) {
                return null;
            }
            int length = in.readByte();
            byte[] buf = new byte[length];
            int read = in.read(buf, 0, length);
            if (read < length) {
                return null;
            }
            String id = new String(buf, StandardCharsets.UTF_8);
            Numeric possiblyNumeric = Numeric.tryNumeric(id);
            if (possiblyNumeric != null) {
                return possiblyNumeric;
            }
            return new Textual(id);
        } catch (IOException ignored) {
        }
        return null;
    }

    public static WorldId textual(String id) {
        return new Textual(id);
    }

    public static WorldId numeric(int id) {
        return new Numeric(id);
    }

    protected abstract int getNumericId();
    protected abstract String getStringId();

    static class Numeric extends WorldId {
        private final int id;

        public Numeric(int id) {
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
            return "WorldId.Numeric{" +
                    "id=" + id +
                    '}';
        }

        @Nullable
        public static Numeric tryNumeric(String value) {
            int numeric;
            try {
                numeric = Integer.parseInt(value);
            } catch (NumberFormatException ignored) {
                return null;
            }
            return new Numeric(numeric);
        }
    }

    static class Textual extends WorldId {
        private final String id;

        public Textual(String id) {
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
                    '}';
        }
    }
}
