package org.moodminds.elemental;

import java.util.SortedSet;

import static org.moodminds.sneaky.Cast.cast;

/**
 * Wrapping {@link java.util.Set} implementation of the {@link Set} interface.
 *
 * @param <V> the type of elements
 * @param <S> the type of wrapped {@link java.util.Set}
 */
public class WrapSet<V, S extends java.util.Set<V>> extends WrapCollection<V, S>
        implements Set<V> {

    private static final long serialVersionUID = 5408431420941605919L;

    /**
     * Construct the object with the given {@link S} set.
     *
     * @param wrapped the given {@link S} set to wrap
     */
    protected WrapSet(S wrapped) {
        super(wrapped);
    }

    @Override public boolean equatable(Object obj) {
        return obj instanceof java.util.Set; }
    @Override public int hashCode() {
        return wrapped.hashCode(); }
    @Override public boolean equals(Object obj) {
        return this == obj || (obj instanceof WrapSet && wrapped.equals(((WrapSet<?, ?>) obj).wrapped))
            || wrapped.equals(obj); }


    /**
     * Return wrapping {@link Set} instance of the given {@link java.util.Set} set.
     *
     * @param wrapped the given {@link java.util.Set} set
     * @return wrapping {@link Set} instance of the given {@link java.util.Set} set
     * @param <V> the type of elements
     * @throws NullPointerException if the given {@link java.util.Set} set is {@code null}
     */
    public static <V> Set<V> wrap(java.util.Set<V> wrapped) {
        return wrapped instanceof SortedSet ? WrapSortedSet.wrap(cast(wrapped)) : new WrapSet<>(wrapped);
    }
}
