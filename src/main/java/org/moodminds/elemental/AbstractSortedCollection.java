package org.moodminds.elemental;

import org.moodminds.sneaky.Cast;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.lang.ref.Reference;
import java.lang.ref.WeakReference;
import java.util.Comparator;
import java.util.Iterator;
import java.util.Map;
import java.util.Optional;
import java.util.SortedMap;
import java.util.Spliterator;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Consumer;

import static java.util.Objects.requireNonNull;
import static java.util.Optional.ofNullable;
import static org.moodminds.sneaky.Cast.cast;

/**
 * A template implementation of the {@link SortedCollection} interface,
 * which allows duplicates and is powered by an internal {@link SortedMap}.
 *
 * @param <E> the element type
 * @param <M> the type of the internal {@link SortedMap}
 */
public abstract class AbstractSortedCollection<E, M extends SortedMap<E, Object>>
        extends AbstractHeapCollection<E, M> implements SortedCollection<E> {

    private static final long serialVersionUID = 8097575755780232788L;

    /**
     * Container size holder field.
     */
    protected Integer size;

    /**
     * Child sub-Collection {@link Reference} holder field.
     */
    protected transient SubReference<E> child;

    /**
     * Construct the object by the given {@link M map}.
     *
     * @param map the given {@link M map}
     */
    protected AbstractSortedCollection(M map) {
        super(map);
    }

    /**
     * Construct the object by the given {@link M map} and size.
     *
     * @param map the given {@link M map}
     * @param size the given size
     */
    protected AbstractSortedCollection(M map, Integer size) {
        super(map); this.size = size;
    }

    /**
     * Construct the object with the given {@link M map} and sequential single-threaded {@link Producer} of elements.
     *
     * @param map the given {@link M map}
     * @param elements the given sequential single-threaded {@link Producer} of elements
     */
    protected AbstractSortedCollection(M map, Producer<? extends E> elements) {
        super(map, elements);
    }

    @Override public boolean add(E element) {
        super.add(element); propagateCount(element, 1); return true; }
    @Override public void clear() {
        Integer size = this.size; super.clear(); propagateClear(size); }

    @Override public int size() {
        return getSize().orElseGet(() -> {
            int count = 0; for (Object value : map.values())
                count = count + tryBucket(value, Container::size, unused -> 1);
            propagateSize(size = count); return size;
        });
    }

    @Override public Comparator<? super E> comparator() {
        return map.comparator(); }

    @Override public Iterator<E> firstAll() {
        return contains() ? getAll(map.firstKey()) : iterator(null, false); }
    @Override public Iterator<E> lastAll() {
        return contains() ? getAll(map.lastKey()) : iterator(null, false); }

    /**
     * {@inheritDoc}
     *
     * @param entriesSpliterator {@inheritDoc}
     * @param useSourceSize {@inheritDoc}
     * @param isDistinct {@inheritDoc}
     * @param knownSize {@inheritDoc}
     * @return {@inheritDoc}
     */
    @Override
    protected Spliterator<E> containerSpliterator(Spliterator<Map.Entry<E, Object>> entriesSpliterator, boolean useSourceSize, boolean isDistinct, Integer knownSize) {
        return super.containerSpliterator(entriesSpliterator, false, isDistinct, knownSize);
    }

    /**
     * {@inheritDoc}
     *
     * @param iterator {@inheritDoc}
     * @return {@inheritDoc}
     */
    @Override
    protected Iterator<E> collectionIterator(Iterator<E> iterator) {
        return new Iterator<E>() {

            final Iterator<E> collectionIterator = AbstractSortedCollection.super.collectionIterator(iterator); E current;

            @Override public boolean hasNext() { return collectionIterator.hasNext(); }
            @Override public E next() { return current = collectionIterator.next(); }
            @Override public void remove() { collectionIterator.remove(); propagateCount(current, -1); }
            @Override public void forEachRemaining(Consumer<? super E> action) {
                requireNonNull(action); collectionIterator.forEachRemaining(element ->
                        action.accept(current = element)); }
        };
    }

    @Override protected void init(Producer<? extends E> elements, BiConsumer<Bucket<E>, E> bucketAccumulation,
                                  BiFunction<E, E, Bucket<E>> bucketConstruction) {
        size = 0; super.init(elements, bucketAccumulation, bucketConstruction); }

    @Override protected Optional<Integer> getSize() {
        return ofNullable(size); }

    @Override protected void count(int number) {
        if (size != null) size = size + number; }
    @Override protected void countClear() {
        size = 0; }

    /**
     * Propagate collection clearance for all child sub-views.
     */
    protected void propagateClear(Integer size) {
        propagate((collection, range) -> {
            collection.countClear(); return true;
        });
    }

    /**
     * Propagate collection size change for all child sub-views.
     *
     * @param element the element to adjust count for
     * @param number the given size adjustment number
     */
    protected void propagateCount(E element, int number) {
        propagate((collection, range) -> {
            collection.count(number); return true;
        });
    }

    /**
     * Propagate collection size count for all child sub-views.
     */
    protected void propagateSize(int size) {
        propagate(new SizePropagation<>(new Range<>(map.comparator()), size));
    }

    /**
     * Apply the provided {@link Propagation} operation to this Collection sub-views
     * and determine whether to execute this operation on their sub-views by the returning boolean flag.
     *
     * @param propagation the {@link Propagation} operation to be applied
     */
    protected void propagate(Propagation<E> propagation) {
        for (SubReference<E> child = this.child; child != null; child = child.previous) child.refer((sub, range) -> {
            if (propagation.propagate(sub, range)) sub.propagate(propagation);
        });
    }

    @Override
    protected void serialize(ObjectOutputStream output) throws Exception {
        output.writeObject(map);

        AbstractSortedCollection<E, ?> sub;

        for (SubReference<E> child = this.child; child != null; child = child.previous)
            if ((sub = child.get()) != null) {
                output.writeObject(sub); output.writeObject(child.range);
            }
        output.writeObject(0);
    }

    @Override
    protected void deserialize(ObjectInputStream input) throws Exception {
        map = cast(input.readObject());

        while (true) {
            Object read = input.readObject();
            if (!(read instanceof AbstractSortedCollection))
                break;
            child = new SubReference<>(child, cast(read), cast(input.readObject()));
        }
    }


    /**
     * Sub-collection extension template of the {@link AbstractSortedCollection}.
     *
     * @param <R> the type of the root collection
     * @param <P> the type of the parent collection
     * @param <E> the element type
     * @param <M> the type of the internal {@link SortedMap}
     */
    protected static abstract class AbstractSortedSubCollection<R extends AbstractSortedCollection<E, ?>, P extends AbstractSortedCollection<E, ?>,
            E, M extends SortedMap<E, Object>> extends AbstractSortedCollection<E, M> {

        private static final long serialVersionUID = -2692092958047756457L;

        protected final Range<E> range;

        protected final R root; protected final P parent;

        protected AbstractSortedSubCollection(R root, P parent, M map, Range<E> range) {
            super(map); this.range = range; this.root = root; this.parent = parent; parent.child = new SubReference<>(parent.child, this, range); }

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
     * Sub-collection extension of the {@link AbstractSortedCollection}.
     *
     * @param <R> the type of the root collection
     * @param <P> the type of the parent collection
     * @param <E> the element type
     * @param <M> the type of the internal {@link SortedMap}
     */
    protected static class SortedSubCollection<R extends AbstractSortedCollection<E, ?>, P extends AbstractSortedCollection<E, ?>,
            E, M extends SortedMap<E, Object>> extends AbstractSortedSubCollection<R, P, E, M> {

        private static final long serialVersionUID = 3947071043558156236L;

        protected SortedSubCollection(R root, P parent, M map, Range<E> range) {
            super(root, parent, map, range); }

        @Override public SortedCollection<E> sub(E fromElement, E toElement) {
            return new SortedSubCollection<>(root, this, map.subMap(fromElement, toElement), range.subRange(fromElement, toElement)); }
        @Override public SortedCollection<E> head(E toElement) {
            return new SortedSubCollection<>(root, this, map.headMap(toElement), range.headRange(toElement)); }
        @Override public SortedCollection<E> tail(E fromElement) {
            return new SortedSubCollection<>(root, this, map.tailMap(fromElement), range.tailRange(fromElement)); }
    }


    /**
     * A generic, immutable representation of a range with optional lower and upper bounds.
     * <p>
     * This class supports inclusive and exclusive bounds and allows custom ordering via a {@link Comparator}.
     * If no comparator is provided, elements must be {@link Comparable}, and natural ordering is used.
     * </p>
     *
     * @param <V> the type of elements in the range
     */
    protected static class Range<V> implements Serializable {

        private static final long serialVersionUID = 7109706343983476145L;

        /**
         * The comparator used to determine element ordering.
         */
        protected final Comparator<? super V> comparator;

        /**
         * The lower and upper bound elements holding fields.
         */
        protected final V fromElement, toElement;

        /**
         * The lower and upper bound inclusivity holding fields.
         */
        protected final boolean fromInclusive, toInclusive;

        /**
         * Construct an unbounded range with a specified comparator.
         *
         * @param comparator the comparator to determine ordering, or {@code null} to use natural ordering
         */
        public Range(Comparator<? super V> comparator) {
            this(comparator, null, true, null, true);
        }

        /**
         * Construct a range with specified bounds and inclusivity.
         *
         * @param comparator the comparator to determine ordering, or {@code null} to use natural ordering
         * @param fromElement the lower bound element, or {@code null} for no lower bound
         * @param fromInclusive {@code true} if the lower bound is inclusive, {@code false} if exclusive
         * @param toElement the upper bound element, or {@code null} for no upper bound
         * @param toInclusive {@code true} if the upper bound is inclusive, {@code false} if exclusive
         */
        public Range(Comparator<? super V> comparator, V fromElement, boolean fromInclusive, V toElement, boolean toInclusive) {
            this.comparator = comparator != null ? comparator : naturalComparator();
            this.fromElement = fromElement; this.fromInclusive = fromInclusive;
            this.toElement = toElement; this.toInclusive = toInclusive;
        }

        /**
         * Check whether a given value falls within the range.
         *
         * @param value the value to check
         * @return {@code true} if the value is within the range, {@code false} otherwise
         */
        public boolean bounds(V value) {
            return fromBefore(this, value, true) && toAfter(this, value, true);
        }

        /**
         * Determine whether this range has the same lower bound as the given range.
         *
         * @param range the range to compare
         * @return {@code true} if both ranges have the same lower bound and inclusivity, {@code false} otherwise
         */
        public boolean fromAtFrom(Range<V> range) {
            return fromElement == null ? range.fromElement == null
                    : fromElement == range.fromElement && fromInclusive == range.fromInclusive;
        }

        /**
         * Determine whether this range has the same upper bound as the given range.
         *
         * @param range the range to compare
         * @return {@code true} if both ranges have the same upper bound and inclusivity, {@code false} otherwise
         */
        public boolean toAtTo(Range<V> range) {
            return toElement == null ? range.toElement == null
                    : toElement == range.toElement && toInclusive == range.toInclusive;
        }

        /**
         * Determine whether this range's lower bound is strictly before the given range's lower bound.
         *
         * @param range the range to compare
         * @return {@code true} if this range starts before the given range, {@code false} otherwise
         */
        public boolean fromBeforeFrom(Range<V> range) {
            return range.fromElement != null && fromBefore(this, range.fromElement, range.fromInclusive);
        }

        /**
         * Determine whether this range's lower bound is strictly before the given range's upper bound.
         *
         * @param range the range to compare
         * @return {@code true} if this range starts before the given range's upper bound, {@code false} otherwise
         */
        public boolean fromBeforeTo(Range<V> range) {
            return range.toElement == null || fromBefore(this, range.toElement, range.toInclusive);
        }

        /**
         * Determine whether this range's upper bound is strictly after the given range's upper bound.
         *
         * @param range the range to compare
         * @return {@code true} if this range ends after the given range, {@code false} otherwise
         */
        public boolean toAfterTo(Range<V> range) {
            return range.toElement != null && toAfter(this, range.toElement, range.toInclusive);
        }

        /**
         * Determine whether this range's upper bound is strictly after the given range's lower bound.
         *
         * @param range the range to compare
         * @return {@code true} if this range ends after the given range's lower bound, {@code false} otherwise
         */
        public boolean toAfterFrom(Range<V> range) {
            return range.fromElement == null || toAfter(this, range.fromElement, range.fromInclusive);
        }

        /**
         * Return a sub-range with a specified lower and upper bound.
         *
         * @param fromElement the new lower bound
         * @param toElement the new upper bound
         * @return a new {@link Range} instance
         */
        public Range<V> subRange(V fromElement, V toElement) {
            return subRange(fromElement, true, toElement, false);
        }

        /**
         * Return a sub-range that extends from the current lower bound to a new upper bound.
         *
         * @param toElement the new upper bound
         * @return a new {@link Range} instance
         */
        public Range<V> headRange(V toElement) {
            return headRange(toElement, false);
        }

        /**
         * Return a sub-range that extends from a new lower bound to the current upper bound.
         *
         * @param fromElement the new lower bound
         * @return a new {@link Range} instance
         */
        public Range<V> tailRange(V fromElement) {
            return tailRange(fromElement, true);
        }

        /**
         * Return a sub-range from the current lower bound to a new upper bound with specified inclusivity.
         *
         * @param toElement the new upper bound
         * @param toInclusive whether the new upper bound is inclusive
         * @return a new {@link Range} instance
         */
        public Range<V> headRange(V toElement, boolean toInclusive) {
            return subRange(fromElement, fromInclusive, toElement, toInclusive);
        }

        /**
         * Return a sub-range from a new lower bound to the current upper bound with specified inclusivity.
         *
         * @param fromElement the new lower bound
         * @param fromInclusive whether the new lower bound is inclusive
         * @return a new {@link Range} instance
         */
        public Range<V> tailRange(V fromElement, boolean fromInclusive) {
            return subRange(fromElement, fromInclusive, toElement, toInclusive);
        }

        /**
         * Return a sub-range with specified bounds and inclusivity.
         *
         * @param fromElement the new lower bound
         * @param fromInclusive whether the new lower bound is inclusive
         * @param toElement the new upper bound
         * @param toInclusive whether the new upper bound is inclusive
         * @return a new {@link Range} instance
         */
        public Range<V> subRange(V fromElement, boolean fromInclusive, V toElement, boolean toInclusive) {
            return new Range<>(comparator, fromElement, fromInclusive, toElement, toInclusive);
        }

        private static <E> boolean fromBefore(Range<E> range, E value, boolean inclusive) {
            int comparison = range.fromElement == null ? -1 : range.comparator.compare(range.fromElement, value);
            return comparison < 0 || (comparison == 0 && (range.fromInclusive || !inclusive));
        }

        private static <E> boolean toAfter(Range<E> range, E value, boolean inclusive) {
            int comparison = range.toElement == null ? 1 : range.comparator.compare(range.toElement, value);
            return comparison > 0 || (comparison == 0 && (range.toInclusive || !inclusive));
        }

        private static <E> Comparator<E> naturalComparator() {
            return (Comparator<E> & Serializable) (e1, e2) -> Cast.<Comparable<E>>cast(e1).compareTo(e2);
        }
    }

    /**
     * A functional interface representing a propagation mechanism for an {@link AbstractSortedCollection}.
     * <p>
     * Implementations define how propagation should be handled for a given collection and range.
     * This can be used for efficient updates, event propagation, or range-based filtering.
     * </p>
     *
     * @param <E> the type of elements in the collection
     */
    @FunctionalInterface
    protected interface Propagation<E> {

        /**
         * Propagate an operation to the given collection within the specified range.
         *
         * @param collection the collection to propagate to
         * @param range the range within which to propagate
         * @return {@code true} if propagation should continue, {@code false} to stop propagation
         */
        boolean propagate(AbstractSortedCollection<E, ?> collection, Range<E> range);

        /**
         * Propagate this operation to the root collection.
         * <p>
         * This method starts propagation with an unbounded range using the collection's comparator,
         * and then applies further propagation using {@link AbstractSortedCollection#propagate(Propagation)}.
         * </p>
         *
         * @param root the root collection to begin propagation from
         */
        default void propagateRoot(AbstractSortedCollection<E, ?> root) {
            propagate(root, new Range<>(root.map.comparator())); root.propagate(this); }

        /**
         * Return a propagation instance that stops when a specific collection is reached.
         * <p>
         * This method creates a wrapper around the current propagation logic, ensuring that
         * propagation halts when the specified collection {@code c} is encountered.
         * </p>
         *
         * @param c the collection at which propagation should stop
         * @return a modified {@link Propagation} instance that stops at {@code c}
         */
        default Propagation<E> untilCollection(AbstractSortedCollection<E, ?> c) {
            return (collection, range) -> {
                if (collection == c) return false; return propagate(collection, range);
            }; }
    }

    /**
     * An abstract implementation of {@link Propagation} that operates within a specified {@link Range}.
     * <p>
     * This class provides a structured way to propagate operations within a given range,
     * delegating the actual propagation logic to subclasses.
     * </p>
     *
     * @param <E> the type of elements in the collection
     */
    protected abstract static class AbstractRangePropagation<E> implements Propagation<E> {

        /**
         * The range holding field.
         */
        protected final Range<E> range;

        /**
         * Construct the object with the specified range.
         *
         * @param range the range within which propagation should occur
         */
        protected AbstractRangePropagation(Range<E> range) {
            this.range = range; }

        @Override public boolean propagate(AbstractSortedCollection<E, ?> collection, Range<E> range) {
            return propagate(this.range, collection, range); }

        /**
         * Perform the actual propagation logic using the given source range.
         *
         * @param sourceRange the originating range for the propagation
         * @param collection the collection to propagate to
         * @param range the range within which to propagate
         * @return {@code true} if propagation should continue, {@code false} to stop propagation
         */
        public abstract boolean propagate(Range<E> sourceRange, AbstractSortedCollection<E, ?> collection, Range<E> range);
    }

    /**
     * A {@link Propagation} implementation that propagates a count modification for a specific element.
     *
     * @param <E> the type of elements in the collection
     */
    protected static class CountPropagation<E> implements Propagation<E> {

        /**
         * The element holding field.
         */
        protected final E element;

        /**
         * The count holding field.
         */
        protected final Integer count;

        /**
         * Construct the object for the specified element and count.
         *
         * @param element the element whose count should be propagated
         * @param count the count value to propagate
         */
        protected CountPropagation(E element, Integer count) {
            this.element = element; this.count = count; }

        @Override public boolean propagate(AbstractSortedCollection<E, ?> collection, Range<E> range) {
            if (!range.bounds(element)) return false; collection.count(count); return true; }
    }

    /**
     * A {@link Propagation} implementation that clears or adjusts the size of a collection
     * based on the specified range constraints.
     *
     * @param <E> the type of elements in the collection
     */
    protected static class ClearPropagation<E> extends AbstractRangePropagation<E> {

        /**
         * The count adjustment holding field.
         */
        protected final Integer count;

        /**
         * Construct the object with the specified range and count.
         *
         * @param range the range within which elements should be cleared
         * @param count the count adjustment to apply, or {@code null} to reset the size
         */
        protected ClearPropagation(Range<E> range, Integer count) {
            super(range); this.count = count; }

        @Override public boolean propagate(Range<E> sourceRange, AbstractSortedCollection<E, ?> collection, Range<E> range) {
            if (sourceRange.fromBeforeFrom(range))
                if (sourceRange.toAfterTo(range))
                    collection.size = 0;
                else if (sourceRange.toAfterFrom(range))
                    collection.size = null;
                else return false;
            else if(sourceRange.toAfterTo(range))
                if (sourceRange.fromBeforeTo(range))
                    collection.size = null;
                else return false;
            else if (count != null) collection.count(-count);
            else collection.size = null; return true; }
    }

    /**
     * A {@link Propagation} implementation that sets or adjusts the size of a collection
     * based on the specified range constraints.
     *
     * @param <E> the type of elements in the collection
     */
    protected static class SizePropagation<E> extends AbstractRangePropagation<E> {

        /**
         * The target size holding field.
         */
        protected final int size;

        /**
         * Construct the object with the specified range and size.
         *
         * @param range the range within which the size propagation should be applied
         * @param size the target size to set when the range fully matches
         */
        protected SizePropagation(Range<E> range, int size) {
            super(range); this.size = size; }

        @Override public boolean propagate(Range<E> sourceRange, AbstractSortedCollection<E, ?> collection, Range<E> range) {
            if (sourceRange.fromAtFrom(range) && sourceRange.toAtTo(range)) {
                if (collection.size == null)
                    collection.size = size;
                return true;
            } return !(sourceRange.fromBeforeFrom(range) || sourceRange.toAfterTo(range)); }
    }

    /**
     * The {@link WeakReference} link for the child sub-views.
     *
     * @param <E> the element type
     */
    protected static class SubReference<E> extends WeakReference<AbstractSortedCollection<E, ?>> {

        /**
         * Sub-view {@link Range} holding field.
         */
        protected final Range<E> range;

        /**
         * Previous sub-view holding field.
         */
        protected final SubReference<E> previous;

        /**
         * Construct the reference by the given sub-view and previous linked sub-view reference.
         *
         * @param previous   the previous linked sub-view reference
         * @param collection the given sub-view to link
         */
        protected SubReference(SubReference<E> previous, AbstractSortedCollection<E, ?> collection, Range<E> range) {
            super(collection); this.previous = previous; this.range = range;
        }

        /**
         * Consume this reference object's sub-view referent and {@link Range}.
         */
        public void refer(BiConsumer<AbstractSortedCollection<E, ?>, Range<E>> consumer) {
            ofNullable(get()).ifPresent(collection -> consumer.accept(collection, range));
        }
    }
}
