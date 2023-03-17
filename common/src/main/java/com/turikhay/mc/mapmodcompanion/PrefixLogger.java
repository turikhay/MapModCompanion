package com.turikhay.mc.mapmodcompanion;

import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

public class PrefixLogger extends Logger {
    public static boolean INCLUDE_PREFIX = false;

    private final VerboseLogger parent;
    private final String formattedPrefix;

    public PrefixLogger(VerboseLogger parent, String prefix) {
        super(parent.getName() + " - " + prefix, null);
        this.parent = parent;
        this.formattedPrefix = "[" + prefix + "] ";
        setParent(parent);
        setLevel(Level.ALL);
    }

    @Override
    public boolean isLoggable(Level level) {
        return parent.isVerbose() || super.isLoggable(level);
    }

    @Override
    public void log(LogRecord record) {
        if (parent.isVerbose() && record.getLevel().intValue() < Level.INFO.intValue()) {
            record.setLevel(Level.INFO);
        }
        if (INCLUDE_PREFIX) {
            record.setMessage(this.formattedPrefix + record.getMessage());
        }
        super.log(record);
    }
}
