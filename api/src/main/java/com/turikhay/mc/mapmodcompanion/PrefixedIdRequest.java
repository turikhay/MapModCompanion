package com.turikhay.mc.mapmodcompanion;

import javax.annotation.Nullable;
import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.EOFException;
import java.io.IOException;
import java.util.Objects;

import static com.turikhay.mc.mapmodcompanion.Id.MAGIC_MARKER;

/**
 * Parsed description of a VoxelMap-style world id request packet.
 *
 * <p>Clients send such a packet to learn how the server expects the id to be
 * encoded. Use {@link #parse(byte[], Integer)} to interpret the request and
 * {@link #constructId(int)} to produce a {@link PrefixedId} for the response.</p>
 *
 * <pre>{@code
 * PrefixedIdRequest request = PrefixedIdRequest.parse(data, protocolVersion);
 * PrefixedId id = request.constructId(1337);
 * }</pre>
 */
public class PrefixedIdRequest {
    private final int padding;
    private final boolean usesMagicByte;

    /**
     * Creates a new request descriptor.
     *
     * @param padding       number of leading zero bytes
     * @param usesMagicByte whether the {@link Id#MAGIC_MARKER} is present
     */
    public PrefixedIdRequest(int padding, boolean usesMagicByte) {
        this.padding = padding;
        this.usesMagicByte = usesMagicByte;
    }

    /**
     * Constructs a {@link PrefixedId} using this request's parameters.
     *
     * @param id numeric world id
     * @return {@link PrefixedId} matching the request
     */
    public PrefixedId constructId(int id) {
        return new PrefixedId(padding, usesMagicByte, id);
    }

    /** {@inheritDoc} */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PrefixedIdRequest that = (PrefixedIdRequest) o;
        return padding == that.padding && usesMagicByte == that.usesMagicByte;
    }

    /** {@inheritDoc} */
    @Override
    public int hashCode() {
        return Objects.hash(padding, usesMagicByte);
    }

    /** {@inheritDoc} */
    @Override
    public String toString() {
        return "PrefixedIdRequest{" +
                "padding=" + padding +
                ", usesMagicByte=" + usesMagicByte +
                '}';
    }

    /**
     * Parses a VoxelMap-style request packet.
     *
     * @param payload         raw packet bytes
     * @param protocolVersion client's protocol version or {@code null} if unknown
     * @return parsed request descriptor
     * @throws MalformedPacketException if the payload cannot be parsed
     */
    public static PrefixedIdRequest parse(byte[] payload, @Nullable Integer protocolVersion) throws MalformedPacketException {
        int padding = -1;
        try (DataInputStream in = new DataInputStream(new ByteArrayInputStream(payload))) {
            int c;
            try {
                do {
                    padding++;
                    c = in.readByte();
                } while (c == 0);
                if (c != MAGIC_MARKER) {
                    throw new MalformedPacketException("first byte after zero padding in the request is not a magic byte");
                }
            } catch (EOFException e) {
                if (padding == 2) {
                    // VoxelMap Forge 1.12.2
                    return new PrefixedIdRequest(1, false);
                } else {
                    throw new MalformedPacketException("ambiguous zero-filled request packet");
                }
            }
        } catch (IOException e) {
            throw new MalformedPacketException("unexpected exception reading the request packet", e);
        }
        switch (padding) {
            case 1:
                if (protocolVersion != null && protocolVersion <= 753) { // 1.16.3 and below
                    // VoxelMap Forge 1.13.2 - 1.16.3
                    return new PrefixedIdRequest(1, false);
                }
                // VoxelMap Forge 1.16.4+
                // VoxelMap Fabric 1.19.3+
                // JourneyMap 1.16.5+
                return new PrefixedIdRequest(padding, true);
            case 3:
                // VoxelMap LiteLoader 1.8.9 - 1.12.2
                // VoxelMap Fabric 1.14.4 - 1.19.x
                return new PrefixedIdRequest(0, true);
            default:
                throw new MalformedPacketException("unexpected prefix length in the request packet");
        }
    }
}
