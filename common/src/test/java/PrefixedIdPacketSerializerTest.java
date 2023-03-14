import com.turikhay.mc.mapmodcompanion.PrefixedId;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;

class PrefixedIdPacketSerializerTest {

    final PrefixedId.Serializer writer = PrefixedId.Serializer.ofAny();

    @Test
    void prefixLengthTest() {
        for (int prefixLength = 0; prefixLength < 8; prefixLength++) {
            ByteArrayOutputStream array = new ByteArrayOutputStream();
            for (int i = 0; i < prefixLength; i++) {
                array.write(0);
            }
            array.write(42);
            array.write(1);
            array.write(49);
            assertArrayEquals(array.toByteArray(), writer.serialize(new PrefixedId(prefixLength, 1)));
        }
    }
}
