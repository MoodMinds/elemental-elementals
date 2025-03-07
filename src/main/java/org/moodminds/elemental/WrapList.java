package org.moodminds.elemental;

import java.util.Iterator;
import java.util.RandomAccess;

import static org.moodminds.sneaky.Cast.cast;

/**
 * Wrapping {@link java.util.List} implementation of the {@link List} interface.
 *
 * @param <E> the type of elements
 * @param <L> the type of wrapped {@link java.util.List}
 */
public class WrapList<E, L extends java.util.List<E>> extends WrapCollection<E, L> implements List<E> {

    private static final long serialVersionUID = -148203256793068643L;

    /**
     * Construct the object with the given {@link L} list.
     *
     * @param list the given {@link L} list to wrap
     */
    protected WrapList(L list) {
        super(list);
    }

    @Override public boolean equatable(Object obj) {
        return obj instanceof java.util.List; }
    @Override public E get(int index) {
        return wrapped.get(index); }
    @Override public Iterator<E> getAllDescending(Object o) {
        return iterator(descendingIterator(), o); }
    @Override public E set(int index, E element) {
        return wrapped.set(index, element); }
    @Override public void add(int index, E element) {
        wrapped.add(index, element); }
    @Override public E remove(int index) {
        return wrapped.remove(index); }
    @Override public List<E> subList(int fromIndex, int toIndex) {
        return wrap(wrapped.subList(fromIndex, toIndex)); }
    @Override public ListIterator<E> listIterator() {
        return WrapListIterator.wrap(wrapped.listIterator()); }
    @Override public ListIterator<E> listIterator(int index) {
        return WrapListIterator.wrap(wrapped.listIterator(index)); }
    @Override public int hashCode() {
        return wrapped.hashCode(); }
    @Override public boolean equals(Object obj) {
        return this == obj || (obj instanceof WrapList && wrapped.equals(((WrapList<?, ?>) obj).wrapped))
            || wrapped.equals(obj); }

    @Override protected String toStringThis() {
        return "(this List)"; }


    /**
     * Wrapping {@link RandomAccess} {@link java.util.List} implementation of the {@link List} interface.
     *
     * @param <E> the type of elements
     * @param <L> the type of wrapped {@link RandomAccess} {@link java.util.List}
     */
    protected static class WrapRandomAccessList<E, L extends java.util.List<E> & RandomAccess>
            extends WrapList<E, L> implements RandomGet, RandomAccess {

        private static final long serialVersionUID = -1342021321821812516L;

        protected WrapRandomAccessList(L wrapped) { super(wrapped); }
    }


    /**
     * Return wrapping {@link List} instance of the given {@link java.util.List} list.
     *
     * @param list the given {@link java.util.List} collection
     * @return wrapping {@link List} instance of the given {@link java.util.List} list
     * @param <V> the type of elements
     * @throws NullPointerException if the given {@link java.util.List} list is {@code null}
     */
    public static <V> List<V> wrap(java.util.List<V> list) {
        return list instanceof RandomAccess ? new WrapRandomAccessList<>(cast(list))
                : new WrapList<>(list);
    }
}
