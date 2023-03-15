package com.turikhay.mc.mapmodcompanion;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.FileTime;
import java.util.concurrent.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class FileChangeWatchdog implements Disposable {
    private final Logger logger;
    private final ScheduledExecutorService scheduler;
    private final Path path;
    private final Runnable callback;

    public FileChangeWatchdog(VerboseLogger parent, ScheduledExecutorService scheduler, Path path, Runnable callback) {
        this.logger = new PrefixLogger(parent, FileChangeWatchdog.class.getSimpleName());
        this.scheduler = scheduler;
        this.path = path;
        this.callback = callback;
    }

    private ScheduledFuture<?> task;

    public void start() {
        logger.fine("Starting watchdog task");
        task = scheduler.scheduleWithFixedDelay(this::tick, 5L, 5L, TimeUnit.SECONDS);
    }

    private FileTime lastTime;

    private void tick() {
        FileTime time;
        try {
            time = Files.getLastModifiedTime(path);
        } catch (IOException e) {
            logger.log(Level.WARNING, "Couldn't poll last modification time of " + path, e);
            return;
        }
        if (lastTime == null) {
            lastTime = time;
            return;
        }
        if (!time.equals(lastTime)) {
            logger.info("File has been changed: " + path);
            try {
                callback.run();
            } catch (RuntimeException e) {
                logger.log(Level.SEVERE, "File change callback error", e);
            }
        }
    }

    @Override
    public void cleanUp() {
        logger.fine("Cleaning up the task");
        task.cancel(true);
    }

    public static ScheduledExecutorService createScheduler() {
        return Executors.newSingleThreadScheduledExecutor(r ->
                new Thread(r, "MapModCompanion-" + FileChangeWatchdog.class.getSimpleName())
        );
    }
}
