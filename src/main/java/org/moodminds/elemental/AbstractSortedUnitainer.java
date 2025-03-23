package org.moodminds.elemental;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Comparator;
import java.util.Iterator;
import java.util.SortedMap;

import static org.moodminds.sneaky.Cast.cast;

/**
 * A template unique-values implementation of the {@link SortedContainer}
 * interface, which is powered by an internal {@link SortedMap}.
 *
 * @param <E> the element type
 * @param <M> the type of the internal {@link SortedMap}
 */
public abstract class AbstractSortedUnitainer<E, M extends SortedMap<E, E>>
        extends AbstractMapUnitainer<E, M> implements SortedContainer<E> {

    private static final long serialVersionUID = 3571792155591018581L;

    /**
     * Construct the object with the given {@link M map} .
     *
     * @param map the given {@link M map} 
     */
    protected AbstractSortedUnitainer(M map) {
        super(map);
    }

    /**
     * Construct the object with the given {@link M} and sequential single-threaded {@link Producer} of elements.
     *
     * @param map the given {@link M}
     * @param elements the given sequential single-threaded {@link Producer} of elements
     */
    protected AbstractSortedUnitainer(M map, Producer<? extends E> elements) {
        super(map); elements.provide(element -> map.putIfAbsent(element, element));
    }

    @Override public Comparator<? super E> comparator() {
        return map.comparator(); }

    @Override public Iterator<E> firstAll() {
        return contains() ? iterator(map.firstKey(), true) : iterator(null, false); }
    @Override public Iterator<E> lastAll() {
        return contains() ? iterator(map.lastKey(), true) : iterator(null, false); }

    @Override public SortedContainer<E> sub(E fromElement, E toElement) {
        return new SortedSubUnitainer<>(map.subMap(fromElement, toElement)); }
    @Override public SortedContainer<E> head(E toElement) {
        return new SortedSubUnitainer<>(map.headMap(toElement)); }
    @Override public SortedContainer<E> tail(E fromElement) {
        return new SortedSubUnitainer<>(map.tailMap(fromElement)); }

    @Override protected void serialize(ObjectOutputStream output) throws Exception {
        output.writeObject(map); }
    @Override protected void deserialize(ObjectInputStream input) throws Exception {
        map = cast(input.readObject()); }



    /**
     * Sub-container extension of the {@link AbstractSortedUnitainer}.
     *
     * @param <E> the element type
     * @param <M> the type of the internal {@link SortedMap}
     */
    protected static class SortedSubUnitainer<E, M extends SortedMap<E, E>>
            extends AbstractSortedUnitainer<E, M> {

        private static final long serialVersionUID = 3481554803700758249L;

        protected SortedSubUnitainer(M map) { super(map); }
    }
}
