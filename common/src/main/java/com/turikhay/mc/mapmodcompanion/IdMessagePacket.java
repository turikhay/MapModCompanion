package com.turikhay.mc.mapmodcompanion;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public interface IdMessagePacket<CombinableWith extends IdMessagePacket<?>> {
    CombinableWith combineWith(CombinableWith packet);
    void constructPacket(DataOutputStream out) throws IOException;

    static byte[] bytesPacket(IdMessagePacket<?> id) {
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        try (DataOutputStream out = new DataOutputStream(buffer)) {
            id.constructPacket(out);
        } catch (IOException e) {
            throw new Error(e);
        }
        return buffer.toByteArray();
    }
}
