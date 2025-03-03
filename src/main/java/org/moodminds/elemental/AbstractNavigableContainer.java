package org.moodminds.elemental;

import java.util.Iterator;
import java.util.Map;
import java.util.NavigableMap;
import java.util.Optional;
import java.util.SortedMap;

/**
 * A template implementation of the {@link NavigableContainer} interface,
 * which allows duplicates and is powered by an internal {@link NavigableMap}.
 *
 * @param <E> the element type
 * @param <M> the type of the internal {@link SortedMap}
 */
public abstract class AbstractNavigableContainer<E, M extends NavigableMap<E, Object>>
        extends AbstractSortedContainer<E, M> implements NavigableContainer<E> {

    private static final long serialVersionUID = -5610445205254730785L;

    /**
     * Construct the object with the given {@link M map}.
     *
     * @param map the given {@link M map}
     */
    protected AbstractNavigableContainer(M map) {
        super(map);
    }

    /**
     * Construct the object with the given {@link M map} and size.
     *
     * @param map the given {@link M map}
     * @param size the given size
     */
    protected AbstractNavigableContainer(M map, Integer size) {
        super(map, size);
    }

    /**
     * Construct the object with the given {@link M map} and sequential single-threaded {@link Producer} of elements.
     *
     * @param map the given {@link M map}
     * @param elements the given sequential single-threaded {@link Producer} of elements
     */
    protected AbstractNavigableContainer(M map, Producer<? extends E> elements) {
        super(map, elements);
    }

    @Override public Iterator<E> lowerAll(E e) {
        Map.Entry<E, Object> entry = map.lowerEntry(e); return entry != null ? tryBucket(entry.getValue(),
                bucket -> iterator(bucket, bucket.iterator()), value -> iterator(value, true)) : iterator(null, false); }
    @Override public Iterator<E> floorAll(E e) {
        Map.Entry<E, Object> entry = map.floorEntry(e); return entry != null ? tryBucket(entry.getValue(),
                bucket -> iterator(bucket, bucket.iterator()), value -> iterator(value, true)) : iterator(null, false); }
    @Override public Iterator<E> ceilingAll(E e) {
        Map.Entry<E, Object> entry = map.ceilingEntry(e); return entry != null ? tryBucket(entry.getValue(),
                bucket -> iterator(bucket, bucket.iterator()), value -> iterator(value, true)) : iterator(null, false); }
    @Override public Iterator<E> higherAll(E e) {
        Map.Entry<E, Object> entry = map.higherEntry(e); return entry != null ? tryBucket(entry.getValue(),
                bucket -> iterator(bucket, bucket.iterator()), value -> iterator(value, true)) : iterator(null, false); }

    @Override public NavigableContainer<E> descending() {
        return new NavigableSubContainer<>(this, map.descendingMap(), size); }

    @Override public NavigableContainer<E> sub(E fromElement, boolean fromInclusive, E toElement, boolean toInclusive) {
        return new NavigableSubContainer<>(this, map.subMap(fromElement, fromInclusive, toElement, toInclusive)); }
    @Override public NavigableContainer<E> head(E toElement, boolean inclusive) {
        return new NavigableSubContainer<>(this, map.headMap(toElement, inclusive)); }
    @Override public NavigableContainer<E> tail(E fromElement, boolean inclusive) {
        return new NavigableSubContainer<>(this, map.tailMap(fromElement, inclusive)); }


    /**
     * Sub-container extension of the {@link AbstractNavigableContainer}.
     *
     * @param <P> the type of the parent collection
     * @param <E> the element type
     * @param <M> the type of the internal {@link SortedMap}
     */
    protected static class NavigableSubContainer<P extends AbstractNavigableContainer<E, ? extends M>,
            E, M extends NavigableMap<E, Object>> extends AbstractNavigableContainer<E, M> {

        private static final long serialVersionUID = -5646956340075624242L;

        protected final P parent;

        protected NavigableSubContainer(P parent, M map) {
            super(map); this.parent = parent; }

        protected NavigableSubContainer(P parent, M map, Integer size) {
            super(map, size); this.parent = parent; }

        @Override protected Optional<Boolean> isDistinct() {
            return parent.isDistinct(); }
    }
}
