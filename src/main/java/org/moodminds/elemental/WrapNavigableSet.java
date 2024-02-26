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
     * @param set the given {@link S} set to wrap
     */
    protected WrapNavigableSet(S set) { super(set); }

    @Override public V lower(V v) {
        return collection.lower(v); }
    @Override public Iterator<V> lowerAll(V e) {
        V value = collection.lower(e); return value != null || collection.contains(null)
                ? new SortedSetSingleIterator(value) : new SortedSetEmptyIterator(); }
    @Override public V floor(V v) {
        return collection.floor(v); }
    @Override public Iterator<V> floorAll(V e) {
        V value = collection.floor(e); return value != null || collection.contains(null)
                ? new SortedSetSingleIterator(value) : new SortedSetEmptyIterator(); }
    @Override public V ceiling(V v) {
        return collection.ceiling(v); }
    @Override public Iterator<V> ceilingAll(V e) {
        V value = collection.ceiling(e); return value != null || collection.contains(null)
                ? new SortedSetSingleIterator(value) : new SortedSetEmptyIterator(); }
    @Override public V higher(V v) {
        return collection.higher(v); }
    @Override public Iterator<V> higherAll(V e) {
        V value = collection.higher(e); return value != null || collection.contains(null)
                ? new SortedSetSingleIterator(value) : new SortedSetEmptyIterator(); }
    @Override public V pollFirst() {
        return collection.pollFirst(); }
    @Override public V pollLast() {
        return collection.pollLast(); }
    @Override public NavigableSet<V> descendingSet() {
        return wrap(collection.descendingSet()); }
    @Override public Iterator<V> descendingIterator() {
        return collection.descendingIterator(); }
    @Override public NavigableSet<V> subSet(V fromElement, boolean fromInclusive, V toElement, boolean toInclusive) {
        return wrap(collection.subSet(fromElement, fromInclusive, toElement, toInclusive)); }
    @Override public NavigableSet<V> headSet(V toElement, boolean inclusive) {
        return wrap(collection.headSet(toElement, inclusive)); }
    @Override public NavigableSet<V> tailSet(V fromElement, boolean inclusive) {
        return wrap(collection.tailSet(fromElement, inclusive)); }


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
