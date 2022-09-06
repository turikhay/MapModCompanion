package com.turikhay.mc.mapmodcompanion.xaeros;

import com.turikhay.mc.mapmodcompanion.IdMessagePacket;

import javax.annotation.Nullable;
import java.io.*;
import java.util.Objects;

public class LevelMapProperties implements IdMessagePacket<LevelMapProperties> {
    private final int id;

    public LevelMapProperties(int id) {
        this.id = id;
    }

    @Override
    public LevelMapProperties combineWith(LevelMapProperties packet) {
        return new LevelMapProperties(Objects.hash(id, packet.id));
    }

    @Override
    public void constructPacket(DataOutputStream out) throws IOException {
        out.write(0);  // LevelMapProperties {
        out.writeInt(id); // id }
    }

    @Nullable
    public static LevelMapProperties tryRead(byte[] data) {
        DataInputStream in = new DataInputStream(new ByteArrayInputStream(data));
        try {
            int marker = in.readByte();
            if (marker == 0) {
                return new LevelMapProperties(in.readInt());
            }
        } catch (IOException ignored) { // EOF?
        }
        return null;
    }

    @Override
    public String toString() {
        return "LevelMapProperties{" +
                "id=" + id +
                '}';
    }
}
