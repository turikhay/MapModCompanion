package com.turikhay.mc.mapmodcompanion.spigot;

import com.turikhay.mc.mapmodcompanion.IdLookup;
import com.turikhay.mc.mapmodcompanion.PrefixLogger;
import com.turikhay.mc.mapmodcompanion.VerboseLogger;
import org.bukkit.World;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;

public interface IdRegistry {
    int getId(World world);

    class CacheableRegistry implements IdRegistry {
        private final Map<String, Integer> cache = new ConcurrentHashMap<>();
        private final IdRegistry delegate;

        public CacheableRegistry(IdRegistry delegate) {
            this.delegate = delegate;
        }

        @Override
        public int getId(World world) {
            return cache.computeIfAbsent(world.getName(), s -> delegate.getId(world));
        }

        @Override
        public String toString() {
            return "CacheableRegistry{" +
                    "delegate=" + delegate +
                    '}';
        }
    }

    class ConvertingRegistry implements IdRegistry {
        private final Logger logger;
        private final IdLookup lookup;
        private final IdRegistry delegate;

        public ConvertingRegistry(VerboseLogger parent, IdLookup lookup, IdRegistry delegate) {
            this.logger = new PrefixLogger(parent, ConvertingRegistry.class.getSimpleName());
            this.lookup = lookup;
            this.delegate = delegate;
        }

        @Override
        public int getId(World world) {
            Optional<Integer> byName = lookup.findMatch(world.getName());
            if (byName.isPresent()) {
                logger.fine("Found override: " + world.getName() + " -> " + byName.get());
                return byName.get();
            }
            int processedId = delegate.getId(world);
            Optional<Integer> byId = lookup.findMatch(processedId);
            if (byId.isPresent()) {
                logger.fine("Found override: " + processedId + " (" + world.getName() + ") -> " + byId.get());
                return byId.get();
            }
            return processedId;
        }

        @Override
        public String toString() {
            return "ConvertingRegistry{" +
                    "delegate=" + delegate +
                    '}';
        }
    }

    class ConstantRegistry implements IdRegistry {
        private final int id;

        public ConstantRegistry(int id) {
            this.id = id;
        }

        @Override
        public int getId(World world) {
            return id;
        }

        @Override
        public String toString() {
            return "ConstantRegistry{" +
                    "id=" + id +
                    '}';
        }
    }

    class DynamicUUIDRegistry implements IdRegistry {
        @Override
        public int getId(World world) {
            return world.getUID().hashCode();
        }
    }
}
