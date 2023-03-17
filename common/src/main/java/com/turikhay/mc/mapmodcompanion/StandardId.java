package com.turikhay.mc.mapmodcompanion;

import java.util.Objects;

public class StandardId implements Id {
    private final int id;

    public StandardId(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    @Override
    public StandardId withIdUnchecked(int id) {
        return new StandardId(id);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        StandardId that = (StandardId) o;
        return id == that.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "StandardId{" +
                "id=" + id +
                '}';
    }
}
