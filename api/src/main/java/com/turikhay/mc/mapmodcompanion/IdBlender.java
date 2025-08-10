package com.turikhay.mc.mapmodcompanion;

import java.util.Objects;

/**
 * Combines two world identifiers into a single deterministic value.
 */
public interface IdBlender {

    /**
     * A simple implementation that hashes the provided ids using
     * {@link Objects#hash(Object...)}.
     */
    IdBlender DEFAULT = new IdBlender() {
        @Override
        public <IdType extends Id> IdType blend(IdType id, int anotherId) {
            return id.withId(Objects.hash(id.getId(), anotherId));
        }
    };

    /**
     * Produces a new id based on two input ids.
     *
     * @param id        base id
     * @param anotherId id to blend with
     * @param <IdType>  concrete {@link Id} type
     * @return blended identifier
     */
    <IdType extends Id> IdType blend(IdType id, int anotherId);
}
