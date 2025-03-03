package org.moodminds.elemental;

import org.moodminds.elemental.AbstractHeapCollection.Bucket;
import org.moodminds.elemental.AbstractHeapCollection.Bucket.Node;

import java.io.InvalidObjectException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Comparator;
import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.Map;
import java.util.Spliterator;
import java.util.function.Consumer;
import java.util.function.Function;

import static java.lang.String.format;
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
                    () -> { E element = bucket.iterator().next(); map.put(element, element); }, () -> map.remove(current));

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
    protected Iterator<E> containerIterator(Iterator<Map.Entry<E, Object>> entriesIterator, Function<Bucket<E>, Iterator<E>> bucketIteration, Runnable removal) {
        return new Iterator<E>() {

            Map.Entry<E, Object> entry; Bucket<E> bucket; Iterator<E> bucketIterator;

            final Iterator<E> iterator = AbstractHeapCollection.super.containerIterator(new Iterator<Map.Entry<E, Object>>() {

                @Override public boolean hasNext() { return entriesIterator.hasNext(); }
                @Override public Map.Entry<E, Object> next() { bucket = null; bucketIterator = null; return entry = entriesIterator.next(); }

            }, bucket -> this.bucketIterator = bucketIterator(bucket, bucketIteration.apply(this.bucket = bucket),
                            () -> entry.setValue(bucket.iterator().next()), entriesIterator::remove),
                    () -> { if (bucketIterator != null) bucketIterator.remove(); else entriesIterator.remove();
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
    protected Spliterator<E> containerSpliterator(Spliterator<Map.Entry<E, Object>> entriesSpliterator, boolean useSourceSize, boolean isDistinct, Integer knownSize) {
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
     * Return an iterator that iterates over the elements of the specified bucket.
     * Provide additional functionality to handle bucket-specific operations,
     * including element removal and triggering bucket collapse or removal when necessary.
     *
     * @param bucket the bucket containing the elements to iterate over
     * @param bucketIterator the iterator to be used for iterating over the bucket's elements
     * @param bucketCollapse the {@link Runnable} to be run when the bucket collapses (i.e., when only one element remains)
     * @param bucketRemoval the {@link Runnable} to be run when the bucket is removed (i.e., when the bucket no longer contains any elements)
     * @return an {@link Iterator} that iterates over the bucket's elements and supports element removal and bucket management
     */
    private Iterator<E> bucketIterator(Bucket<E> bucket, Iterator<E> bucketIterator, Runnable bucketCollapse, Runnable bucketRemoval) {
        return new Iterator<E>() {

            final Iterator<E> iterator = AbstractHeapCollection.super.iterator(bucket, bucketIterator);

            @Override public boolean hasNext() { return iterator.hasNext(); }
            @Override public E next() { return iterator.next(); }
            @Override public void remove() {
                bucketIterator.remove();
                if (bucket.size() == 1) bucketCollapse.run();
                else if (!bucket.contains()) bucketRemoval.run(); }
            @Override public void forEachRemaining(Consumer<? super E> action) {
                iterator.forEachRemaining(action); }
        };
    }

    /**
     * Represents a bucket in the Collection, extending {@link AbstractLinkSequence}.
     * This class is responsible for managing a collection of elements in a linked sequence.
     * Each bucket can contain multiple elements and provides functionality for managing those elements
     * efficiently within the broader structure.
     *
     * @param <E> the type of elements contained in this bucket
     */
    protected static class Bucket<E> extends AbstractLinkSequence<E, Node<E>> {

        private static final long serialVersionUID = 2314740533859790494L;

        /**
         * Head {@link Node} node holding field.
         */
        protected transient Node<E> tail;

        @Override
        protected SequenceIterator<E> iterator(Node<E> previous, int index, int s, Runnable removal) {
            return new SequenceIterator<E>() {

                AbstractLinkSequenceIterator<E, Node<E>> iterator;

                {
                    iterator = (AbstractLinkSequenceIterator<E, Node<E>>) Bucket.super.iterator(previous, index, s, () -> {
                        unlinkNext(iterator.previous); size--;
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

        @Override
        public Sequence<E> sub(int fromIndex, int toIndex) {
            return new SubBucket(0, size, fromIndex, toIndex);
        }

        protected Bucket<E> add(E element) {
            linkNext(new Node<>(element)); size++; return this;
        }

        protected void linkNext(Node<E> next) {
            if (tail != null)
                tail.linkNext(tail = next);
            else head = tail = next;
        }

        protected void unlinkNext(Node<E> previous) {
            if (previous == null)
                if (head == tail) head = tail = null;
                else head = head.next;
            else {
                if (previous.next == tail)
                    tail = previous;
                previous.unlinkNext();
            }
        }

        @Override protected void serialize(ObjectOutputStream output) throws Exception {
            output.writeInt(size); for (E element : this)
                output.writeObject(element); }
        @Override protected void deserialize(ObjectInputStream input) throws Exception {
            int size; if ((size = input.readInt()) < 0)
                throw new InvalidObjectException("Negative size: " + size);
            while (this.size < size)
                add(cast(input.readObject())); }


        /**
         * Represents a node in the linked structure, implementing the {@link Link} interface.
         *
         * @param <E> the type of element contained in the node
         */
        protected static class Node<E> extends Link<E, Node<E>> {

            protected Node(E item) { super(item); }
        }

        /**
         * A formal implementation of a sub-bucket within the bucket structure.
         * This class is intended for holding elements in a sequence, but it does not support element removal.
         * Any modification to the bucket during iteration will result in a {@link ConcurrentModificationException}.
         */
        protected class SubBucket extends AbstractSequence<E> {

            private final int bucketSize; private final Node<E> bucketTail; protected int offset, size;

            protected SubBucket(int offset, int size, int fromIndex, int toIndex) {
                this(Bucket.this.size, Bucket.this.tail, offset, size, fromIndex, toIndex); }

            private SubBucket(int bucketSize, Node<E> bucketTail, int offset, int size, int fromIndex, int toIndex) {
                if (fromIndex < 0)
                    throw new IndexOutOfBoundsException(format("fromIndex = %d", fromIndex));
                if (toIndex > size)
                    throw new IndexOutOfBoundsException(format("toIndex = %d", toIndex));
                if (fromIndex > toIndex)
                    throw new IllegalArgumentException(format("fromIndex(%d) > toIndex(%d)", fromIndex, toIndex));
                this.bucketSize = bucketSize; this.bucketTail = bucketTail;
                this.offset = offset + fromIndex; this.size = toIndex - fromIndex; }

            @Override public int size() {
                checkMod(); return size; }
            @Override public <R extends E> R get(int index) {
                checkMod(); return cast(Bucket.this.link(offset + inBounds(index, size)).item); }
            @Override public Iterator<E> iterator() {
                checkMod(); return Bucket.super.iterator(offset == 0 ? null : link(offset - 1), 0, size); }
            @Override public SequenceIterator<E> iterator(int index) {
                checkMod(); return Bucket.super.iterator(offset + inBounds(index, size) == 0 ? null : link(offset + index - 1), index, size); }
            @Override public Spliterator<E> spliterator() {
                checkMod(); return super.spliterator(); }
            @Override public Sequence<E> sub(int fromIndex, int toIndex) {
                return new SubBucket(bucketSize, bucketTail, offset, size, fromIndex, toIndex); }

            void checkMod() {
                if (bucketSize != Bucket.this.size || bucketTail != Bucket.this.tail)
                    throw new ConcurrentModificationException(); }
        }
    }
}
