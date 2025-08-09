import com.turikhay.mc.mapmodcompanion.FileChangeWatchdog;
import com.turikhay.mc.mapmodcompanion.ILogger;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Method;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.assertEquals;

class FileChangeWatchdogTest {

    @Test
    void callbackRunsOnceWhenFileChanges() throws Exception {
        Path file = Files.createTempFile("watchdog", ".tmp");
        ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
        try {
            AtomicInteger counter = new AtomicInteger();
            FileChangeWatchdog watchdog = new FileChangeWatchdog(
                    ILogger.ofJava(java.util.logging.Logger.getAnonymousLogger()),
                    scheduler,
                    file,
                    counter::incrementAndGet
            );
            Method tick = FileChangeWatchdog.class.getDeclaredMethod("tick");
            tick.setAccessible(true);

            tick.invoke(watchdog); // initialize lastTime

            Thread.sleep(1000);
            Files.writeString(file, "a");

            tick.invoke(watchdog); // should trigger callback
            tick.invoke(watchdog); // should not trigger again

            assertEquals(1, counter.get());
        } finally {
            scheduler.shutdownNow();
            Files.deleteIfExists(file);
        }
    }
}
