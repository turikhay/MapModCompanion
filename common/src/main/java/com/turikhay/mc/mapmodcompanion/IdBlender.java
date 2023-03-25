package com.turikhay.mc.mapmodcompanion;

import java.util.Objects;

public interface IdBlender {
    IdBlender DEFAULT = new IdBlender() {
        @Override
        public <IdType extends Id> IdType blend(IdType id, int anotherId) {
            return id.withId(Objects.hash(id.getId(), anotherId));
        }
    };

    <IdType extends Id> IdType blend(IdType id, int anotherId);
}
