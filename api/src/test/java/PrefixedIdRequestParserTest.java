import com.turikhay.mc.mapmodcompanion.*;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static com.turikhay.mc.mapmodcompanion.PrefixedIdRequest.parse;
import static org.junit.jupiter.api.Assertions.*;

class PrefixedIdRequestParserTest {

    @Test
    void voxelMapForge1_12_2() throws MalformedPacketException {
        test(new PrefixedIdRequest(1, false), new byte[] { 0, 0 });
    }

    @Test
    void voxelMapForge1_13_2UpTo1_16_3() throws MalformedPacketException {
        test(
                Arrays.asList(
                        ProtocolVersion.MINECRAFT_1_13_2,
                        ProtocolVersion.MINECRAFT_1_14_4,
                        ProtocolVersion.MINECRAFT_1_15_2,
                        ProtocolVersion.MINECRAFT_1_16_3
                ),
                new PrefixedIdRequest(1, false),
                new byte[] { 0, 42, 0 }
        );
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

    private void test(List<Integer> protocolVersions, PrefixedIdRequest expected, byte[] data) throws MalformedPacketException {
        for (Integer protocolVersion : protocolVersions) {
            assertEquals(expected, parse(data, protocolVersion), "protocolVersion: " + protocolVersion.toString());
        }
    }

    private void test(PrefixedIdRequest expected, byte[] data) throws MalformedPacketException {
        assertEquals(expected, parse(data, null));
    }

    @Test
    void zeroTest() {
        assertThrows(MalformedPacketException.class, () -> parse(new byte[]{ 0 }, null));
    }
}
