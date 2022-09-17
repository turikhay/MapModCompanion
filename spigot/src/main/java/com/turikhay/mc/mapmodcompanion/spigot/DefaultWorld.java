package com.turikhay.mc.mapmodcompanion.spigot;

import org.bukkit.World;

import java.util.*;

interface DefaultWorld {
    Optional<World> optional();

    static Empty empty() {
        return new Empty();
    }

    static DefaultWorld detectDefaultWorld(CompanionSpigot plugin) {
        List<World> worlds = plugin.getServer().getWorlds();
        if (worlds.isEmpty()) {
            throw new RuntimeException("world list is empty");
        }
        World defaultWorld = null;
        for (World world : worlds) {
            World.Environment env = world.getEnvironment();
            if (env == World.Environment.NORMAL) {
                if (defaultWorld != null) {
                    // Non-default server configuration
                    plugin.getLogger().severe("Unexpected world: " + world);
                    plugin.getLogger().severe("For every world plugin will now send their unique IDs");
                    return new Empty();
                }
                defaultWorld = world;
            }
        }
        if (defaultWorld == null) {
            plugin.getLogger().severe("Default world not detected");
            plugin.getLogger().severe("For every world plugin will now send their unique IDs");
            return new Empty();
        }
        if (CompanionSpigot.ENABLE_LOGGING) {
            plugin.getLogger().info("Selected default world: " + defaultWorld + " (" + defaultWorld.getUID() + ")");
        }
        return new ByUUID(plugin, defaultWorld.getUID());
    }

    class ByUUID implements DefaultWorld {
        private final CompanionSpigot plugin;
        private final UUID uuid;

        public ByUUID(CompanionSpigot plugin, UUID uuid) {
            this.plugin = plugin;
            this.uuid = uuid;
        }

        @Override
        public Optional<World> optional() {
            World world = plugin.getServer().getWorld(uuid);
            if (world == null) {
                plugin.getLogger().warning("Couldn't find world " + uuid);
                return Optional.empty();
            }
            return Optional.of(world);
        }
    }

    class Empty implements DefaultWorld {
        @Override
        public Optional<World> optional() {
            return Optional.empty();
        }
    }
}
