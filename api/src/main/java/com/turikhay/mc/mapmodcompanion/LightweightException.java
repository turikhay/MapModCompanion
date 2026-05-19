package com.turikhay.mc.mapmodcompanion;

import javax.annotation.Nullable;

/**
 * A lightweight {@link Exception} implementation that does not populate the
 * stack trace.
 * <p>
 * This is useful when the exception is part of the regular control flow and
 * the overhead of capturing a stack trace is undesirable.
 */
public class LightweightException extends Exception {

    /**
     * Creates a new exception with the provided message and optional cause
     * without filling in the stack trace.
     *
     * @param message human readable description of the problem
     * @param cause   underlying cause or {@code null}
     */
    public LightweightException(String message, @Nullable Throwable cause) {
        super(message, cause, false, false);
    }

    /**
     * Creates a new exception with the provided message.
     *
     * @param message human readable description of the problem
     */
    public LightweightException(String message) {
        this(message, null);
    }
}
