package com.turikhay.mc.mapmodcompanion;

import javax.annotation.Nullable;

public class MalformedPacketException extends LightweightException {
    public MalformedPacketException(String message, @Nullable Throwable cause) {
        super(message, cause);
    }

    public MalformedPacketException(String message) {
        super(message);
    }
}
