package com.turikhay.mc.mapmodcompanion.fabric;

import com.turikhay.mc.mapmodcompanion.IdRegistry;
import com.turikhay.mc.mapmodcompanion.WorldInfo;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;

import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.UUID;
import java.util.function.Consumer;

public class MapModCompanion implements ModInitializer {
    public static final String MOD_ID = "mapmodcompanion";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    public static final boolean
            xaeroMinimap = enableIfMissing("xaero.minimap.XaeroMinimap"),
            xaeroWorldMap = enableIfMissing("xaero.map.WorldMap");

    private static MapModCompanion INSTANCE;

    private IdRegistry registry;

    @Override
    public void onInitialize() {
        LevelMapPropertiesPayload.register();
        registry = new IdRegistry.DynamicUUIDRegistry();
        INSTANCE = this;
        // TODO add overrides support
        // then
        // TODO make pre-1.20.5 support
        // TODO adapt tests_e2e to also test fabric servers
    }

    public void sendLevelData(ServerPlayerEntity player, ServerWorld world) {
        int id = registry.getId(toWorldInfo(world));
        if (xaeroMinimap) {
            ServerPlayNetworking.send(player, new LevelMapPropertiesPayload.Minimap(id));
        }
        if (xaeroWorldMap) {
            ServerPlayNetworking.send(player, new LevelMapPropertiesPayload.WorldMap(id));
        }
    }

    public static void run(String taskDescription, Consumer<MapModCompanion> task) {
        MapModCompanion instance = INSTANCE;
        if (instance == null) {
            throw new IllegalStateException("MapModCompanion has not been initialized");
        }
        try {
            task.accept(instance);
        } catch (Exception e) {
            LOGGER.error("Error running the task: {}", taskDescription, e);
        }
    }

    public static WorldInfo toWorldInfo(ServerWorld world) {
        UUID uuid;
        try {
            uuid = WorldUID.getOrCreate(world);
        } catch (IOException e) {
            throw new RuntimeException("Failed to read or write CraftBukkit-style uid.dat file", e);
        }
        return new WorldInfo(uuid, world.worldProperties.getLevelName());
    }

    private static boolean enableIfMissing(String className) {
        boolean enable = false;
        try {
            Class.forName(className);
        } catch (ClassNotFoundException e) {
            enable = true;
        }
        if (!enable) {
            LOGGER.warn("Found {} in the classpath", className);
        }
        return enable;
    }
}
