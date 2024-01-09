package org.moodminds.elemental;

import java.util.RandomAccess;

import static org.moodminds.sneaky.Cast.cast;

/**
 * Wrapping {@link java.util.List} implementation of the {@link List} interface.
 *
 * @param <V> the type of elements
 * @param <L> the type of wrapped {@link java.util.List}
 */
public class WrapList<V, L extends java.util.List<V>> extends WrapCollection<V, L> implements List<V> {

    private static final long serialVersionUID = -148203256793068643L;

    /**
     * Construct the object with the given {@link L} list.
     *
     * @param wrapped the given {@link L} list to wrap
     */
    protected WrapList(L wrapped) {
        super(wrapped);
    }

    @Override public boolean equatable(Object obj) {
        return obj instanceof java.util.List; }
    @Override public V get(int index) {
        return wrapped.get(index); }
    @Override public V set(int index, V element) {
        return wrapped.set(index, element); }
    @Override public void add(int index, V element) {
        wrapped.add(index, element); }
    @Override public V remove(int index) {
        return wrapped.remove(index); }
    @Override public List<V> subList(int fromIndex, int toIndex) {
        return wrap(wrapped.subList(fromIndex, toIndex)); }
    @Override public ListIterator<V> listIterator() {
        return WrapListIterator.wrap(wrapped.listIterator()); }
    @Override public ListIterator<V> listIterator(int index) {
        return WrapListIterator.wrap(wrapped.listIterator(index)); }
    @Override public int hashCode() {
        return wrapped.hashCode(); }
    @Override public boolean equals(Object obj) {
        return this == obj || (obj instanceof WrapList && wrapped.equals(((WrapList<?, ?>) obj).wrapped))
            || wrapped.equals(obj); }


    /**
     * Wrapping {@link RandomAccess} {@link java.util.List} implementation of the {@link List} interface.
     *
     * @param <V> the type of elements
     * @param <L> the type of wrapped {@link RandomAccess} {@link java.util.List}
     */
    protected static class WrapRandomAccessList<V, L extends java.util.List<V> & RandomAccess> extends WrapList<V, L>
            implements RandomGet, RandomAccess {

        private static final long serialVersionUID = -1342021321821812516L;

        protected WrapRandomAccessList(L wrapped) { super(wrapped); }
    }


    /**
     * Return wrapping {@link List} instance of the given {@link java.util.List} list.
     *
     * @param wrapped the given {@link java.util.List} collection
     * @return wrapping {@link List} instance of the given {@link java.util.List} list
     * @param <V> the type of elements
     * @throws NullPointerException if the given {@link java.util.List} list is {@code null}
     */
    public static <V> List<V> wrap(java.util.List<V> wrapped) {
        return wrapped instanceof RandomAccess ? new WrapRandomAccessList<>(cast(wrapped))
                : new WrapList<>(wrapped);
    }
}
