package com.turikhay.mc.mapmodcompanion;

import java.util.Objects;

/**
 * Plain numeric {@link Id} implementation.
 */
public class StandardId implements Id {
    private final int id;

    /**
     * Creates a new id with the given value.
     *
     * @param id numeric world id
     */
    public StandardId(int id) {
        this.id = id;
    }

    /**
     * Returns the numeric world id.
     */
    public int getId() {
        return id;
    }

    /** {@inheritDoc} */
    @Override
    public StandardId withIdUnchecked(int id) {
        return new StandardId(id);
    }

    /** {@inheritDoc} */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        StandardId that = (StandardId) o;
        return id == that.id;
    }

    /** {@inheritDoc} */
    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    /** {@inheritDoc} */
    @Override
    public String toString() {
        return "StandardId{" +
                "id=" + id +
                '}';
    }
}
