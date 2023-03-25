package com.turikhay.mc.mapmodcompanion.spigot;

import com.turikhay.mc.mapmodcompanion.*;
import org.junit.jupiter.api.Test;

import static com.turikhay.mc.mapmodcompanion.spigot.PrefixedIdRequest.parse;
import static org.junit.jupiter.api.Assertions.*;

class PrefixedIdRequestParserTest {

    @Test
    void voxelMapForge1_12_2() throws MalformedPacketException {
        test(new PrefixedIdRequest(1, false), new byte[] { 0, 0 });
    }

    @Test
    void standardFormTest() throws MalformedPacketException {
        // JourneyMap 1.16.5, VoxelMap 1.19.2+
        test(
                new PrefixedIdRequest(1, true),
                new byte[] { 0, 42, 0 }
        );
    }

    @Test
    void voxelMapFixTest() throws MalformedPacketException {
        // VoxelMap LiteLoader 1.8.9 - 1.12.2, VoxelMap Fabric 1.14.4 - 1.19.x
        test(
                new PrefixedIdRequest(0, true),
                new byte[] { 0, 0, 0, 42 }
        );
    }

    private void test(PrefixedIdRequest expected, byte[] data) throws MalformedPacketException {
        assertEquals(expected, parse(data));
    }

    @Test
    void zeroTest() {
        assertThrows(MalformedPacketException.class, () -> parse(new byte[]{ 0 }));
    }
}
