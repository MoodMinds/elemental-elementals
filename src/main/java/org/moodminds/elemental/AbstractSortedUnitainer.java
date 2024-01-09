package org.moodminds.elemental;

import java.util.Comparator;
import java.util.Iterator;
import java.util.SortedMap;

/**
 * Template unique-values implementation of the {@link SortedContainer}
 * interface, which is powered by an internal {@link java.util.SortedMap}.
 *
 * @param <E> the element type
 * @param <M> the type of the internal {@link SortedMap}
 */
public abstract class AbstractSortedUnitainer<E, M extends SortedMap<E, E>> extends AbstractUnitainer<E, M>
        implements SortedContainer<E> {

    private static final long serialVersionUID = 3571792155591018581L;

    /**
     * Construct the object with the given {@link M} map.
     *
     * @param map the given {@link M} map
     */
    protected AbstractSortedUnitainer(M map) {
        super(map);
    }


    @Override public Comparator<? super E> comparator() {
        return map.comparator(); }

    @Override public Iterator<E> firstAll() {
        return contains() ? iterator(map.firstKey(), true) : iterator(null, false); }
    @Override public Iterator<E> lastAll() {
        return contains() ? iterator(map.lastKey(), true) : iterator(null, false); }
}
