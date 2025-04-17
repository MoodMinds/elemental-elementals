package org.moodminds.elemental;

import java.io.InvalidObjectException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;

import static java.lang.Math.max;
import static java.util.Optional.of;
import static org.moodminds.elemental.Producer.producer;
import static org.moodminds.sneaky.Cast.cast;
import static org.moodminds.sneaky.Sneak.sneak;

/**
 * A {@link HashMap}-powered implementation of the {@link Collection} interface.
 *
 * @param <E> the element type
 */
public class HashCollection<E> extends AbstractHeapCollection<E, Map<E, Object>> {

    private static final long serialVersionUID = -8125380692227516710L;

    /**
     * Collection size holder field.
     */
    protected transient int size;

    /**
     * Modification count holder field.
     */
    protected transient int modCount;

    /**
     * Construct the object with the given elements vararg.
     *
     * @param elements the given elements vararg
     */
    @SafeVarargs
    public HashCollection(E... elements) {
        this(new HashMap<>(capacity(elements.length)), producer(elements));
    }

    /**
     * Construct the object with the given elements {@link Stream}.
     *
     * @param elements the given elements {@link Stream}
     */
    public HashCollection(Stream<? extends E> elements) {
        this(new HashMap<>(), elements.sequential()::forEach);
    }

    /**
     * Construct the object with the given elements {@link Container}.
     *
     * @param elements the given elements {@link Container}
     */
    public HashCollection(Container<? extends E> elements) {
        this(new HashMap<>(capacity(elements.size())), elements::forEach);
    }

    /**
     * Construct the object with the given elements {@link java.util.Collection}.
     *
     * @param elements the given elements {@link java.util.Collection}
     */
    public HashCollection(java.util.Collection<? extends E> elements) {
        this(new HashMap<>(capacity(elements.size())), elements::forEach);
    }

    /**
     * Construct the object with the given elements {@link Collection}.
     *
     * @param elements the given elements {@link Collection}
     */
    public HashCollection(Collection<? extends E> elements) {
        this((java.util.Collection<? extends E>) elements);
    }

    /**
     * Construct the object with the specified initial capacity and the default load factor (0.75).
     *
     * @param initialCapacity the initial capacity of the hash table
     * @throws IllegalArgumentException if the initial capacity is negative
     */
    public HashCollection(int initialCapacity) {
        this(new HashMap<>(initialCapacity), producer());
    }

    /**
     * Construct the object with the specified initial capacity and load factor.
     *
     * @param initialCapacity the initial capacity of the hash map
     * @param loadFactor the load factor of the hash map
     * @throws IllegalArgumentException if the initial capacity is negative, or if the load factor is not positive
     */
    public HashCollection(int initialCapacity, float loadFactor) {
        this(new HashMap<>(initialCapacity, loadFactor), producer());
    }

    /**
     * Construct the object with the given {@link HashMap}
     * and sequential single-threaded {@link Producer} of elements.
     *
     * @param map the given  {@link HashMap}
     * @param elements the given sequential single-threaded {@link Producer} of elements
     */
    protected HashCollection(HashMap<E, Object> map, Producer<? extends E> elements) {
        super(map, elements);
    }

    @Override public int size() {
        return size; }

    @Override protected Optional<Boolean> isDistinct() {
        return of(size == map.size()); }
    @Override protected Optional<Integer> getSize() {
        return of(size); }

    @Override protected void count(int number) { size = size + number; }
    @Override protected void countClear() { size = 0; }

    @Override protected int totalMod() { return modCount; }
    @Override protected void countMod() { modCount++; }

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
        }, bucketAccumulation, bucketConstruction);
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
     * Return a {@link HashCollection} of the given values.
     *
     * @param elements the given values
     * @param <E> the element type
     * @return a {@link HashCollection} of the given values
     */
    @SafeVarargs
    public static <E> HashCollection<E> collection(E... elements) {
        return new HashCollection<>(elements);
    }
}
