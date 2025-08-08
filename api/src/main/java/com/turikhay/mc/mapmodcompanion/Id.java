package com.turikhay.mc.mapmodcompanion;

/**
 * Represents a world identifier.
 * <p>
 * Implementations are immutable and return a new instance when a different id
 * is required.
 */
public interface Id {

    /** Magic value used in some packet formats. */
    int MAGIC_MARKER = 42;

    /**
     * Returns the numeric id value.
     */
    int getId();

    /**
     * Creates a copy of this id with a different numeric value.
     *
     * @param id new value
     * @return new id instance
     */
    Id withIdUnchecked(int id);

    /**
     * Type-safe wrapper around {@link #withIdUnchecked(int)}.
     *
     * <pre>{@code
     * StandardId newId = existing.withId(5);
     * }</pre>
     *
     * @param id new value
     * @param <IdType> concrete {@link Id} type
     * @return new id instance
     */
    @SuppressWarnings("unchecked")
    default <IdType> IdType withId(int id) {
        return (IdType) withIdUnchecked(id);
    }

    /**
     * Converts raw packet bytes into an {@link Id} instance.
     *
     * @param <IdType> type of id produced
     */
    interface Deserializer<IdType extends Id> {
        /**
         * Deserializes an id from raw bytes.
         *
         * @param data raw packet data
         * @return parsed id
         * @throws MalformedPacketException if the data cannot be parsed
         */
        IdType deserialize(byte[] data) throws MalformedPacketException;
    }

    /**
     * Produces raw packet bytes from an {@link Id} instance.
     *
     * @param <IdType> type of id to serialize
     */
    interface Serializer<IdType extends Id> {
        /**
         * Serializes the supplied id to a byte array.
         *
         * @param id id to encode
         * @return encoded packet bytes
         */
        byte[] serialize(IdType id);
    }
}
