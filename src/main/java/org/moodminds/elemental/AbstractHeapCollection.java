package org.moodminds.elemental;

import org.moodminds.elemental.AbstractHeapCollection.Bucket;

import java.util.Comparator;
import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Spliterator;
import java.util.function.Consumer;
import java.util.function.Function;

import static java.util.Objects.requireNonNull;
import static java.util.Optional.ofNullable;
import static org.moodminds.sneaky.Cast.cast;

/**
 * A template implementation of the {@link Collection} interface,
 * which allows duplicates and is powered by an internal {@link Map}.
 *
 * @param <E> the element type
 * @param <M> the type of the internal {@link Map}
 */
public abstract class AbstractHeapCollection<E, M extends Map<E, Object>>
        extends AbstractHeapContainer<E, Bucket<E>, M> implements Collection<E> {

    private static final long serialVersionUID = 4988869826364597936L;

    /**
     * Construct the object with the given {@link M map}.
     *
     * @param map the given {@link M map}
     */
    protected AbstractHeapCollection(M map) {
        super(map);
    }

    /**
     * Construct the object with the given {@link M map}
     * and sequential single-threaded {@link Producer} of elements.
     *
     * @param map the given {@link M map}
     * @param elements the given sequential single-threaded {@link Producer} of elements
     */
    protected AbstractHeapCollection(M map, Producer<? extends E> elements) {
        super(map); init(elements);
    }

    @Override public boolean add(E element) {
        put(element); count(1); countMod(); return true; }
    @Override public void clear() {
        map.clear(); countClear(); countMod(); }

    @Override public Iterator<E> getAll(Object o) {
        return collectionIterator(super.getAll(o)); }
    @Override public Iterator<E> iterator() {
        return collectionIterator(super.iterator()); }
    @Override public Spliterator<E> spliterator() {
        return collectionSpliterator(super.spliterator()); }

    /**
     * Initialize the current object by providing elements via the specified {@link Producer}.
     * Process and add each element by invoking the {@code put} method and updating the count.
     *
     * @param elements the {@link Producer} that supplies elements to be processed
     */
    protected void init(Producer<? extends E> elements) {
        elements.provide(element -> { put(element); count(1); }); }

    /**
     * Put the specified element into the internal map, handling element placement
     * based on the current mapping and bucket conditions. If the element is already
     * mapped, it may be added to an existing or new {@link Bucket}.
     *
     * @param element the element to be added to the map
     */
    protected void put(E element) {
        Object value = map.get(element);
        if (!tryBucket(value, bucket -> bucket.add(element)))
            if (isMapped(element, value))
                map.put(cast(value), new Bucket<E>().add(cast(value)).add(element));
            else map.put(element, element);
    }

    /**
     * {@inheritDoc}
     *
     * @param value {@inheritDoc}
     * @param present {@inheritDoc}
     * @param removal {@inheritDoc}
     * @return {@inheritDoc}
     */
    @Override
    protected Iterator<E> iterator(Object value, boolean present, Runnable removal) {
        return new Iterator<E>() {

            OptionalIterator<E> iterator;

            {
                iterator = (OptionalIterator<E>) AbstractHeapCollection.super.iterator(value, present, () -> {
                    if (removal != null) removal.run();
                    iterator.present = false; map.remove(value);
                });
            }

            @Override public boolean hasNext() { return iterator.hasNext(); }
            @Override public E next() { return iterator.next(); }
            @Override public void remove() { iterator.remove(); }

            @Override public void forEachRemaining(Consumer<? super E> action) {
                iterator.forEachRemaining(action); }
        };
    }

    /**
     * {@inheritDoc}
     *
     * @param bucket {@inheritDoc}
     * @param bucketIterator {@inheritDoc}
     * @return {@inheritDoc}
     */
    @Override
    protected Iterator<E> iterator(Bucket<E> bucket, Iterator<E> bucketIterator) {
        return new Iterator<E>() {

            E current; final Iterator<E> iterator = bucketIterator(bucket, AbstractHeapCollection.super.iterator(bucket, bucketIterator),
                    element -> map.put(element, element), () -> map.remove(current));

            @Override public boolean hasNext() { return iterator.hasNext(); }
            @Override public E next() { return current = iterator.next(); }
            @Override public void remove() { iterator.remove(); }
            @Override public void forEachRemaining(Consumer<? super E> action) {
                iterator.forEachRemaining(element -> current = element); }
        };
    }

    /**
     * {@inheritDoc}
     *
     * @param entriesIterator {@inheritDoc}
     * @param bucketIteration {@inheritDoc}
     * @param removal {@inheritDoc}
     * @return {@inheritDoc}
     */
    @Override
    protected Iterator<E> containerIterator(Iterator<Entry<E, Object>> entriesIterator, Function<Bucket<E>, Iterator<E>> bucketIteration, Runnable removal) {
        return new Iterator<E>() {

            Entry<E, Object> entry; Bucket<E> bucket; Iterator<E> bucketIterator;

            final Iterator<E> iterator = AbstractHeapCollection.super.containerIterator(new Iterator<Entry<E, Object>>() {

                @Override public boolean hasNext() { return entriesIterator.hasNext(); }
                @Override public Entry<E, Object> next() { bucket = null; bucketIterator = null; return entry = entriesIterator.next(); }

            }, bucket -> this.bucketIterator = bucketIterator(bucket, bucketIteration.apply(this.bucket = bucket), entry::setValue, entriesIterator::remove),
                    () -> { if (removal != null) removal.run(); if (bucketIterator != null) bucketIterator.remove(); else entriesIterator.remove();
            });

            @Override public boolean hasNext() { return iterator.hasNext(); }
            @Override public E next() { return iterator.next(); }
            @Override public void remove() { iterator.remove(); }
            @Override public void forEachRemaining(Consumer<? super E> action) {
                iterator.forEachRemaining(action); }
        };
    }

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
    protected Spliterator<E> containerSpliterator(Spliterator<Entry<E, Object>> entriesSpliterator, boolean useSourceSize, boolean isDistinct, Integer knownSize) {
        return new Spliterator<E>() {

            final Spliterator<E> spliterator = AbstractHeapCollection.super.containerSpliterator(entriesSpliterator, useSourceSize, isDistinct, knownSize);

            @Override public boolean tryAdvance(Consumer<? super E> action) { return spliterator.tryAdvance(action); }
            @Override public Spliterator<E> trySplit() { return spliterator.trySplit(); }
            @Override public long estimateSize() { return spliterator.estimateSize(); }
            @Override public void forEachRemaining(Consumer<? super E> action) { spliterator.forEachRemaining(action); }
            @Override public long getExactSizeIfKnown() { return spliterator.getExactSizeIfKnown(); }
            @Override public Comparator<? super E> getComparator() { return spliterator.getComparator(); }

            @Override public int characteristics() {
                return spliterator.characteristics() & ~IMMUTABLE; }
            @Override public boolean hasCharacteristics(int characteristics) {
                if ((characteristics & IMMUTABLE) != 0) return false;
                return spliterator.hasCharacteristics(characteristics); }
        };
    }

    /**
     * Return an {@link Iterator} that wraps the given iterator and provides additional checks for modification
     * during iteration. Support element removal, ensure consistency with the Collection's modification
     * state, and track the modification count.
     *
     * @param iterator the underlying iterator to wrap and iterate over
     * @return an {@link Iterator} that performs modification checks and supports element removal
     */
    protected Iterator<E> collectionIterator(Iterator<E> iterator) {
        return new Iterator<E>() {

            int expectedMod = totalMod();

            @Override public boolean hasNext() {
                return iterator.hasNext(); }
            @Override public E next() {
                checkMod(expectedMod); return iterator.next(); }
            @Override public void remove() {
                checkMod(expectedMod); iterator.remove(); count(-1); countMod(); expectedMod++; }

            @Override public void forEachRemaining(Consumer<? super E> action) {
                requireNonNull(action); checkMod(expectedMod); iterator.forEachRemaining(element -> {
                    action.accept(element); checkMod(expectedMod);
                }); }
        };
    }

    /**
     * Return a {@link Spliterator} that wraps the given spliterator and performs additional checks for modification
     * during traversal. Ensure that the Collection's modification state remains consistent during iteration
     * and support efficient parallel processing by allowing splitting.
     *
     * @param spliterator the underlying {@link Spliterator} to wrap and iterate over
     * @return a {@link Spliterator} that performs modification checks and supports splitting
     */
    protected Spliterator<E> collectionSpliterator(Spliterator<E> spliterator) {
        return new Spliterator<E>() {

            final int expectedMod = totalMod();

            @Override public boolean tryAdvance(Consumer<? super E> action) {
                checkMod(expectedMod); return spliterator.tryAdvance(action); }
            @Override public long estimateSize() {
                return spliterator.estimateSize(); }
            @Override public long getExactSizeIfKnown() {
                return spliterator.getExactSizeIfKnown(); }
            @Override public int characteristics() {
                return spliterator.characteristics() & ~IMMUTABLE; }
            @Override public boolean hasCharacteristics(int characteristics) {
                return spliterator.hasCharacteristics(characteristics); }
            @Override public Comparator<? super E> getComparator() {
                return spliterator.getComparator(); }
            @Override public Spliterator<E> trySplit() {
                return ofNullable(spliterator.trySplit())
                        .map(AbstractHeapCollection.this::collectionSpliterator)
                        .orElse(null); }

            @Override public void forEachRemaining(Consumer<? super E> action) {
                requireNonNull(action); checkMod(expectedMod); spliterator.forEachRemaining(element -> {
                    action.accept(element); checkMod(expectedMod);
                }); }
        };
    }

    @Override protected String toStringThis() {
        return "(this Collection)"; }
    @Override protected boolean isBucket(Object value) {
        return value instanceof Bucket; }

    /**
     * Check if the Collection has been modified since the specified modification count.
     * If the Collection's modification count has changed, a {@link ConcurrentModificationException} is thrown.
     *
     * @param expectedMod the expected modification count to compare against the current modification count
     * @throws ConcurrentModificationException if the Collection has been modified since the specified modification count
     */
    protected void checkMod(int expectedMod) {
        if (totalMod() != expectedMod) throw new ConcurrentModificationException(); }

    /**
     * Return the total modification count of this collection.
     * This count reflects the number of structural modifications made to the collection.
     *
     * @return the total modification count of the collection
     */
    protected abstract int totalMod();

    /**
     * Increment the modification count of this collection.
     * This method should be called after any structural modification to the collection
     * to ensure that the modification count remains consistent.
     */
    protected abstract void countMod();

    /**
     * Reset or clear the modification count of this collection.
     * This method can be used to reset the modification tracking after bulk operations
     * or other scenarios where the modification count should be cleared.
     */
    protected abstract void countClear();

    /**
     * Adjust the size of this Collection by the specified amount.
     * This method should be called whenever the size of the Collection changes (e.g., elements are added or removed).
     *
     * @param number the amount by which to adjust the size
     */
    protected abstract void count(int number);

    /**
     * Return an {@link Iterator} for iterating over the elements of the given {@code bucket}.
     * <p>
     * Delegates to the standard iteration mechanism of the parent class while providing additional behavior during element removal:
     * <ul>
     *     <li>When an element is removed, if the bucket contains only one remaining element,
     *         that element is passed to {@code bucketCollapse} {@link Consumer}.</li>
     *     <li>If the bucket becomes empty after removal, {@code bucketRemoval} {@link Runnable} is executed.</li>
     * </ul>
     * </p>
     *
     * @param bucket the bucket containing the elements to iterate over
     * @param bucketIterator the iterator over the elements within the bucket
     * @param bucketCollapse the action to perform when the bucket is reduced to a single element
     * @param bucketRemoval the action to perform when the bucket becomes empty
     * @return an iterator over the elements in the given bucket
     */
    private Iterator<E> bucketIterator(Bucket<E> bucket, Iterator<E> bucketIterator, Consumer<E> bucketCollapse, Runnable bucketRemoval) {
        return new Iterator<E>() {

            final Iterator<E> iterator = AbstractHeapCollection.super.iterator(bucket, bucketIterator);

            @Override public boolean hasNext() { return iterator.hasNext(); }
            @Override public E next() { return iterator.next(); }
            @Override public void remove() { bucketIterator.remove();
                if (bucket.size() == 1) bucketCollapse.accept(bucket.iterator().next());
                else if (!bucket.contains()) bucketRemoval.run(); }
            @Override public void forEachRemaining(Consumer<? super E> action) {
                iterator.forEachRemaining(action); }
        };
    }

    /**
     * Represents a bucket in the Collection, extending {@link AbstractLinkInitialSequence}.
     * This class is responsible for managing a collection of elements in a linked sequence.
     * Each bucket can contain multiple elements and provides functionality for managing those elements
     * efficiently within the broader structure.
     *
     * @param <E> the type of elements contained in this bucket
     */
    protected static class Bucket<E> extends AbstractLinkInitialSequence<E> {

        private static final long serialVersionUID = -5964830056992665020L;

        /**
         * Head {@link Link} node holding field.
         */
        protected transient Link<E> tail;

        protected Bucket<E> add(E element) {
            put(tail, tail = new Link<>(element)); return this;
        }

        @Override
        protected SequenceIterator<E> iterator(Link<E> previous, int index, int s, Runnable removal) {
            return new SequenceIterator<E>() {

                AbstractLinkSequenceIterator<E, Link<E>> iterator;

                {
                    iterator = (AbstractLinkSequenceIterator<E, Link<E>>) Bucket.super.iterator(previous, index, s, () -> {
                        if (removal != null) removal.run(); delete(iterator.previous);
                    });
                }

                @Override public boolean hasNext() { return iterator.hasNext(); }
                @Override public E next() { return iterator.next(); }
                @Override public int nextIndex() { return iterator.nextIndex(); }
                @Override public void remove() { iterator.remove(); }
                @Override public void forEachRemaining(Consumer<? super E> action) {
                    iterator.forEachRemaining(action); }
            };
        }
        
        protected void delete(Link<E> previous) {
            unlinkNext(previous); size--;
        }

        @Override
        protected void unlinkNext(Link<E> previous) {
            super.unlinkNext(previous);
            if (previous == null) {
                if (head == null) tail = null;
            } else if (previous.next == null) tail = previous;
        }
    }
}
