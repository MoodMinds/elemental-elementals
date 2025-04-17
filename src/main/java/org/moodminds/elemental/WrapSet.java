package org.moodminds.elemental;

import java.util.SortedSet;

import static org.moodminds.sneaky.Cast.cast;

/**
 * Wrapping {@link java.util.Set} implementation of the {@link Set} interface.
 *
 * @param <E> the type of elements
 * @param <S> the type of wrapped {@link java.util.Set}
 */
public class WrapSet<E, S extends java.util.Set<E>> extends WrapCollection<E, S>
        implements Set<E> {

    private static final long serialVersionUID = 5408431420941605919L;

    /**
     * Construct the object with the given {@link S} set.
     *
     * @param set the given {@link S} set to wrap
     */
    protected WrapSet(S set) {
        super(set);
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
     * @param set the given {@link java.util.Set} set
     * @return wrapping {@link Set} instance of the given {@link java.util.Set} set
     * @param <V> the type of elements
     * @throws NullPointerException if the given {@link java.util.Set} set is {@code null}
     */
    public static <V> Set<V> wrap(java.util.Set<V> set) {
        return set instanceof SortedSet ? WrapSortedSet.wrap(cast(set)) : new WrapSet<>(set);
    }
}
