package org.moodminds.elemental;

/**
 * A foundational interface for objects that provide a means to determine if they can be
 * compared to another object without the need to invoke the {@link Object#equals(Object)} method.
 * Such a comparability is essential for ensuring symmetric equality in intricate type hierarchies.
 */
public interface Equatable {

    /**
     * Indicates whether the given object is a valid candidate for comparison with the current object.
     *
     * @param obj the given object to check for comparability with the current object
     * @return {@code true} if the given object can be compared to the current object, {@code false} otherwise
     */
    boolean equatable(Object obj);
}
