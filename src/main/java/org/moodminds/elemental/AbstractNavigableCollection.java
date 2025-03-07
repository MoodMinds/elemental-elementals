package org.moodminds.elemental;

import java.util.Iterator;
import java.util.Map;
import java.util.NavigableMap;
import java.util.Optional;
import java.util.SortedMap;

/**
 * A template implementation of the {@link NavigableCollection} interface,
 * which allows duplicates and is powered by an internal {@link NavigableMap}.
 *
 * @param <E> the element type
 * @param <M> the type of the internal {@link NavigableMap}
 */
public abstract class AbstractNavigableCollection<E, M extends NavigableMap<E, Object>>
        extends AbstractSortedCollection<E, M> implements NavigableCollection<E> {

    private static final long serialVersionUID = 1857254411097836838L;

    /**
     * Construct the object by the given {@link M map}.
     *
     * @param map the given {@link M map}
     */
    protected AbstractNavigableCollection(M map) {
        super(map);
    }

    /**
     * Construct the object by the given {@link M map} and size.
     *
     * @param map the given {@link M map}
     * @param size the given size
     */
    protected AbstractNavigableCollection(M map, Integer size) {
        super(map, size);
    }

    /**
     * Construct the object with the given {@link M map} and sequential single-threaded {@link Producer} of elements.
     *
     * @param map the given {@link M map}
     * @param elements the given sequential single-threaded {@link Producer} of elements
     */
    protected AbstractNavigableCollection(M map, Producer<? extends E> elements) {
        super(map, elements);
    }

    @Override public E pollFirst() {
        return poll(iterator()); }
    @Override public E pollLast() {
        return poll(descending().iterator()); }

    @Override public Iterator<E> lowerAll(E e) {
        Map.Entry<E, Object> entry = map.lowerEntry(e); return collectionIterator(entry != null ? tryBucket(entry.getValue(),
                bucket -> iterator(bucket, bucket.iterator()), value -> iterator(value, true)) : iterator(null, false)); }
    @Override public Iterator<E> floorAll(E e) {
        Map.Entry<E, Object> entry = map.floorEntry(e); return collectionIterator(entry != null ? tryBucket(entry.getValue(),
                bucket -> iterator(bucket, bucket.iterator()), value -> iterator(value, true)) : iterator(null, false)); }
    @Override public Iterator<E> ceilingAll(E e) {
        Map.Entry<E, Object> entry = map.ceilingEntry(e); return collectionIterator(entry != null ? tryBucket(entry.getValue(),
                bucket -> iterator(bucket, bucket.iterator()), value -> iterator(value, true)) : iterator(null, false)); }
    @Override public Iterator<E> higherAll(E e) {
        Map.Entry<E, Object> entry = map.higherEntry(e); return collectionIterator(entry != null ? tryBucket(entry.getValue(),
                bucket -> iterator(bucket, bucket.iterator()), value -> iterator(value, true)) : iterator(null, false)); }

    /**
     * Retrieve and remove the next element from the specified iterator.
     * If the iterator has no more elements, return {@code null}.
     *
     * @param iterator the iterator from which to retrieve the next element
     * @return the next element in the iterator, or {@code null} if no elements are left
     */
    protected E poll(Iterator<E> iterator) {
        if (iterator.hasNext()) {
            E value = iterator.next();
            iterator.remove(); return value;
        } return null;
    }


    /**
     * Descending sub-collection extension of the {@link AbstractSortedCollection}.
     *
     * @param <R> the type of the root collection
     * @param <P> the type of the parent collection
     * @param <E> the element type
     * @param <M> the type of the internal {@link SortedMap}
     */
    protected static class DescendingSortedSubCollection<R extends AbstractSortedCollection<E, ?>, P extends AbstractSortedCollection<E, ?>,
            E, M extends SortedMap<E, Object>> extends AbstractSortedSubCollection<R, P, E, M> {

        private static final long serialVersionUID = 3947071043558156236L;

        protected DescendingSortedSubCollection(R root, P parent, M map, Range<E> range) {
            super(root, parent, map, range); }

        @Override public SortedCollection<E> sub(E fromElement, E toElement) {
            return new DescendingSortedSubCollection<>(root, this, map.subMap(fromElement, toElement), range.subRange(toElement, false, fromElement, true)); }
        @Override public SortedCollection<E> head(E toElement) {
            return new DescendingSortedSubCollection<>(root, this, map.headMap(toElement), range.tailRange(toElement, false)); }
        @Override public SortedCollection<E> tail(E fromElement) {
            return new DescendingSortedSubCollection<>(root, this, map.tailMap(fromElement), range.headRange(fromElement, true)); }
    }

    /**
     * Sub-collection extension template of the {@link AbstractNavigableCollection}.
     *
     * @param <R> the type of the root collection
     * @param <P> the type of the parent collection
     * @param <E> the element type
     * @param <M> the type of the internal {@link NavigableMap}
     */
    protected abstract static class AbstractNavigableSubCollection<R extends AbstractNavigableCollection<E, ?>, P extends AbstractNavigableCollection<E, ?>,
            E, M extends NavigableMap<E, Object>> extends AbstractNavigableCollection<E, M> {

        private static final long serialVersionUID = 4875940283769434697L;

        protected final Range<E> range;

        protected final R root; protected final P parent;

        protected AbstractNavigableSubCollection(R root, P parent, M map, Range<E> range) {
            super(map); this.range = range; this.root = root; this.parent = parent; parent.child = new SubReference<>(parent.child, this, range); }

        protected AbstractNavigableSubCollection(R root, P parent, M map, Integer size, Range<E> range) {
            super(map, size); this.range = range; this.root = root; this.parent = parent; parent.child = new SubReference<>(parent.child, this, range); }

        @Override protected int totalMod() { return parent.totalMod(); }
        @Override protected void countMod() { parent.countMod(); }

        @Override protected Optional<Boolean> isDistinct() {
            return parent.isDistinct(); }

        @Override protected void propagateClear(Integer size) {
            new ClearPropagation<>(range, size).untilCollection(this).propagateRoot(root); super.propagateClear(size); }
        @Override protected void propagateCount(E element, int number) {
            new CountPropagation<>(element, number).untilCollection(this).propagateRoot(root); super.propagateCount(element, number); }
        @Override protected void propagateSize(int size) {
            new SizePropagation<>(range, size).untilCollection(this).propagateRoot(root); super.propagateSize(size); }
    }

    /**
     * Sub-collection extension of the {@link AbstractNavigableCollection}.
     *
     * @param <R> the type of the root collection
     * @param <P> the type of the parent collection
     * @param <E> the element type
     * @param <M> the type of the internal {@link NavigableMap}
     */
    protected static class NavigableSubCollection<R extends AbstractNavigableCollection<E, ?>, P extends AbstractNavigableCollection<E, ?>,
            E, M extends NavigableMap<E, Object>> extends AbstractNavigableSubCollection<R, P, E, M> {

        private static final long serialVersionUID = 6413530236317264799L;

        protected NavigableSubCollection(R root, P parent, M map, Range<E> range) {
            super(root, parent, map, range); }

        protected NavigableSubCollection(R root, P parent, M map, Integer size, Range<E> range) {
            super(root, parent, map, size, range); }

        @Override public SortedCollection<E> sub(E fromElement, E toElement) {
            return new SortedSubCollection<>(root, this, map.subMap(fromElement, toElement), range.subRange(fromElement, toElement)); }
        @Override public SortedCollection<E> head(E toElement) {
            return new SortedSubCollection<>(root, this, map.headMap(toElement), range.headRange(toElement)); }
        @Override public SortedCollection<E> tail(E fromElement) {
            return new SortedSubCollection<>(root, this, map.tailMap(fromElement), range.tailRange(fromElement)); }

        @Override public NavigableCollection<E> descending() {
            return new DescendingNavigableSubCollection<>(root, this, map.descendingMap(), size, range); }

        @Override public NavigableCollection<E> sub(E fromElement, boolean fromInclusive, E toElement, boolean toInclusive) {
            return new NavigableSubCollection<>(root, this, map.subMap(fromElement, fromInclusive, toElement, toInclusive), range.subRange(fromElement, fromInclusive, toElement, toInclusive)); }
        @Override public NavigableCollection<E> head(E toElement, boolean inclusive) {
            return new NavigableSubCollection<>(root, this, map.headMap(toElement, inclusive), range.headRange(toElement, inclusive)); }
        @Override public NavigableCollection<E> tail(E fromElement, boolean inclusive) {
            return new NavigableSubCollection<>(root, this, map.tailMap(fromElement, inclusive), range.tailRange(fromElement, inclusive)); }
    }

    /**
     * Descending sub-collection extension of the {@link AbstractNavigableCollection}.
     *
     * @param <R> the type of the root collection
     * @param <P> the type of the parent collection
     * @param <E> the element type
     * @param <M> the type of the internal {@link NavigableMap}
     */
    protected static class DescendingNavigableSubCollection<R extends AbstractNavigableCollection<E, ?>, P extends AbstractNavigableCollection<E, ?>,
            E, M extends NavigableMap<E, Object>> extends AbstractNavigableSubCollection<R, P, E, M> {

        private static final long serialVersionUID = 6413530236317264799L;

        protected DescendingNavigableSubCollection(R root, P parent, M map, Range<E> range) {
            super(root, parent, map, range); }

        protected DescendingNavigableSubCollection(R root, P parent, M map, Integer size, Range<E> range) {
            super(root, parent, map, size, range); }

        @Override public SortedCollection<E> sub(E fromElement, E toElement) {
            return new DescendingSortedSubCollection<>(root, this, map.subMap(fromElement, toElement), range.subRange(toElement, false, fromElement, true)); }
        @Override public SortedCollection<E> head(E toElement) {
            return new DescendingSortedSubCollection<>(root, this, map.headMap(toElement), range.tailRange(toElement, false)); }
        @Override public SortedCollection<E> tail(E fromElement) {
            return new DescendingSortedSubCollection<>(root, this, map.tailMap(fromElement), range.headRange(fromElement, true)); }

        @Override public NavigableCollection<E> descending() {
            return new NavigableSubCollection<>(root, this, map.descendingMap(), size, range); }

        @Override public NavigableCollection<E> sub(E fromElement, boolean fromInclusive, E toElement, boolean toInclusive) {
            return new DescendingNavigableSubCollection<>(root, this, map.subMap(fromElement, fromInclusive, toElement, toInclusive), range.subRange(toElement, toInclusive, fromElement, fromInclusive)); }
        @Override public NavigableCollection<E> head(E toElement, boolean inclusive) {
            return new DescendingNavigableSubCollection<>(root, this, map.headMap(toElement, inclusive), range.tailRange(toElement, inclusive)); }
        @Override public NavigableCollection<E> tail(E fromElement, boolean inclusive) {
            return new DescendingNavigableSubCollection<>(root, this, map.tailMap(fromElement, inclusive), range.headRange(fromElement, inclusive)); }
    }
}
