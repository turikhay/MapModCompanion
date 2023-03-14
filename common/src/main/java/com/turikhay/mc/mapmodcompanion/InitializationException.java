package com.turikhay.mc.mapmodcompanion;

import javax.annotation.Nullable;

public class InitializationException extends LightweightException {
    public InitializationException(String message, @Nullable Throwable cause) {
        super(message, cause);
    }

    public InitializationException(String message) {
        super(message);
    }
}
