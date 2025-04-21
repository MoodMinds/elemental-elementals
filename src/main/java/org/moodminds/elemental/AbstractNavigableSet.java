package org.moodminds.elemental;

import java.util.Iterator;
import java.util.Map.Entry;
import java.util.NavigableMap;

import static java.util.Optional.ofNullable;

/**
 * A template implementation of the {@link NavigableSet} interface,
 * which is powered by an internal {@link NavigableMap}.
 *
 * @param <E> the element type
 * @param <M> the type of the internal {@link NavigableMap}
 */
public abstract class AbstractNavigableSet<E, M extends NavigableMap<E, E>>
        extends AbstractSortedSet<E, M> implements NavigableSet<E> {

    private static final long serialVersionUID = 1139419161769574628L;

    /**
     * Construct the object with the given {@link M map} .
     *
     * @param map the given {@link M map} 
     */
    protected AbstractNavigableSet(M map) {
        super(map);
    }

    /**
     * Construct the object with the given {@link M} and sequential single-threaded {@link Producer} of elements.
     *
     * @param map the given {@link M}
     * @param elements the given sequential single-threaded {@link Producer} of elements
     */
    protected AbstractNavigableSet(M map, Producer<? extends E> elements) {
        super(map, elements);
    }

    @Override public Iterator<E> lowerAll(E e) {
        Entry<E, E> entry = map.lowerEntry(e); return entry != null
                ? iterator(entry.getValue(), true) : iterator(null, false); }
    @Override public Iterator<E> floorAll(E e) {
        Entry<E, E> entry = map.floorEntry(e); return entry != null
                ? iterator(entry.getValue(), true) : iterator(null, false); }
    @Override public Iterator<E> ceilingAll(E e) {
        Entry<E, E> entry = map.ceilingEntry(e); return entry != null
                ? iterator(entry.getValue(), true) : iterator(null, false); }
    @Override public Iterator<E> higherAll(E e) {
        Entry<E, E> entry = map.higherEntry(e); return entry != null
                ? iterator(entry.getValue(), true) : iterator(null, false); }

    @Override public E pollFirst() {
        return ofNullable(map.pollFirstEntry()).map(Entry::getKey).orElse(null); }
    @Override public E pollLast() {
        return ofNullable(map.pollLastEntry()).map(Entry::getKey).orElse(null); }

    @Override public Iterator<E> descendingIterator() {
        return map.descendingKeySet().iterator(); }

    @Override public NavigableSet<E> descendingSet() {
        return new NavigableSubSet<>(map.descendingMap()); }

    @Override public NavigableSet<E> subSet(E fromElement, boolean fromInclusive, E toElement, boolean toInclusive) {
        return new NavigableSubSet<>(map.subMap(fromElement, fromInclusive, toElement, toInclusive)); }
    @Override public NavigableSet<E> headSet(E toElement, boolean inclusive) {
        return new NavigableSubSet<>(map.headMap(toElement, inclusive)); }
    @Override public NavigableSet<E> tailSet(E fromElement, boolean inclusive) {
        return new NavigableSubSet<>(map.tailMap(fromElement, inclusive)); }


    /**
     * Sub-set extension of the {@link AbstractNavigableSet}.
     *
     * @param <E> the element type
     * @param <M> the type of the internal {@link NavigableMap}
     */
    protected static class NavigableSubSet<E, M extends NavigableMap<E, E>>
            extends AbstractNavigableSet<E, M> {

        private static final long serialVersionUID = -6882257609001927320L;

        protected NavigableSubSet(M map) { super(map); }
    }
}
