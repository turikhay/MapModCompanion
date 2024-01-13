package com.turikhay.mc.mapmodcompanion;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

public class DaemonThreadFactory implements ThreadFactory {
    private final AtomicInteger counter = new AtomicInteger();
    private final ILogger logger;
    private final String name;
    private final Thread.UncaughtExceptionHandler handler = new Thread.UncaughtExceptionHandler() {
        @Override
        public void uncaughtException(Thread thread, Throwable throwable) {
            logger.error("Error executing the task inside " + thread.getName(), throwable);
        }
    };

    public DaemonThreadFactory(ILogger logger, String name) {
        this.logger = logger;
        this.name = name;
    }

    public DaemonThreadFactory(ILogger logger, Class<?> cl) {
        this(logger, cl.getSimpleName());
    }

    @Override
    public Thread newThread(Runnable runnable) {
        Thread t = new Thread(runnable, computeNextName());
        t.setDaemon(true);
        t.setUncaughtExceptionHandler(handler);
        return t;
    }

    private String computeNextName() {
        int i = counter.getAndIncrement();
        if (i == 0) {
            return name;
        }
        return name + "#" + i;
    }
}
