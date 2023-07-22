package com.turikhay.mc.mapmodcompanion.spigot;

import com.turikhay.mc.mapmodcompanion.*;
import org.bstats.bukkit.Metrics;
import org.bukkit.World;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.plugin.messaging.PluginMessageListener;

import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ScheduledExecutorService;

public class MapModCompanion extends JavaPlugin {
    private static final int BSTATS_ID = 16539;

    private final List<Handler.Factory<MapModCompanion>> factories = Arrays.asList(
            new XaeroHandler.Factory(
                    "xaero.mini_map",
                    Channels.XAERO_MINIMAP_CHANNEL
            ),
            new XaeroHandler.Factory(
                    "xaero.world_map",
                    Channels.XAERO_WORLDMAP_CHANNEL
            ),
            new LevelIdHandler.Factory(
                    "world_id.modern",
                    Channels.WORLDID_CHANNEL,
                    false
            ),
            new LevelIdHandler.Factory(
                    "world_id.legacy",
                    Channels.WORLDID_LEGACY_CHANNEL,
                    true
            )
    );

    private VerboseLogger logger;
    private ScheduledExecutorService fileChangeWatchdogScheduler;
    private IdRegistry registry;
    private @Nullable ProtocolLib protocolLib;
    private List<Handler> handlers = Collections.emptyList();
    private FileChangeWatchdog fileChangeWatchdog;

    public VerboseLogger getVerboseLogger() {
        return logger;
    }

    public IdRegistry getRegistry() {
        return registry;
    }

    public Optional<ProtocolLib> getProtocolLib() {
        return Optional.ofNullable(protocolLib);
    }

    @Override
    public void onLoad() {
        logger = new VerboseLogger(getLogger());
    }

    @Override
    public void onEnable() {
        fileChangeWatchdogScheduler = FileChangeWatchdog.createScheduler();
        new Metrics(this, BSTATS_ID);
        saveDefaultConfig();
        load();
    }

    @Override
    public void onDisable() {
        unload();
        fileChangeWatchdogScheduler.shutdown();
    }

    private void load() {
        logger.fine("Loading");

        reloadConfig();

        logger.setVerbose(getConfig().getBoolean("verbose", false));
        logger.fine("Verbose logging enabled");

        registry = initRegistry();
        protocolLib = Handler.initialize(logger, this, new ProtocolLib.Factory());
        handlers = Handler.initialize(logger, this, factories);
        fileChangeWatchdog = new FileChangeWatchdog(
                logger,
                fileChangeWatchdogScheduler,
                getDataFolder().toPath().resolve("config.yml"),
                () -> getServer().getScheduler().scheduleSyncDelayedTask(this, this::reload)
        );
        fileChangeWatchdog.start();
    }

    private void unload() {
        logger.fine("Unloading");
        Handler.cleanUp(logger, handlers);
        handlers = Collections.emptyList();
        getProtocolLib().ifPresent(Handler::cleanUp);
        fileChangeWatchdog.cleanUp();
    }

    private void reload() {
        unload();
        load();
    }

    private IdRegistry initRegistry() {
        World world = null;
        if (getConfig().getBoolean("preferDefaultWorld", true)) {
            world = detectDefaultWorld();
        }
        IdRegistry registry;
        if (world == null) {
            logger.severe("For every world plugin will now send their unique IDs");
            registry = new IdRegistry.DynamicUUIDRegistry();
        } else {
            int id = world.getUID().hashCode();
            registry = new IdRegistry.ConstantRegistry(id);
        }
        registry = new IdRegistry.ConvertingRegistry(
                logger,
                new IdLookup.ConfigBased((path, def) -> getConfig().getInt(path, def)),
                registry
        );
        return new IdRegistry.CacheableRegistry(registry);
    }

    private @Nullable World detectDefaultWorld() {
        List<World> worlds = getServer().getWorlds();
        if (worlds.isEmpty()) {
            throw new RuntimeException("world list is empty");
        }
        World defaultWorld = null;
        for (World world : worlds) {
            World.Environment env = world.getEnvironment();
            if (env == World.Environment.NORMAL) {
                if (defaultWorld != null) {
                    // Non-default server configuration
                    logger.severe("Unexpected world: " + world);
                    return null;
                }
                defaultWorld = world;
            }
        }
        if (defaultWorld == null) {
            logger.severe("Default world not detected");
            return null;
        }
        logger.fine("Selected default world: " + defaultWorld + " (" + defaultWorld.getUID() + ")");
        return defaultWorld;
    }

    void registerOutgoingChannel(String channelName) throws InitializationException {
        logger.fine("Registering outgoing plugin channel: " + channelName);
        try {
            getServer().getMessenger().registerOutgoingPluginChannel(this, channelName);
        } catch (Exception e) {
            throw new InitializationException("couldn't register outgoing plugin channel: " + channelName, e);
        }
    }

    void unregisterOutgoingChannel(String channelName) {
        logger.fine("Unregistering outgoing plugin channel: " + channelName);
        getServer().getMessenger().unregisterOutgoingPluginChannel(this, channelName);
    }

    void registerIncomingChannel(String channelName, boolean legacyChannel, PluginMessageListener listener) throws InitializationException {
        logger.fine("Registering incoming plugin channel: " + channelName);
        try {
            getServer().getMessenger().registerIncomingPluginChannel(this, channelName, listener);
        } catch (Exception e) {
            String message = "couldn't register incoming plugin channel: " + channelName;
            if (legacyChannel) {
                message += " (can be safely ignored on 1.13+)";
            }
            throw new InitializationException(message, e);
        }
    }

    void registerIncomingChannel(String channelName, PluginMessageListener listener) throws InitializationException {
        registerIncomingChannel(channelName, false, listener);
    }

    void unregisterIncomingChannel(String channelName, PluginMessageListener listener) {
        logger.fine("Unregistering incoming plugin channel: " + channelName);
        getServer().getMessenger().unregisterIncomingPluginChannel(this, channelName, listener);
    }

    void checkEnabled(String configPath) throws InitializationException {
        if (!getConfig().getBoolean(configPath + ".enabled", true)) {
            throw new InitializationException("disabled in the config");
        }
    }
}
