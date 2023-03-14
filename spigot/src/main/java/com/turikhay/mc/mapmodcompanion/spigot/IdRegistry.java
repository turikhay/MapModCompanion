package com.turikhay.mc.mapmodcompanion.spigot;

import com.turikhay.mc.mapmodcompanion.IdConverter;
import org.bukkit.World;

import java.util.HashMap;
import java.util.Map;

public interface IdRegistry {
    int getId(World world);

    class CacheableRegistry implements IdRegistry {
        private final Map<String, Integer> cache = new HashMap<>();
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
        private final IdConverter converter;
        private final IdRegistry delegate;

        public ConvertingRegistry(IdConverter converter, IdRegistry delegate) {
            this.converter = converter;
            this.delegate = delegate;
        }

        @Override
        public int getId(World world) {
            int id = delegate.getId(world);
            return converter.findMatch(id).orElse(id);
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
