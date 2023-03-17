import com.turikhay.mc.mapmodcompanion.LevelMapProperties;
import com.turikhay.mc.mapmodcompanion.MalformedPacketException;
import com.turikhay.mc.mapmodcompanion.StandardId;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class LevelMapPropertiesPacketDeserializerTest {

    final LevelMapProperties.Deserializer reader = LevelMapProperties.Deserializer.instance();

    @Test
    void regularTest() throws MalformedPacketException {
        assertEquals(new StandardId(1), reader.deserialize(new byte[]{ 0, 0, 0, 0, 1 }));
        assertEquals(new StandardId(1337), reader.deserialize(new byte[]{ 0, 0, 0, 5, 57 }));
    }

    @Test
    void noMarkerTest() {
        assertThrows(MalformedPacketException.class, () -> reader.deserialize(new byte[]{ 5, 57 }));
    }

    @Test
    void emptyTest() {
        assertThrows(MalformedPacketException.class, () -> reader.deserialize(new byte[]{}));
    }
}
