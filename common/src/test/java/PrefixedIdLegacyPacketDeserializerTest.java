import com.turikhay.mc.mapmodcompanion.MalformedPacketException;
import com.turikhay.mc.mapmodcompanion.PrefixedId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class PrefixedIdLegacyPacketDeserializerTest {

    PrefixedId.Deserializer reader;

    @BeforeEach
    void setUp() {
        reader = new PrefixedId.Deserializer(true);
    }

    @Test
    void readRegularTest() throws MalformedPacketException {
        assertEquals(new PrefixedId(0, 1), reader.deserialize(new byte[]{ 0, 1, 49 }));
    }

    @Test
    void readMalformedTest() {
        assertThrows(MalformedPacketException.class, () -> reader.deserialize(new byte[]{ 13, 37 }));
    }

    @Test
    void readModernTest() {
        assertThrows(MalformedPacketException.class, () -> reader.deserialize(new byte[]{ 0, 0, 0, 42, 1, 49 }));
    }

    @Test
    void readTextIdTest() {
        assertThrows(MalformedPacketException.class, () -> reader.deserialize(new byte[]{ 0, 1, 65 })); // -> "A"
    }
}
