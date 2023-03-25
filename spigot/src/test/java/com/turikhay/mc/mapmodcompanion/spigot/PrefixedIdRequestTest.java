package com.turikhay.mc.mapmodcompanion.spigot;

import com.turikhay.mc.mapmodcompanion.PrefixedId;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class PrefixedIdRequestTest {

    @Test
    void constructIdTest() {
        assertEquals(new PrefixedId(0, true, 1337), new PrefixedIdRequest(0, true).constructId(1337));
        assertEquals(new PrefixedId(1, true, 1337), new PrefixedIdRequest(1, true).constructId(1337));
        assertEquals(new PrefixedId(3, true, 1337), new PrefixedIdRequest(3, true).constructId(1337));
        assertEquals(new PrefixedId(4, false, 1337), new PrefixedIdRequest(4, false).constructId(1337));
    }
}
