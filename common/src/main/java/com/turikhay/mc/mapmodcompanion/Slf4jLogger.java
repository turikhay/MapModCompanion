package com.turikhay.mc.mapmodcompanion;

import org.slf4j.Logger;

public class Slf4jLogger implements ILogger {
    private final Logger logger;

    public Slf4jLogger(Logger logger) {
        this.logger = logger;
    }

    @Override
    public void fine(String message) {
        logger.debug("{}", message);
    }

    @Override
    public void info(String message) {
        logger.info("{}", message);
    }

    @Override
    public void warn(String message, Throwable t) {
        logger.warn("{}", message, t);
    }

    @Override
    public void error(String message, Throwable t) {
        logger.error("{}", message, t);
    }
}
