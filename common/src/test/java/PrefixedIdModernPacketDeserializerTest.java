import com.turikhay.mc.mapmodcompanion.MalformedPacketException;
import com.turikhay.mc.mapmodcompanion.PrefixedId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class PrefixedIdModernPacketDeserializerTest {

    PrefixedId.Deserializer reader;

    @BeforeEach
    void setUp() {
        reader = new PrefixedId.Deserializer(false);
    }

    @Test
    void readRegularTest() throws MalformedPacketException {
        assertEquals(new PrefixedId(1, 1), reader.deserialize(new byte[]{ 0, 42, 1, 49 }));
    }

    @Test
    void readPrefixedTest() throws MalformedPacketException {
        for (int prefixLength = 0; prefixLength < 8; prefixLength++) {
            ByteArrayOutputStream array = new ByteArrayOutputStream();
            for (int i = 0; i < prefixLength; i++) {
                array.write(0);
            }
            array.write(42);
            array.write(1);
            array.write(49);
            assertEquals(new PrefixedId(prefixLength, 1), reader.deserialize(array.toByteArray()));
        }
    }

    @Test
    void readMalformedTest() {
        assertThrows(MalformedPacketException.class, () -> reader.deserialize(new byte[]{ 0, 13, 37 }));
    }

    @Test
    void readLegacyTest() {
        assertThrows(MalformedPacketException.class, () -> reader.deserialize(new byte[]{ 0, 1, 49 }));
    }

    @Test
    void readTextIdTest() {
        assertThrows(MalformedPacketException.class, () -> reader.deserialize(new byte[]{ 0, 42, 1, 65 })); // -> "A"
    }
}
