package com.turikhay.mc.mapmodcompanion;

import java.util.logging.Level;

public interface ILogger {
    void fine(String message);

    void info(String message);

    void warn(String message, Throwable t);

    void error(String message, Throwable t);

    static ILogger ofJava(java.util.logging.Logger logger) {
        return new ILogger() {
            @Override
            public void fine(String message) {
                logger.fine(message);
            }

            @Override
            public void info(String message) {
                logger.info(message);
            }

            @Override
            public void warn(String message, Throwable t) {
                logger.log(Level.WARNING, message, t);
            }

            @Override
            public void error(String message, Throwable t) {
                logger.log(Level.SEVERE, message, t);
            }
        };
    }
}
