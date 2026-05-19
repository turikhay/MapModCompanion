package com.turikhay.mc.mapmodcompanion;

import java.util.Objects;
import java.util.UUID;

public class WorldInfo {
    private final UUID uuid;
    private final String name;

    public WorldInfo(UUID uuid, String name) {
        this.uuid = uuid;
        this.name = name;
    }

    public UUID getUID() {
        return uuid;
    }

    public String getName() {
        return name;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        WorldInfo worldInfo = (WorldInfo) o;
        return Objects.equals(uuid, worldInfo.uuid) && Objects.equals(name, worldInfo.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(uuid, name);
    }

    @Override
    public String toString() {
        return "WorldInfo{" +
                "uuid=" + uuid +
                ", name='" + name + '\'' +
                '}';
    }
}
