package com.turikhay.mc.mapmodcompanion.velocity;

import com.google.inject.Inject;
import com.moandjiezana.toml.Toml;
import com.turikhay.mc.mapmodcompanion.*;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
import com.velocitypowered.api.event.proxy.ProxyShutdownEvent;
import com.velocitypowered.api.plugin.Plugin;
import com.velocitypowered.api.plugin.annotation.DataDirectory;
import com.velocitypowered.api.proxy.ProxyServer;
import org.bstats.velocity.Metrics;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ScheduledExecutorService;

import static com.turikhay.mc.mapmodcompanion.velocity.Channels.*;

@Plugin(
        id = "mapmodcompanion",
        name = "MapModCompanion",
        version = "to be filled by the build script",
        url = "https://github.com/turikhay/MapModCompanion",
        authors = {"turikhay"}
)
public class MapModCompanion {
    private static final int BSTATS_ID = 17977;

    private final List<Handler.Factory<MapModCompanion>> factories = List.of(
            new MessageHandler.Factory<>(
                    "world_id.modern",
                    WORLD_ID,
                    PrefixedId.Deserializer.instance(),
                    PrefixedId.Serializer.instance()
            ),
            new MessageHandler.Factory<>(
                    "world_id.legacy",
                    WORLD_ID_LEGACY,
                    PrefixedId.Deserializer.instance(),
                    PrefixedId.Serializer.instance()
            ),
            new MessageHandler.Factory<>(
                    "xaero.mini_map",
                    XAERO_MINIMAP,
                    LevelMapProperties.Deserializer.instance(),
                    LevelMapProperties.Serializer.instance()
            ),
            new MessageHandler.Factory<>(
                    "xaero.world_map",
                    XAERO_WORLDMAP,
                    LevelMapProperties.Deserializer.instance(),
                    LevelMapProperties.Serializer.instance()
            )
    );

    private final ProxyServer server;
    private final Logger logger;
    private final Path dataDirectory;
    private final Metrics.Factory metricsFactory;

    private final IdLookup converter = new IdLookup.ConfigBased((path, def) ->
            getConfig().getLong(path, (long) def).intValue()
    );

    private ScheduledExecutorService fileChangeWatchdogScheduler;
    private Toml config;
    private List<Handler> handlers;
    private FileChangeWatchdog fileChangeWatchdog;

    @Inject
    public MapModCompanion(ProxyServer server, Logger logger,
                           @DataDirectory Path dataDirectory,
                           Metrics.Factory metricsFactory
    ) {
        this.server = server;
        this.logger = logger;
        this.dataDirectory = dataDirectory;
        this.metricsFactory = metricsFactory;
    }

    public Toml getConfig() {
        return this.config;
    }

    public ProxyServer getServer() {
        return server;
    }

    public IdLookup getConverter() {
        return converter;
    }

    @Subscribe
    public void onProxyInitialization(ProxyInitializeEvent event) {
        fileChangeWatchdogScheduler = FileChangeWatchdog.createScheduler(ofSlf4j(logger));
        metricsFactory.make(this, BSTATS_ID);
        load();
    }

    @Subscribe
    public void onProxyShutdown(ProxyShutdownEvent event) {
        fileChangeWatchdogScheduler.shutdown();
        unload();
    }

    private void load() {
        logger.debug("Loading");

        boolean reload = config != null;
        try {
            this.config = this.reloadConfig();
        } catch (IOException e) {
            throw new RuntimeException("error loading config file", e);
        }
        logger.info("Configuration has been " + (reload ? "reloaded" : "loaded"));

        handlers = Handler.initialize(ofSlf4j(logger), this, factories);

        fileChangeWatchdog = new FileChangeWatchdog(
                ofSlf4j(LoggerFactory.getLogger(FileChangeWatchdog.class)),
                fileChangeWatchdogScheduler,
                getConfigFile(),
                this::reload
        );
        fileChangeWatchdog.start();
    }

    private void unload() {
        logger.debug("Unloading");
        fileChangeWatchdog.cleanUp();
        Handler.cleanUp(ofSlf4j(logger), handlers);
        handlers = null;
    }

    private void reload() {
        unload();
        load();
    }

    private Toml reloadConfig() throws IOException {
        logger.debug("Creating new config file");
        var configFile = getConfigFile();
        if (!Files.exists(configFile)) {
            Files.createDirectories(dataDirectory);
            try (InputStream in = getClass().getResourceAsStream(CONFIG_PATH);
                 OutputStream out = Files.newOutputStream(configFile)
            ) {
                Objects.requireNonNull(in, "missing " + CONFIG_PATH).transferTo(out);
            }
        }
        var config = new Toml();
        try {
            config.read(configFile.toFile());
        } catch (RuntimeException e) {
            throw new IOException(e);
        }
        return config;
    }

    private Path getConfigFile() {
        return dataDirectory.resolve("config.toml");
    }

    private static final String CONFIG_PATH = "/config_velocity.toml";

    private static ILogger ofSlf4j(Logger logger) {
        return new ILogger() {
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
        };
    }
}
