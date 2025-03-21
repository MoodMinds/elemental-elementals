package org.moodminds.elemental;

import org.moodminds.elemental.HashContainer.Bucket;
import org.moodminds.elemental.LinkSequence.Link;

import java.io.InvalidObjectException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.stream.Stream;

import static java.lang.Math.max;
import static java.util.Optional.of;
import static org.moodminds.elemental.Producer.producer;
import static org.moodminds.sneaky.Cast.cast;
import static org.moodminds.sneaky.Sneak.sneak;

/**
 * A {@link HashMap}-powered implementation of the {@link Container} interface.
 *
 * @param <E> the element type
 */
public class HashContainer<E> extends AbstractHeapContainer<E, Bucket<E>, Map<E, Object>> {

    private static final long serialVersionUID = 4140307042993429240L;

    /**
     * Container size holder field.
     */
    protected transient int size;

    /**
     * Construct the object with the given elements array.
     *
     * @param elements the given elements array
     */
    @SafeVarargs
    public HashContainer(E... elements) {
        this(new HashMap<>(capacity(elements.length)), producer(elements));
    }

    /**
     * Construct the object with the given elements {@link Stream}.
     *
     * @param elements the given elements {@link Stream}
     */
    public HashContainer(Stream<? extends E> elements) {
        this(new HashMap<>(), elements.sequential()::forEach);
    }

    /**
     * Construct the object with the given elements {@link Container}.
     *
     * @param elements the given elements {@link Container}
     */
    public HashContainer(Container<? extends E> elements) {
        this(new HashMap<>(capacity(elements.size())), elements::forEach);
    }

    /**
     * Construct the object with the given elements {@link java.util.Collection}.
     *
     * @param elements the given elements {@link java.util.Collection}
     */
    public HashContainer(java.util.Collection<? extends E> elements) {
        this(new HashMap<>(capacity(elements.size())), elements::forEach);
    }

    /**
     * Construct the object with the given elements {@link Collection}.
     *
     * @param elements the given elements {@link Collection}
     */
    public HashContainer(Collection<? extends E> elements) {
        this((java.util.Collection<? extends E>) elements);
    }

    /**
     * Construct the object with the given {@link HashMap}
     * and sequential single-threaded {@link Producer} of elements.
     *
     * @param map the given {@link HashMap}
     * @param elements the given sequential single-threaded {@link Producer} of elements
     */
    protected HashContainer(HashMap<E, Object> map, Producer<? extends E> elements) {
        this(map, elements, new Bucketing<>());
    }

    /**
     * Construct the object with the given {@link HashMap} and sequential single-threaded {@link Producer} of elements
     * and using a single stateful {@code bucketing} strategy for both bucket creation and accumulation.
     *
     * @param map the backing {@link HashMap} used for storing single elements and buckets
     * @param elements the given sequential single-threaded {@link Producer} of elements
     * @param bucketing a combined {@link BiConsumer} and {@link BiFunction} strategy for bucket handling
     * @param <S> a type parameter constrained to both {@link BiConsumer} and {@link BiFunction} interfaces
     */
    private <S extends BiConsumer<Bucket<E>, E> & BiFunction<E, E, Bucket<E>>> HashContainer(HashMap<E, Object> map, Producer<? extends E> elements, S bucketing) {
        super(map, elements, bucketing, bucketing);
    }

    /**
     * Initialize the container from the specified sequential single-threaded {@link Producer} of elements
     * and using a single stateful {@code bucketing} strategy for both bucket creation and accumulation.
     *
     * @param elements the given sequential single-threaded {@link Producer} of elements
     * @param bucketing a combined {@link BiConsumer} and {@link BiFunction} strategy for bucket handling
     * @param <S> a type parameter constrained to both {@link BiConsumer} and {@link BiFunction} interfaces
     */
    protected <S extends BiConsumer<Bucket<E>, E> & BiFunction<E, E, Bucket<E>>> void init(Producer<? extends E> elements, S bucketing) {
        init(elements, bucketing, bucketing);
    }

    @Override public int size() {
        return size; }

    @Override protected Optional<Boolean> isDistinct() {
        return of(size == map.size()); }
    @Override protected Optional<Integer> getSize() {
        return of(size); }

    @Override protected void count(int number) { size = size + number; }

    @Override protected boolean isBucket(Object value) {
        return value instanceof Bucket; }

    @Override
    protected void serialize(ObjectOutputStream output) throws Exception {
        output.writeInt(size); for (E e : this)
            output.writeObject(e);
    }

    @Override
    protected void deserialize(ObjectInputStream input) throws Exception {
        int size; if ((size = input.readInt()) < 0)
            throw new InvalidObjectException("Negative size: " + size);
        map = new HashMap<>(capacity(size)); init(consumer -> {
            try {
                while (this.size < size)
                    consumer.accept(cast(input.readObject()));
            } catch (Exception e) { sneak(e); }
        }, new Bucketing<>());
    }

    /**
     * Represents a bucket in this container.
     *
     * @param <E> the type of elements
     */
    protected static class Bucket<E> extends LinkSequence<E> {

        private static final long serialVersionUID = 5178607093351787071L;
    }

    /**
     * A composite utility that handles both bucket accumulation and bucket creation logic.
     *
     * @param <E> the type of elements stored in the bucket
     */
    protected static class Bucketing<E> implements BiConsumer<Bucket<E>, E>, BiFunction<E, E, Bucket<E>> {

        final Map<Bucket<E>, Link<E>> tails = new IdentityHashMap<>();

        @Override public void accept(Bucket<E> bucket, E e) {
            Link<E> next; bucket.put(tails.put(bucket, next = new Link<>(e)), next); }
        @Override public Bucket<E> apply(E e1, E e2) {
            Bucket<E> bucket = new Bucket<>(); accept(bucket, e1); accept(bucket, e1); return bucket; }
    }


    /**
     * Calculate the initial {@link HashMap} capacity basing on the given size.
     *
     * @param size the given size
     * @return the initial {@link HashMap} capacity basing on the given size
     */
    protected static int capacity(int size) {
        return max((int) (size/.75f) + 1, 16);
    }

    /**
     * Return a {@link HashContainer} of the given values.
     *
     * @param elements the given values
     * @param <E> the element type
     * @return a {@link HashContainer} of the given values
     */
    @SafeVarargs
    public static <E> HashContainer<E> container(E... elements) {
        return new HashContainer<>(elements);
    }
}
