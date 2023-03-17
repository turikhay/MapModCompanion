package com.turikhay.mc.mapmodcompanion;

public interface Id {
    int MAGIC_MARKER = 42;

    int getId();

    Id withIdUnchecked(int id);

    @SuppressWarnings("unchecked")
    default <IdType> IdType withId(int id) {
        return (IdType) withIdUnchecked(id);
    }

    interface Deserializer<IdType extends Id> {
        IdType deserialize(byte[] data) throws MalformedPacketException;
    }

    interface Serializer<IdType extends Id> {
        byte[] serialize(IdType id);
    }
}
