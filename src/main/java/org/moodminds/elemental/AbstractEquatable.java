package org.moodminds.elemental;

/**
 * A template implementation of the {@link Equatable} interface.
 */
public abstract class AbstractEquatable implements Equatable {

    /**
     * Returns the hash code value for this {@link Equatable}.
     *
     * @return the hash code value for this {@link Equatable}
     */
    @Override
    public abstract int hashCode();

    /**
     * Indicates whether some specified object is "equal to" this one.
     *
     * @param obj the specified object
     * @return {@code true} if the specified object equals to this one, or {@code false} otherwise
     */
    @Override
    public boolean equals(Object obj) {
        return super.equals(obj) || equatable(obj) && obj instanceof Equatable
                && ((Equatable) obj).equatable(this) && equals((Equatable) obj);
    }

    /**
     * Return {@code true} if this {@link Equatable} is equal to the given {@link Equatable},
     * or {@code false} otherwise.
     *
     * @param equatable the given {@link Equatable} to check this for equality to
     * @return {@code true} if this {@link Equatable} is equal to the given {@link Equatable}
     */
    protected abstract boolean equals(Equatable equatable);
}
