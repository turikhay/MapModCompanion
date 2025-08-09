package com.turikhay.mc.mapmodcompanion;

import java.util.Optional;

public interface IdLookup {
    Optional<Integer> findMatch(String id);

    default Optional<Integer> findMatch(int id) {
        return findMatch(String.valueOf(id));
    }

    class ConfigBased implements IdLookup {
        public static final String PATH_PREFIX = "overrides.";

        private final ConfigAccessor accessor;

        public ConfigBased(ConfigAccessor accessor) {
            this.accessor = accessor;
        }

        @Override
        public Optional<Integer> findMatch(String id) {
            int value = accessor.getInt(PATH_PREFIX + id, Integer.MIN_VALUE);
            return value == Integer.MIN_VALUE ? Optional.empty() : Optional.of(value);
        }

        public interface ConfigAccessor {
            int getInt(String path, int def);
        }
    }
}
