package com.turikhay.mc.mapmodcompanion.spigot;

import com.turikhay.mc.mapmodcompanion.DaemonThreadFactory;
import com.turikhay.mc.mapmodcompanion.ILogger;

import java.util.concurrent.ScheduledThreadPoolExecutor;

public class SingleThreadScheduler implements PluginScheduler {

    private final ScheduledThreadPoolExecutor service;

    public SingleThreadScheduler(ILogger logger) {
        this.service = new ScheduledThreadPoolExecutor(
                1,
                new DaemonThreadFactory(logger, SingleThreadScheduler.class)
        );
        service.setExecuteExistingDelayedTasksAfterShutdownPolicy(false);
    }

    @Override
    public void cleanUp() {
        service.shutdown();
    }

    @Override
    public void schedule(Runnable r) {
        service.submit(r);
    }

    @Override
    public String toString() {
        return "SingleThreadScheduler{" +
                "service=" + service +
                '}';
    }
}
