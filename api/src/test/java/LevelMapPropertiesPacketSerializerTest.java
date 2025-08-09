import com.turikhay.mc.mapmodcompanion.LevelMapProperties;
import com.turikhay.mc.mapmodcompanion.StandardId;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class LevelMapPropertiesPacketSerializerTest {

    final LevelMapProperties.Serializer writer = LevelMapProperties.Serializer.instance();

    @Test
    void regularTest() {
        assertArrayEquals(new byte[]{0, 0, 0, 0, 1}, writer.serialize(new StandardId(1)));
        assertArrayEquals(new byte[]{0, 0, 0, 5, 57}, writer.serialize(new StandardId(1337)));
    }
}
