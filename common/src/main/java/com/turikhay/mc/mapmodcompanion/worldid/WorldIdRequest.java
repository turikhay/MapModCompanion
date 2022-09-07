package com.turikhay.mc.mapmodcompanion.worldid;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;

import static com.turikhay.mc.mapmodcompanion.worldid.WorldId.MAGIC_MARKER;

public class WorldIdRequest {

    private final int prefixLength;

    public WorldIdRequest(int prefixLength) {
        this.prefixLength = prefixLength;
    }

    public int getPrefixLength() {
        return prefixLength;
    }

    public static WorldIdRequest parse(byte[] message) throws IOException {
        int prefixLength = 0;
        try (DataInputStream in = new DataInputStream(new ByteArrayInputStream(message))) {
            if (in.readByte() != 0) {
                throw new IOException("unexpected first byte");
            }
            int c;
            do {
                prefixLength++;
                c = in.readByte();
            } while(c == 0);
            if (c != MAGIC_MARKER) {
                throw new IOException("first byte prefix is not a magic number");
            }
        }
        switch (prefixLength) {
            case 1:
                // Normal request
                break;
            case 3:
                // VoxelMap fix
                prefixLength = 0;
                break;
            default:
                throw new IOException("unexpected prefix length");
        }
        return new WorldIdRequest(prefixLength);
    }
}
