package com.turikhay.mc.mapmodcompanion.worldid;

import com.turikhay.mc.mapmodcompanion.IdMessagePacket;

import javax.annotation.Nullable;
import java.io.*;
import java.nio.charset.StandardCharsets;

import static com.turikhay.mc.mapmodcompanion.worldid.WorldIdCompanion.WORLD_ID_MAX_LENGTH;

public class WorldId implements IdMessagePacket<WorldId> {
    private static final int MAX_ID_LENGTH = WORLD_ID_MAX_LENGTH;
    private static final int HALF_LENGTH = MAX_ID_LENGTH / 2 - 1;
    private static final int MAGIC_MARKER = 42;

    private final String id;

    private WorldId(String id) {
        this.id = id;
    }

    @Override
    public WorldId combineWith(WorldId packet) {
        return createTruncatingLength(
                truncate(packet.id, HALF_LENGTH) + '_' + truncate(id, HALF_LENGTH)
        );
    }

    @Override
    public void constructPacket(DataOutputStream out) throws IOException {
        out.writeByte(0);         // packetId
        out.writeByte(MAGIC_MARKER); // 42 (literally)
        byte[] data = id.getBytes(StandardCharsets.UTF_8);
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
            return new WorldId(new String(buf, StandardCharsets.UTF_8));
        } catch (IOException ignored) {
        }
        return null;
    }

    @Override
    public String toString() {
        return "WorldId{" +
                "id='" + id + '\'' +
                '}';
    }

    public static WorldId createTruncatingLength(String id) {
        return new WorldId(truncate(id, MAX_ID_LENGTH));
    }

    private static String truncate(String text, int size) {
        while (text.getBytes(StandardCharsets.UTF_8).length > size) {
            if (text.length() == 1) {
                throw new IllegalArgumentException("cannot truncate");
            }
            text = text.substring(0, text.length() - 1);
        }
        return text;
    }
}
