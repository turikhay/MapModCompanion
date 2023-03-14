package com.turikhay.mc.mapmodcompanion.spigot;

import com.turikhay.mc.mapmodcompanion.MalformedPacketException;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;

import static com.turikhay.mc.mapmodcompanion.spigot.PrefixedIdRequest.parse;
import static org.junit.jupiter.api.Assertions.*;

class PrefixedIdRequestParserTest {
    @Test
    void firstByteNonZeroTest() {
        assertThrows(MalformedPacketException.class, () -> parse(new byte[]{42, 1, 49}));
    }

    @Test
    void firstByteAfterPaddingNonMagicalTest() {
        assertThrows(MalformedPacketException.class, () -> parse(new byte[]{0, 0, 0, 43}));
    }

    @Test
    void singularPaddingTest() throws MalformedPacketException {
        assertEquals(new PrefixedIdRequest(1), parse(new byte[]{0, 42, 0}));
    }

    @Test
    void doublePaddingTest() {
        assertThrows(MalformedPacketException.class, () -> parse(new byte[]{0, 0, 42, 0}));
    }

    @Test
    void voxelMapPaddingTest() throws MalformedPacketException {
        assertEquals(new PrefixedIdRequest(0), parse(new byte[]{0, 0, 0, 42, 0}));
    }

    @Test
    void largePaddingTest() {
        for (int padding = 4; padding < 8; padding++) {
            ByteArrayOutputStream array = new ByteArrayOutputStream();
            for (int i = 0; i < padding; i++) {
                array.write(0);
            }
            array.write(42);
            assertThrows(MalformedPacketException.class, () -> parse(array.toByteArray()));
        }
    }
}

