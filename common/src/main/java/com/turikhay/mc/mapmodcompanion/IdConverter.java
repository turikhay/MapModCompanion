package com.turikhay.mc.mapmodcompanion;

import java.util.Optional;

public interface IdConverter {
    Optional<Integer> findMatch(int id);

    class ConfigBased implements IdConverter {
        private final ConfigAccessor accessor;

        public ConfigBased(ConfigAccessor accessor) {
            this.accessor = accessor;
        }

        @Override
        public Optional<Integer> findMatch(int id) {
            int value = accessor.getInt("overrides." + id, Integer.MIN_VALUE);
            return value == Integer.MIN_VALUE ? Optional.empty() : Optional.of(value);
        }

        public interface ConfigAccessor {
            int getInt(String path, int def);
        }
    }
}
