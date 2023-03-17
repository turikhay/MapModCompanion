package com.turikhay.mc.mapmodcompanion;

import javax.annotation.Nullable;

public class LightweightException extends Exception {
    public LightweightException(String message, @Nullable Throwable cause) {
        super(message, cause, false, false);
    }

    public LightweightException(String message) {
        this(message, null);
    }
}
