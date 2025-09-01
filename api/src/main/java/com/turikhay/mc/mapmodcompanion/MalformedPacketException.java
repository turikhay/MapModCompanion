package com.turikhay.mc.mapmodcompanion;

import javax.annotation.Nullable;

/**
 * Indicates that a network packet could not be parsed.
 */
public class MalformedPacketException extends LightweightException {

    /**
     * Creates a new exception with the provided message and optional cause.
     *
     * @param message human readable description of the problem
     * @param cause   underlying cause or {@code null}
     */
    public MalformedPacketException(String message, @Nullable Throwable cause) {
        super(message, cause);
    }

    /**
     * Creates a new exception with the provided message.
     *
     * @param message human readable description of the problem
     */
    public MalformedPacketException(String message) {
        super(message);
    }
}
