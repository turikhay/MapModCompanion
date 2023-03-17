package com.turikhay.mc.mapmodcompanion.spigot;

import com.turikhay.mc.mapmodcompanion.MalformedPacketException;
import com.turikhay.mc.mapmodcompanion.PrefixedId;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.util.Objects;

import static com.turikhay.mc.mapmodcompanion.Id.MAGIC_MARKER;

public class PrefixedIdRequest {
    private final int padding;

    public PrefixedIdRequest(int padding) {
        this.padding = padding;
    }

    public PrefixedId constructId(int id) {
        return new PrefixedId(padding, id);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PrefixedIdRequest that = (PrefixedIdRequest) o;
        return padding == that.padding;
    }

    @Override
    public int hashCode() {
        return Objects.hash(padding);
    }

    @Override
    public String toString() {
        return "PrefixedIdRequest{" +
                "prefixLength=" + padding +
                '}';
    }

    public static PrefixedIdRequest parse(byte[] payload) throws MalformedPacketException {
        int padding = 0;
        try (DataInputStream in = new DataInputStream(new ByteArrayInputStream(payload))) {
            if (in.readByte() != 0) {
                throw new MalformedPacketException("unexpected first byte in the request");
            }
            int c;
            do {
                padding++;
                c = in.readByte();
            } while(c == 0);
            if (c != MAGIC_MARKER) {
                throw new MalformedPacketException("first byte after zero padding in the request is not a magic number");
            }
        } catch (IOException e) {
            throw new MalformedPacketException("unexpected exception reading the request packet", e);
        }
        switch (padding) {
            case 1:
                // Normal request
                break;
            case 3:
                // VoxelMap fix
                padding = 0;
                break;
            default:
                throw new MalformedPacketException("unexpected prefix length in the request packet");
        }
        return new PrefixedIdRequest(padding);
    }
}
