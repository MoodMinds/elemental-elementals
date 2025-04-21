package org.moodminds.elemental;

import java.util.Iterator;

/**
 * Wrapping {@link java.util.NavigableSet} implementation of the {@link NavigableSet} interface.
 *
 * @param <E> the type of elements
 * @param <S> the type of wrapped {@link java.util.NavigableSet}
 */
public class WrapNavigableSet<E, S extends java.util.NavigableSet<E>> extends WrapSortedSet<E, S>
        implements NavigableSet<E> {

    private static final long serialVersionUID = 4693344087145930088L;

    /**
     * Construct the object with the given {@link S} set.
     *
     * @param set the given {@link S} set to wrap
     */
    protected WrapNavigableSet(S set) { super(set); }

    @Override public E lower(E e) {
        return wrapped.lower(e); }
    @Override public Iterator<E> lowerAll(E e) {
        E value = wrapped.lower(e); return value != null || containsNull()
                ? iterator(value, true) : iterator(null, false); }
    @Override public E floor(E e) {
        return wrapped.floor(e); }
    @Override public Iterator<E> floorAll(E e) {
        E value = wrapped.floor(e); return value != null || containsNull()
                ? iterator(value, true) : iterator(null, false); }
    @Override public E ceiling(E e) {
        return wrapped.ceiling(e); }
    @Override public Iterator<E> ceilingAll(E e) {
        E value = wrapped.ceiling(e); return value != null || containsNull()
                ? iterator(value, true) : iterator(null, false); }
    @Override public E higher(E e) {
        return wrapped.higher(e); }
    @Override public Iterator<E> higherAll(E e) {
        E value = wrapped.higher(e); return value != null || containsNull()
                ? iterator(value, true) : iterator(null, false); }
    @Override public E pollFirst() {
        return wrapped.pollFirst(); }
    @Override public E pollLast() {
        return wrapped.pollLast(); }
    @Override public NavigableSet<E> descendingSet() {
        return wrap(wrapped.descendingSet()); }
    @Override public Iterator<E> descendingIterator() {
        return wrapped.descendingIterator(); }
    @Override public NavigableSet<E> subSet(E fromElement, boolean fromInclusive, E toElement, boolean toInclusive) {
        return wrap(wrapped.subSet(fromElement, fromInclusive, toElement, toInclusive)); }
    @Override public NavigableSet<E> headSet(E toElement, boolean inclusive) {
        return wrap(wrapped.headSet(toElement, inclusive)); }
    @Override public NavigableSet<E> tailSet(E fromElement, boolean inclusive) {
        return wrap(wrapped.tailSet(fromElement, inclusive)); }

    /**
     * Determine whether the wrapped {@link java.util.NavigableSet} contains {@code null},
     * accounting for implementations that disallow {@code null} and may throw a {@link NullPointerException}.
     *
     * @return {@code true} if {@code null} is an element of the wrapped set; {@code false} otherwise.
     */
    protected boolean containsNull() {
        if (wrapped.comparator() == null) return false;
        try { return wrapped.contains(null); }
        catch (NullPointerException unused) { return false; }
    }


    /**
     * Return wrapping {@link NavigableSet} instance of the given {@link java.util.NavigableSet} set.
     *
     * @param set the given {@link java.util.NavigableSet} set
     * @param <V> the type of elements
     * @return wrapping {@link NavigableSet} instance of the given {@link java.util.NavigableSet} set
     * @throws NullPointerException if the given {@link java.util.NavigableSet} set is {@code null}
     */
    public static <V> NavigableSet<V> wrap(java.util.NavigableSet<V> set) {
        return new WrapNavigableSet<>(set);
    }
}
