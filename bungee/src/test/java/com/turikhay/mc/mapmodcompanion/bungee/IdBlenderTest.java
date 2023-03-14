package com.turikhay.mc.mapmodcompanion.bungee;

import com.turikhay.mc.mapmodcompanion.StandardId;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class IdBlenderTest {

    IdBlender blender = IdBlender.DEFAULT;

    @Test
    void test() {
        assertEquals(new StandardId(3600), blender.blend(new StandardId(42), 1337));
    }
}