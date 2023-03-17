package com.turikhay.mc.mapmodcompanion;

import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

public class VerboseLogger extends Logger {
    private boolean verbose;

    public VerboseLogger(Logger logger) {
        super(logger.getName(), null);
        setParent(logger);
    }

    public boolean isVerbose() {
        return verbose;
    }

    public void setVerbose(boolean verbose) {
        this.verbose = verbose;
    }

    @Override
    public boolean isLoggable(Level level) {
        return verbose || super.isLoggable(level);
    }

    @Override
    public void log(LogRecord logRecord) {
        if (verbose && logRecord.getLevel().intValue() < Level.INFO.intValue()) {
            logRecord.setLevel(Level.INFO);
        }
        super.log(logRecord);
    }
}
