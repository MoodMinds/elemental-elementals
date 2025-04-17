package org.moodminds.elemental;

import org.moodminds.elemental.AbstractSortedContainer.Bucket;
import org.moodminds.elemental.LinkSequence.Node;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Comparator;
import java.util.IdentityHashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Optional;
import java.util.SortedMap;
import java.util.Spliterator;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Consumer;

import static java.util.Optional.ofNullable;
import static org.moodminds.elemental.Producer.producer;
import static org.moodminds.sneaky.Cast.cast;

/**
 * A template implementation of the {@link SortedContainer} interface,
 * which allows duplicates and is powered by an internal {@link SortedMap}.
 *
 * @param <E> the element type
 * @param <M> the type of the internal {@link SortedMap}
 */
public abstract class AbstractSortedContainer<E, M extends SortedMap<E, Object>>
        extends AbstractHeapContainer<E, Bucket<E>, M> implements SortedContainer<E> {

    private static final long serialVersionUID = 6521271551072774284L;

    /**
     * Container size holder field.
     */
    protected Integer size;

    /**
     * Construct the object with the given {@link M map}.
     *
     * @param map the given {@link M map}
     */
    protected AbstractSortedContainer(M map) {
        super(map);
    }

    /**
     * Construct the object with the given {@link M map} and size.
     *
     * @param map the given {@link M map}
     * @param size the given size
     */
    protected AbstractSortedContainer(M map, Integer size) {
        super(map); this.size = size;
    }

    /**
     * Construct the object with the given {@link M map} and sequential single-threaded {@link Producer} of elements.
     *
     * @param map the given {@link M map}
     * @param elements the given sequential single-threaded {@link Producer} of elements
     */
    protected AbstractSortedContainer(M map, Producer<? extends E> elements) {
        this(map, elements, new Bucketization<E>() {

            final Map<Bucket<E>, Node<E>> tails = new IdentityHashMap<>();

            @Override public void accept(Bucket<E> bucket, E element) {
                tails.compute(bucket, (unused, tail) -> bucket.put(tail, element)); }
            @Override public Bucket<E> apply(E first, E second) {
                return new Object() { final Bucket<E> bucket; Node<E> tail;
                    { bucket = new Bucket<>(next -> tail = next, first, second); tails.put(bucket, tail); }
                }.bucket; }
        });
    }

    /**
     * Construct the object with the given {@link M} and sequential single-threaded {@link Producer} of elements
     * and using a single stateful {@code bucketing} strategy for both bucket construction and accumulation.
     *
     * @param map the backing {@link M} used for storing single elements and buckets
     * @param elements the given sequential single-threaded {@link Producer} of elements
     * @param bucketization a combined {@link Bucketization} strategy for bucket handling
     */
    private AbstractSortedContainer(M map, Producer<? extends E> elements, Bucketization<E> bucketization) {
        super(map, elements, bucketization, bucketization);
    }

    @Override public int size() {
        return getSize().orElseGet(() -> {
            int count = 0; for (Object value : map.values())
                count = count + tryBucket(value, Container::size, unused -> 1);
            return size = count;
        }); }

    @Override public Comparator<? super E> comparator() {
        return map.comparator(); }

    @Override public Iterator<E> firstAll() {
        return contains() ? getAll(map.firstKey()) : iterator(null, false); }
    @Override public Iterator<E> lastAll() {
        return contains() ? getAll(map.lastKey()) : iterator(null, false); }

    @Override public SortedContainer<E> sub(E fromElement, E toElement) {
        return new SortedSubContainer<>(this, map.subMap(fromElement, toElement)); }
    @Override public SortedContainer<E> head(E toElement) {
        return new SortedSubContainer<>(this, map.headMap(toElement)); }
    @Override public SortedContainer<E> tail(E fromElement) {
        return new SortedSubContainer<>(this, map.tailMap(fromElement)); }

    @Override protected void init(Producer<? extends E> elements, BiConsumer<Bucket<E>, E> bucketAccumulation,
                                  BiFunction<E, E, Bucket<E>> bucketConstruction) {
        size = 0; super.init(elements, bucketAccumulation, bucketConstruction); }

    @Override protected Optional<Integer> getSize() {
        return ofNullable(size); }

    @Override protected void count(int number) {
        size = size + number; }

    @Override protected boolean isBucket(Object value) {
        return value instanceof Bucket; }

    /**
     * {@inheritDoc}
     *
     * @param entriesSpliterator {@inheritDoc}
     * @param useSourceSize {@inheritDoc}
     * @param isDistinct {@inheritDoc}
     * @param knownSize {@inheritDoc}
     * @return {@inheritDoc}
     */
    @Override protected Spliterator<E> containerSpliterator(Spliterator<Map.Entry<E, Object>> entriesSpliterator, boolean useSourceSize, boolean isDistinct, Integer knownSize) {
        return super.containerSpliterator(entriesSpliterator, false, isDistinct, knownSize); }

    @Override protected void serialize(ObjectOutputStream output) throws Exception {
        output.writeObject(map); }
    @Override protected void deserialize(ObjectInputStream input) throws Exception {
        map = cast(input.readObject()); }

    /**
     * Represents a bucket in this container.
     *
     * @param <E> the type of elements
     */
    protected static class Bucket<E> extends LinkSequence<E> {

        private static final long serialVersionUID = -8267799111732935085L;

        protected Bucket(Consumer<Node<E>> tails, E first, E second) {
            super(tails, producer(first, second)); }
    }

    /**
     * A composite utility that handles both bucket accumulation and bucket construction logic.
     *
     * @param <E> the type of elements stored in the bucket
     */
    protected interface Bucketization<E> extends BiConsumer<Bucket<E>, E>, BiFunction<E, E, Bucket<E>> {}


    /**
     * Sub-container extension of the {@link AbstractSortedContainer}.
     *
     * @param <P> the type of the parent collection
     * @param <E> the element type
     * @param <M> the type of the internal {@link SortedMap}
     */
    protected static class SortedSubContainer<P extends AbstractSortedContainer<E, ? extends M>, E, M extends SortedMap<E, Object>>
            extends AbstractSortedContainer<E, M> {

        private static final long serialVersionUID = -5646956340075624242L;

        protected final P parent;

        protected SortedSubContainer(P parent, M map) { super(map); this.parent = parent; }

        @Override protected Optional<Boolean> isDistinct() {
            return parent.isDistinct(); }
    }
}
