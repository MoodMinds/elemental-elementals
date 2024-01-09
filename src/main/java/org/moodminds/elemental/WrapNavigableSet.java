package org.moodminds.elemental;

import java.util.Iterator;

/**
 * Wrapping {@link java.util.NavigableSet} implementation of the {@link NavigableSet} interface.
 *
 * @param <V> the type of elements
 * @param <S> the type of wrapped {@link java.util.NavigableSet}
 */
public class WrapNavigableSet<V, S extends java.util.NavigableSet<V>> extends WrapSortedSet<V, S>
        implements NavigableSet<V> {

    private static final long serialVersionUID = 4693344087145930088L;

    /**
     * Construct the object with the given {@link S} set.
     *
     * @param wrapped the given {@link S} set to wrap
     */
    protected WrapNavigableSet(S wrapped) { super(wrapped); }

    @Override public V lower(V v) {
        return wrapped.lower(v); }
    @Override public Iterator<V> lowerAll(V e) {
        V value = wrapped.lower(e); return iterator(value, value != null || wrapped.contains(null)); }
    @Override public V floor(V v) {
        return wrapped.floor(v); }
    @Override public Iterator<V> floorAll(V e) {
        V value = wrapped.floor(e); return iterator(value, value != null || wrapped.contains(null)); }
    @Override public V ceiling(V v) {
        return wrapped.ceiling(v); }
    @Override public Iterator<V> ceilingAll(V e) {
        V value = wrapped.ceiling(e); return iterator(value, value != null || wrapped.contains(null)); }
    @Override public V higher(V v) {
        return wrapped.higher(v); }
    @Override public Iterator<V> higherAll(V e) {
        V value = wrapped.higher(e); return iterator(value, value != null || wrapped.contains(null)); }
    @Override public V pollFirst() {
        return wrapped.pollFirst(); }
    @Override public V pollLast() {
        return wrapped.pollLast(); }
    @Override public NavigableSet<V> descendingSet() {
        return wrap(wrapped.descendingSet()); }
    @Override public Iterator<V> descendingIterator() {
        return wrapped.descendingIterator(); }
    @Override public NavigableSet<V> subSet(V fromElement, boolean fromInclusive, V toElement, boolean toInclusive) {
        return wrap(wrapped.subSet(fromElement, fromInclusive, toElement, toInclusive)); }
    @Override public NavigableSet<V> headSet(V toElement, boolean inclusive) {
        return wrap(wrapped.headSet(toElement, inclusive)); }
    @Override public NavigableSet<V> tailSet(V fromElement, boolean inclusive) {
        return wrap(wrapped.tailSet(fromElement, inclusive)); }


    /**
     * Return wrapping {@link NavigableSet} instance of the given {@link java.util.NavigableSet} set.
     *
     * @param wrapped the given {@link java.util.NavigableSet} set
     * @param <V> the type of elements
     * @return wrapping {@link NavigableSet} instance of the given {@link java.util.NavigableSet} set
     * @throws NullPointerException if the given {@link java.util.NavigableSet} set is {@code null}
     */
    public static <V> NavigableSet<V> wrap(java.util.NavigableSet<V> wrapped) {
        return new WrapNavigableSet<>(wrapped);
    }
}
