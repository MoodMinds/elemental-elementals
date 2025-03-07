package org.moodminds.elemental;

import java.util.Iterator;
import java.util.Map.Entry;
import java.util.NavigableMap;

/**
 * A template unique-values implementation of the {@link NavigableContainer}
 * interface, which is powered by an internal {@link NavigableMap}.
 *
 * @param <E> the element type
 * @param <M> the type of the internal {@link NavigableMap}
 */
public abstract class AbstractNavigableUnitainer<E, M extends NavigableMap<E, E>>
        extends AbstractSortedUnitainer<E, M> implements NavigableContainer<E> {

    private static final long serialVersionUID = 2736637274731331383L;

    /**
     * Construct the object with the given {@link M map} .
     *
     * @param map the given {@link M map} 
     */
    protected AbstractNavigableUnitainer(M map) {
        super(map);
    }

    /**
     * Construct the object with the given {@link M} and sequential single-threaded {@link Producer} of elements.
     *
     * @param map the given {@link M}
     * @param elements the given sequential single-threaded {@link Producer} of elements
     */
    protected AbstractNavigableUnitainer(M map, Producer<? extends E> elements) {
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

    @Override public NavigableContainer<E> descending() {
        return new NavigableSubUnitainer<>(map.descendingMap()); }

    @Override public NavigableContainer<E> sub(E fromElement, boolean fromInclusive, E toElement, boolean toInclusive) {
        return new NavigableSubUnitainer<>(map.subMap(fromElement, fromInclusive, toElement, toInclusive)); }
    @Override public NavigableContainer<E> head(E toElement, boolean inclusive) {
        return new NavigableSubUnitainer<>(map.headMap(toElement, inclusive)); }
    @Override public NavigableContainer<E> tail(E fromElement, boolean inclusive) {
        return new NavigableSubUnitainer<>(map.tailMap(fromElement, inclusive)); }


    /**
     * Sub-container extension of the {@link AbstractNavigableUnitainer}.
     *
     * @param <E> the element type
     * @param <M> the type of the internal {@link NavigableMap}
     */
    protected static class NavigableSubUnitainer<E, M extends NavigableMap<E, E>>
            extends AbstractNavigableUnitainer<E, M> {

        private static final long serialVersionUID = 3481554803700758249L;

        protected NavigableSubUnitainer(M map) { super(map); }
    }
}
