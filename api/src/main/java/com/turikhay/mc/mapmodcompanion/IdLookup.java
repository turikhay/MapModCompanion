package com.turikhay.mc.mapmodcompanion;

import java.util.Optional;

/**
 * Performs lookup of world identifiers.
 * <p>
 * Implementations may resolve identifiers from configuration files, network
 * responses or any other data source.
 */
public interface IdLookup {

    /**
     * Attempts to find a numeric world identifier for the supplied string id
     * (typically, world UUID or its name).
     *
     * @param id textual representation of the world id
     * @return a numeric identifier if a mapping exists
     */
    Optional<Integer> findMatch(String id);

    /**
     * Convenience overload accepting an integer id.
     *
     * @param id numeric id
     * @return a numeric identifier if a mapping exists
     */
    default Optional<Integer> findMatch(int id) {
        return findMatch(String.valueOf(id));
    }

    /**
     * Configuration-backed implementation that resolves ids from an external
     * configuration source.
     * <p>
     * Mappings are read from keys starting with {@link #PATH_PREFIX} followed
     * by the textual world id.
     */
    class ConfigBased implements IdLookup {
        /** Prefix within the configuration for override values. */
        public static final String PATH_PREFIX = "overrides.";

        private final ConfigAccessor accessor;

        /**
         * Creates a new lookup using the supplied accessor.
         *
         * @param accessor callback used to read configuration values
         */
        public ConfigBased(ConfigAccessor accessor) {
            this.accessor = accessor;
        }

        /** {@inheritDoc} */
        @Override
        public Optional<Integer> findMatch(String id) {
            int value = accessor.getInt(PATH_PREFIX + id, Integer.MIN_VALUE);
            return value == Integer.MIN_VALUE ? Optional.empty() : Optional.of(value);
        }

        /**
         * Adapter used to access configuration values.
         */
        public interface ConfigAccessor {
            /**
             * Reads an integer from configuration or returns the default value
             * if the path is not present.
             *
             * @param path configuration path
             * @param def  default value
             * @return configured value or {@code def}
             */
            int getInt(String path, int def);
        }
    }
}
