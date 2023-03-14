package com.turikhay.mc.mapmodcompanion.spigot;

import com.turikhay.mc.mapmodcompanion.PrefixedId;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class PrefixedIdRequestTest {

    @Test
    void constructIdTest() {
        assertEquals(new PrefixedId(0, 1337), new PrefixedIdRequest(0).constructId(1337));
        assertEquals(new PrefixedId(1, 1337), new PrefixedIdRequest(1).constructId(1337));
        assertEquals(new PrefixedId(3, 1337), new PrefixedIdRequest(3).constructId(1337));
    }
}
