import com.turikhay.mc.mapmodcompanion.*;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PrefixedIdPacketDeserializerTest {

    PrefixedId.Deserializer deserializer = PrefixedId.Deserializer.instance();

    @Test
    void voxelMapForge1_12_2UpTo1_16_3() throws MalformedPacketException {
        test(new PrefixedId(1, false, 1), new byte[] { 0, 1, 49 });
    }

    @Test
    void standardFormTest() throws MalformedPacketException {
        // JourneyMap 1.16.5, VoxelMap 1.19.2+
        test(new PrefixedId(1, true, 1), new byte[] { 0, 42, 1, 49 });
    }

    @Test
    void voxelMapFixTest() throws MalformedPacketException {
        // VoxelMap LiteLoader 1.8.9 - 1.12.2, VoxelMap Fabric 1.14.4 - 1.19.x
        test(new PrefixedId(0, true, 1), new byte[] { 42, 1, 49 });
    }

    private void test(PrefixedId expected, byte[] data) throws MalformedPacketException {
        assertEquals(expected, deserializer.deserialize(data));
    }
}
