package org.moodminds.elemental;

import org.moodminds.sneaky.Cast;

import java.util.Comparator;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.Spliterator;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;

import static java.lang.Boolean.FALSE;
import static java.util.Optional.ofNullable;
import static org.moodminds.sneaky.Cast.cast;

/**
 * An abstract base for {@link Container}s that do not preserve insertion order.
 * <p>
 * This Container arranges elements internally using bucket-based structure,
 * meaning the iteration order is not defined and may differ from insertion order.
 * The primary purpose is to provide efficient grouping and accumulation mechanisms
 * without maintaining sequential order.
 *
 * @param <E> the element type
 * @param <B> the type of the internal {@link Container} bucket that holds duplicates
 * @param <M> the type of the internal {@link Map}
 */
public abstract class AbstractHeapContainer<E, B extends Container<E>, M extends Map<E, Object>>
        extends AbstractMapContainer<E, B, M> {

    private static final long serialVersionUID = -3405668063091255899L;

    /**
     * Construct the object with the given {@link M map}.
     *
     * @param map the given {@link M map}
     */
    protected AbstractHeapContainer(M map) {
        super(map);
    }

    /**
     * Construct the object with the given {@link M map}, sequential single-threaded {@link Producer} of elements,
     * {@link BiConsumer} bucket accumulation and {@link BiFunction} bucket construction functions.
     *
     * @param map the given {@link M map}
     * @param elements the given sequential single-threaded {@link Producer} of elements
     * @param bucketAccumulation a {@link BiConsumer} that adds the element to an existing bucket
     * @param bucketConstruction a {@link BiFunction} that creates a new bucket from an existing single value and the new element
     */
    protected AbstractHeapContainer(M map, Producer<? extends E> elements, BiConsumer<B, E> bucketAccumulation, BiFunction<E, E, B> bucketConstruction) {
        super(map); init(elements, bucketAccumulation, bucketConstruction);
    }

    /**
     * Initialize the container from the specified sequential single-threaded {@link Producer} of elements
     * and inserting them using the provided bucket handling strategies.
     *
     * @param elements the given sequential single-threaded {@link Producer} of elements
     * @param bucketAccumulation a {@link BiConsumer} that adds the element to an existing bucket
     * @param bucketConstruction a {@link BiFunction} that creates a new bucket from an existing single value and the new element
     */
    protected void init(Producer<? extends E> elements, BiConsumer<B, E> bucketAccumulation, BiFunction<E, E, B> bucketConstruction) {
        elements.provide(element -> put(bucketAccumulation, bucketConstruction, element));
    }

    /**
     * Insert the specified {@code element} into the internal map,
     * either by adding it to an existing bucket or by creating a new one.
     * <p>
     * The insertion logic is as follows:
     * <ul>
     *   <li>If a bucket associated with the {@code element} is found,
     *   the {@code bucketAccumulation} is used to add the element into that bucket.</li>
     *   <li>Otherwise, if the map contains a direct mapping for {@code element},
     *   a new bucket is created using the {@code bucketConstruction} function, combining the existing value and the new element.</li>
     *   <li>If no mapping is present, the element is stored as a direct value.</li>
     * </ul>
     *
     * @param bucketAccumulation a {@link BiConsumer} that adds the {@code element} to an existing bucket
     * @param bucketConstruction a {@link BiFunction} that creates a new bucket from an existing single value and the new {@code element}
     * @param element the element to insert; must not be {@code null}
     * @throws NullPointerException if {@code element} is {@code null} and the {@link M} does not accept null values
     */
    protected void put(BiConsumer<B, E> bucketAccumulation, BiFunction<E, E, B> bucketConstruction, E element) {
        Object value = map.get(element);
        if (!tryBucket(value, bucket -> bucketAccumulation.accept(bucket, element)))
            if (isMapped(element, value))
                map.put(cast(value), bucketConstruction.apply(cast(value), element));
            else map.put(element, element);
        count(1);
    }

    @Override public Iterator<E> iterator() {
        return containerIterator(map.entrySet().iterator(), Container::iterator, null); }
    @Override public Spliterator<E> spliterator() {
        return containerSpliterator(map.entrySet().spliterator(), true, isDistinct().orElse(FALSE), getSize().orElse(null)); }

    /**
     * {@inheritDoc}
     *
     * @param value {@inheritDoc}
     * @param present {@inheritDoc}
     * @return {@inheritDoc}
     */
    @Override
    protected final Iterator<E> iterator(Object value, boolean present) {
        return iterator(value, present, null);
    }

    /**
     * Return an iterator for the specified value, considering its presence and {@link Runnable} removal action.
     *
     * @param value the value to iterate over
     * @param present {@code true} if the value is present, {@code false} otherwise
     * @param removal a {@link Runnable} to execute when the value is removed during iteration
     * @return an iterator for the specified value
     */
    protected Iterator<E> iterator(Object value, boolean present, Runnable removal) {
        return OptionalIterator.iterator(Cast.<E>cast(value), present, removal);
    }

    /**
     * Return an iterator that traverses the elements of the container, iterating over both valued and bucketed elements.
     * Delegate bucket iteration to the provided {@code bucketIteration} function and handle removal actions
     * through the {@code removal} {@link Runnable}.
     *
     * @param entriesIterator the iterator for the container's entries
     * @param bucketIteration a function that provides an iterator for each bucket
     * @param removal a {@link Runnable} to execute when an element is removed during iteration
     * @return an iterator that traverses elements from both buckets and entries
     */
    protected Iterator<E> containerIterator(Iterator<Entry<E, Object>> entriesIterator, Function<B, Iterator<E>> bucketIteration, Runnable removal) {
        return new AbstractIterator<E>(removal) {

            Iterator<E> bucketIterator;

            @Override protected boolean hasNextElement() {
                return bucketIterator != null && bucketIterator.hasNext() || entriesIterator.hasNext(); }

            @Override protected E nextElement() {
                while (true) {
                    if (bucketIterator != null && bucketIterator.hasNext())
                        return bucketIterator.next();
                    bucketIterator = null; Object value = entriesIterator.next().getValue();
                    if (tryBucket(value, bucket -> bucketIterator = bucketIteration.apply(bucket)))
                        continue;
                    return cast(value); } }
        };
    }

    /**
     * Return a {@link Spliterator} for the container that traverses both valued and bucketed elements.
     *
     * @param entriesSpliterator the spliterator for iterating over the container's entries
     * @param useSourceSize a flag indicating whether to use the source size for estimating the spliterator's size
     * @param isDistinct a flag indicating whether the elements in the container are distinct
     * @param knownSize a known size for the container, or {@code null} if the size is not known
     * @return a {@link Spliterator} that can iterate over the elements of the container, handling bucket and entry-based elements
     */
    protected Spliterator<E> containerSpliterator(Spliterator<Entry<E, Object>> entriesSpliterator, boolean useSourceSize, boolean isDistinct, Integer knownSize) {
        return new Spliterator<E>() {

            Spliterator<E> bucketSpliterator; Integer size = size(entriesSpliterator, knownSize);

            @Override public boolean tryAdvance(Consumer<? super E> action) {
                while (true) {
                    if (bucketSpliterator != null && bucketSpliterator.tryAdvance(action))
                        return true;
                    bucketSpliterator = null; Object[] value = new Object[1];
                    if (entriesSpliterator.tryAdvance(entry -> value[0] = entry.getValue())) {
                        if (tryBucket(value[0], bucket -> bucketSpliterator = bucket.spliterator()))
                            continue;
                        action.accept(cast(value[0])); return true;
                    } else return false; } }

            @Override public Spliterator<E> trySplit() {
                Integer size = this.size; return ofNullable(entriesSpliterator.trySplit())
                        .map(split -> containerSpliterator(split, useSourceSize, isDistinct,
                                size != null & (this.size = size(entriesSpliterator, null)) != null ? size - this.size : null))
                        .orElse(null); }

            @Override public long estimateSize() {
                return size != null ? size : Long.MAX_VALUE; }

            @Override public int characteristics() {
                int characteristics = entriesSpliterator.characteristics();
                if (!isDistinct)
                    characteristics &= ~DISTINCT;
                if (size == null && !(isDistinct && useSourceSize))
                    characteristics &= ~(SIZED | SUBSIZED);
                return characteristics | IMMUTABLE; }

            @Override public Comparator<? super E> getComparator() {
                return ofNullable(entriesSpliterator.getComparator()).map(comp -> new Comparator<E>() {

                    E key1; final Entry<E, Object> entry1 = new AbstractKeyValue<E, Object>() {
                        @Override public E getKey() { return key1; }
                        @Override public Object getValue() { return null; } };
                    E key2; final Entry<E, Object> entry2 = new AbstractKeyValue<E, Object>() {
                        @Override public E getKey() { return key2; }
                        @Override public Object getValue() { return null; } };

                    @Override public int compare(E e1, E e2) { key1 = e1; key2 = e2;
                        int result = comp.compare(entry1, entry2); key1 = null; key2 = null; return result; }

                }).orElse(null); }

            private Integer size(Spliterator<Entry<E, Object>> spliterator, Integer knownSize) {
                return isDistinct && useSourceSize && spliterator.hasCharacteristics(SIZED)
                        ? Integer.valueOf((int) spliterator.estimateSize()) : knownSize;
            }
        };
    }

    /**
     * Adjust the size of this container by the specified amount.
     *
     * @param number the amount by which to adjust the size
     */
    protected abstract void count(int number);

    /**
     * Return the size of the container, if available, wrapped in an {@link Optional}.
     * If the size is not available or cannot be determined, return an empty {@link Optional}.
     *
     * @return an {@link Optional} containing the size of the container, or an empty {@link Optional} if the size is unknown
     */
    protected abstract Optional<Integer> getSize();

    /**
     * Returns the property indicating whether the container contains distinct elements,
     * or {@link Optional#empty()} if the distinctness cannot be determined.
     *
     * @return an {@link Optional} containing {@code true} if the container has distinct elements,
     *         {@code false} if it does not, or an empty {@link Optional} if the uniqueness is unknown
     */
    protected abstract Optional<Boolean> isDistinct();
}
