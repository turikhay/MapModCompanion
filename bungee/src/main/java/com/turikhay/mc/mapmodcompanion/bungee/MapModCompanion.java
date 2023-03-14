package com.turikhay.mc.mapmodcompanion.bungee;

import com.turikhay.mc.mapmodcompanion.*;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.config.ConfigurationProvider;
import net.md_5.bungee.config.YamlConfiguration;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

public class MapModCompanion extends Plugin {

    private final List<Handler.Factory<MapModCompanion>> factories = Arrays.asList(
            new PacketHandler.Factory<>(
                    "world_id.modern",
                    Channels.WORLDID_CHANNEL,
                    PrefixedId.Deserializer.ofModern(),
                    PrefixedId.Serializer.ofAny()
            ),
            new PacketHandler.Factory<>(
                    "world_id.legacy",
                    Channels.WORLDID_LEGACY_CHANNEL,
                    PrefixedId.Deserializer.ofLegacy(),
                    PrefixedId.Serializer.ofAny()
            ),
            new PacketHandler.Factory<>(
                    "xaero.mini_map",
                    Channels.XAERO_MINIMAP_CHANNEL,
                    LevelMapProperties.Deserializer.instance(),
                    LevelMapProperties.Serializer.instance()
            ),
            new PacketHandler.Factory<>(
                    "xaero.world_map",
                    Channels.XAERO_WORLDMAP_CHANNEL,
                    LevelMapProperties.Deserializer.instance(),
                    LevelMapProperties.Serializer.instance()
            )
    );
    private final IdConverter converter = new IdConverter.ConfigBased((path, def) -> getConfig().getInt(path, def));
    private VerboseLogger logger;
    private Configuration configuration;
    private List<Handler> handlers = Collections.emptyList();

    public IdConverter getConverter() {
        return converter;
    }

    public VerboseLogger getVerboseLogger() {
        return logger;
    }

    public Configuration getConfig() {
        return configuration;
    }

    @Override
    public void onLoad() {
        PrefixLogger.INCLUDE_PREFIX = true;
        logger = new VerboseLogger(getLogger());
    }

    @Override
    public void onEnable() {
        try {
            configuration = readConfig();
        } catch (IOException e) {
            throw new RuntimeException("Couldn't read or save configuration", e);
        }
        logger.info("Configuration loaded");

        logger.setVerbose(configuration.getBoolean("verbose", false));
        logger.fine("Verbose logging enabled");

        Handler.initialize(logger, this, factories);
    }

    @Override
    public void onDisable() {
        Handler.cleanUp(logger, handlers);
        handlers = Collections.emptyList();
    }

    private Configuration readConfig() throws IOException {
        Path dataFolder = getDataFolder().toPath();
        Path configFile = dataFolder.resolve("config.yml");
        if (!Files.exists(configFile)) {
            logger.fine("Creating new config file");
            Files.createDirectories(dataFolder);
            try (InputStream in = getResourceAsStream("config_bungee.yml");
                 OutputStream out = Files.newOutputStream(configFile)) {
                byte[] buffer = new byte[1024];
                int read;
                while ((read = in.read(buffer)) >= 0) {
                    out.write(buffer, 0, read);
                }
            }
        } else {
            logger.fine("Reading existing config file");
        }
        try (InputStreamReader reader = new InputStreamReader(Files.newInputStream(configFile))) {
            return ConfigurationProvider.getProvider(YamlConfiguration.class).load(reader);
        }
    }
}
