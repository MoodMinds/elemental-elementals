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
import java.util.Map.Entry;
import java.util.Spliterator;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;

import static java.util.Objects.requireNonNull;
import static java.util.Optional.ofNullable;
import static org.moodminds.sneaky.Cast.cast;

/**
 * An abstract base for {@link Collection}s that do not preserve insertion order.
 * <p>
 * This Collection arranges elements internally using bucket-based structure,
 * meaning the iteration order is not defined and may differ from insertion order.
 * The primary purpose is to provide efficient grouping and accumulation mechanisms
 * without maintaining sequential order.
 *
 * @param <E> the element type
 * @param <M> the type of the internal {@link Map}
 */
public abstract class AbstractHeapCollection<E, M extends Map<E, Object>>
        extends AbstractHeapContainer<E, Bucket<E>, M> implements Collection<E> {

    private static final long serialVersionUID = 4988869826364597936L;

    /**
     * A reusable {@link BiConsumer} that appends an element to an existing {@link Bucket}.
     */
    protected transient final BiConsumer<Bucket<E>, E> bucketAccumulation = Bucket::put;

    /**
     * A reusable {@link BiFunction} that creates a new {@link Bucket} from two elements:
     * the existing single element previously associated with a key, and the new incoming element.
     */
    protected transient final BiFunction<E, E, Bucket<E>> bucketConstruction = Bucket::new;

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
        super(map, elements, Bucket::put, Bucket::new);
    }

    @Override public boolean add(E element) {
        put(bucketAccumulation, bucketConstruction, element); countMod(); return true; }
    @Override public void clear() {
        map.clear(); countClear(); countMod(); }

    @Override public Iterator<E> getAll(Object o) {
        return collectionIterator(super.getAll(o)); }
    @Override public Iterator<E> iterator() {
        return collectionIterator(super.iterator()); }
    @Override public Spliterator<E> spliterator() {
        return collectionSpliterator(super.spliterator()); }

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
        return new Object() {
            OptionalIterator<E> iterator;
            {
                iterator = (OptionalIterator<E>) AbstractHeapCollection.super.iterator(value, present, () -> {
                    if (removal != null) removal.run(); iterator.present = false; map.remove(value); count(-1);
                });
            }
        }.iterator;
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
            @Override public void remove() { iterator.remove(); count(-1); }
            @Override public void forEachRemaining(Consumer<? super E> action) {
                iterator.forEachRemaining(element -> action.accept(current = element)); }
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
        return new Object() {

            Entry<E, Object> entry; Bucket<E> bucket; Iterator<E> bucketIterator;

            final AbstractIterator<E> iterator = (AbstractIterator<E>) AbstractHeapCollection.super.containerIterator(new Iterator<Entry<E, Object>>() {

                @Override public boolean hasNext() { return entriesIterator.hasNext(); }
                @Override public Entry<E, Object> next() { bucket = null; bucketIterator = null; return entry = entriesIterator.next(); }

            }, bucket -> this.bucketIterator = bucketIterator(bucket, bucketIteration.apply(this.bucket = bucket), entry::setValue, entriesIterator::remove),
                    () -> { if (removal != null) removal.run(); if (bucketIterator != null) bucketIterator.remove(); else entriesIterator.remove();
            });
        }.iterator;
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

            @Override public boolean hasNext() { return iterator.hasNext(); }
            @Override public E next() { checkMod(); return iterator.next(); }
            @Override public void remove() { checkMod(); iterator.remove(); countMod(); expectedMod++; }

            @Override public void forEachRemaining(Consumer<? super E> action) {
                requireNonNull(action); checkMod(); iterator.forEachRemaining(action
                        .andThen(unused -> checkMod())); }

            void checkMod() { AbstractHeapCollection.this.checkMod(expectedMod); }
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

            @Override public boolean tryAdvance(Consumer<? super E> action) { checkMod(); return spliterator.tryAdvance(action); }
            @Override public long estimateSize() { return spliterator.estimateSize(); }
            @Override public long getExactSizeIfKnown() { return spliterator.getExactSizeIfKnown(); }
            @Override public int characteristics() { return spliterator.characteristics() & ~IMMUTABLE; }
            @Override public boolean hasCharacteristics(int characteristics) { return spliterator.hasCharacteristics(characteristics); }
            @Override public Comparator<? super E> getComparator() { return spliterator.getComparator(); }
            @Override public Spliterator<E> trySplit() { return ofNullable(spliterator.trySplit())
                    .map(AbstractHeapCollection.this::collectionSpliterator).orElse(null); }

            @Override public void forEachRemaining(Consumer<? super E> action) {
                requireNonNull(action); checkMod(); spliterator.forEachRemaining(action
                        .andThen(unused -> checkMod())); }

            void checkMod() { AbstractHeapCollection.this.checkMod(expectedMod); }
        };
    }

    @Override protected String toStringThis() {
        return "(this Collection)"; }
    @Override protected boolean isBucket(Object value) {
        return value instanceof Bucket; }

    /**
     * Check if the Collection has been modified since the specified modification count.
     * If the Collection's modification count has changed, throw a {@link ConcurrentModificationException}.
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
     * Return an {@link Iterator} for iterating over the elements of the given {@code bucket}
     * providing additional behavior during element removal:
     * <ul>
     *     <li>When an element is removed, if the bucket contains only one remaining element,
     *         that element is passed to {@code bucketCollapse} {@link Consumer}.</li>
     *     <li>If the bucket becomes empty after removal, {@code bucketRemoval} {@link Runnable} is executed.</li>
     * </ul>
     *
     * @param bucket the bucket containing the elements to iterate over
     * @param bucketIterator the iterator over the elements within the bucket
     * @param bucketCollapse the action to perform when the bucket is reduced to a single element
     * @param bucketRemoval the action to perform when the bucket becomes empty
     * @return an iterator over the elements in the given bucket
     */
    private Iterator<E> bucketIterator(Bucket<E> bucket, Iterator<E> bucketIterator, Consumer<E> bucketCollapse, Runnable bucketRemoval) {
        return new Iterator<E>() {

            @Override public boolean hasNext() { return bucketIterator.hasNext(); }
            @Override public E next() { return bucketIterator.next(); }
            @Override public void remove() { bucketIterator.remove();
                if (bucket.size() == 1) bucketCollapse.accept(bucket.iterator().next());
                else if (!bucket.contains()) bucketRemoval.run(); }
            @Override public void forEachRemaining(Consumer<? super E> action) {
                bucketIterator.forEachRemaining(action); }
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

        private static final long serialVersionUID = -5964830056992665020L;

        /**
         * Tail {@link Node} node holding field.
         */
        protected transient Node<E> tail;

        public Bucket(E first, E second) { put(first); put(second); }

        @Override
        public Sequence<E> sub(int fromIndex, int toIndex) {
            return new SubBucket(size, fromIndex, toIndex);
        }

        public void put(E element) {
            link(tail, new Node<>(element)); size++;
        }

        @Override
        protected void link(Node<E> previous, Node<E> next) {
            super.link(previous, next); if (next.next == null) tail = next;
        }

        @Override
        protected void unlink(Node<E> previous) {
            super.unlink(previous);
            if (previous == null) {
                if (head == null) tail = null;
            } else if (previous.next == null) tail = previous;
        }

        @Override
        protected TailedSequenceIterator<E> iterator(Node<E> previous, Sequence<E> sequence, int index, Runnable removal) {
            return new Object() {
                AbstractLinkSequenceIterator<E, Node<E>> iterator;
                {
                    iterator = (AbstractLinkSequenceIterator<E, Node<E>>) Bucket.super.iterator(previous, sequence, index, () -> {
                        if (removal != null) removal.run(); unlink(iterator.previous); iterator.next = null; size--; iterator.index--;
                    });
                }
            }.iterator;
        }

        @Override
        protected Node<E> previous(Node<E> link) {
            return null;
        }

        @Override
        protected void serialize(ObjectOutputStream output) throws Exception {
            output.writeInt(size); for (E element : this)
                output.writeObject(element);
        }

        @Override
        protected void deserialize(ObjectInputStream input) throws Exception {
            int size; if ((size = input.readInt()) < 0)
                throw new InvalidObjectException("Negative size: " + size);
            while (this.size < size)
                put(cast(input.readObject()));
        }

        /**
         * Implementation of the {@link AbstractLinkSequence.Node}.
         *
         * @param <E> the type of elements
         */
        protected static class Node<E> extends AbstractLinkSequence.Node<E, Node<E>> {

            /**
             * Construct the object with the specified item.
             *
             * @param item the item to store in this node
             */
            protected Node(E item) { super(item); }
        }

        /**
         * A formal immutable implementation of a sub-bucket view.
         */
        protected class SubBucket extends AbstractLinkSubSequence {

            private static final long serialVersionUID = -7000073677354528483L;

            private transient final int bucketSize; private transient final Node<E> bucketTail;

            protected SubBucket(int size, int fromIndex, int toIndex) {
                super(size, fromIndex, toIndex); this.bucketSize = Bucket.this.size; this.bucketTail = Bucket.this.tail; }

            protected SubBucket(int bucketSize, Node<E> bucketTail, SubBucket parent, int fromIndex, int toIndex) {
                super(parent, fromIndex, toIndex); this.bucketSize = bucketSize; this.bucketTail = bucketTail; }

            @Override public int getCount(Object o) {
                checkMod(); return super.getCount(o); }
            @Override public Iterator<E> getAll(Object o) {
                checkMod(); return super.getAll(o); }
            @Override public <R extends E> R get(int index) {
                checkMod(); return super.get(index); }
            @Override public int size() {
                checkMod(); return super.size(); }
            @Override public Iterator<E> iterator() {
                checkMod(); return iterator(0, previous(0)); }
            @Override public SequenceIterator<E> iterator(int index) {
                checkMod(); return iterator(positionIndex(index, size), previous(index)); }
            @Override public Spliterator<E> spliterator() {
                checkMod(); return super.spliterator(); }
            @Override public Sequence<E> sub(int fromIndex, int toIndex) {
                checkMod(); return new SubBucket(bucketSize, bucketTail, this, fromIndex, toIndex); }

            protected SequenceIterator<E> iterator(int index, Node<E> previous) {
                return new TailedSequenceIterator<E>() {

                    final TailedSequenceIterator<E> iterator = Bucket.super.iterator(previous, SubBucket.this, index, null);

                    @Override public boolean hasPrevious() { return iterator.hasPrevious(); }
                    @Override public E previous() { checkMod(); return iterator.previous(); }
                    @Override public int previousIndex() { checkMod(); return iterator.previousIndex(); }
                    @Override public boolean hasNext() { return iterator.hasNext(); }
                    @Override public E next() { checkMod(); return iterator.next(); }
                    @Override public int nextIndex() { checkMod(); return iterator.nextIndex(); }
                    @Override public void remove() { checkMod(); iterator.remove(); }
                    @Override public void forEachRemaining(Consumer<? super E> action) {
                        checkMod(); iterator.forEachRemaining(action.andThen(unused -> checkMod())); }
                    @Override public void forEachPreceding(Consumer<? super E> action) {
                        checkMod(); iterator.forEachPreceding(action.andThen(unused -> checkMod())); }
                };
            }

            void checkMod() {
                if (bucketSize != Bucket.this.size || bucketTail != Bucket.this.tail)
                    throw new ConcurrentModificationException();
            }
        }
    }
}
