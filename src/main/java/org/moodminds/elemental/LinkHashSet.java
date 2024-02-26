package org.moodminds.elemental;

import java.io.ObjectInputStream;
import java.util.LinkedHashMap;
import java.util.stream.Stream;

import static java.util.stream.Stream.empty;
import static org.moodminds.sneaky.Cast.cast;

/**
 * A {@link LinkedHashMap}-powered implementation
 * of the {@link Set} interface, preserving the order of elements
 * as defined during construction.
 * <p>
 * The main reason to prefer this class over wrapping {@link WrapSet}
 * is the efficient implementation of the {@link #getAll(Object)} method.
 * @param <E> the element type
 */
public class LinkHashSet<E> extends HashSet<E> {

    private static final long serialVersionUID = 1936492031602351171L;

    /**
     * Construct the object with the given elements vararg.
     *
     * @param elements the given elements vararg
     */
    @SafeVarargs
    public LinkHashSet(E... elements) {
        this(new LinkedHashMap<>(capacity(elements.length)), Stream.of(elements));
    }

    /**
     * Construct the object with the given elements {@link Stream}.
     *
     * @param elements the given elements {@link Stream}
     */
    public LinkHashSet(Stream<? extends E> elements) {
        this(new LinkedHashMap<>(), elements);
    }

    /**
     * Construct the object with the given elements {@link Container}.
     *
     * @param elements the given elements {@link Container}
     */
    public LinkHashSet(Container<? extends E> elements) {
        this(new LinkedHashMap<>(capacity(elements.size())), elements.stream());
    }

    /**
     * Construct the object with the given elements {@link java.util.Collection}.
     *
     * @param elements the given elements {@link java.util.Collection}
     */
    public LinkHashSet(java.util.Collection<? extends E> elements) {
        this(new LinkedHashMap<>(capacity(elements.size())), elements.stream());
    }

    /**
     * Construct the object with the given elements {@link Collection}.
     *
     * @param elements the given elements {@link Collection}
     */
    public LinkHashSet(Collection<? extends E> elements) {
        this((java.util.Collection<? extends E>) elements);
    }

    /**
     * Construct the object with the specified initial capacity and the default load factor (0.75).
     *
     * @param initialCapacity the initial capacity of the hash table
     * @throws IllegalArgumentException if the initial capacity is negative
     */
    public LinkHashSet(int initialCapacity) {
        this(new LinkedHashMap<>(initialCapacity), empty());
    }

    /**
     * Construct the object with the specified initial capacity and load factor.
     *
     * @param initialCapacity the initial capacity of the hash map
     * @param loadFactor the load factor of the hash map
     * @throws IllegalArgumentException if the initial capacity is negative, or if the load factor is not positive
     */
    public LinkHashSet(int initialCapacity, float loadFactor) {
        this(new LinkedHashMap<>(initialCapacity, loadFactor), empty());
    }

    /**
     * Construct the object with the given {@link LinkedHashMap} and elements {@link Stream}.
     *
     * @param map the given  {@link LinkedHashMap}
     * @param elements the given elements {@link Stream}
     */
    protected LinkHashSet(LinkedHashMap<E, Object> map, Stream<? extends E> elements) {
        super(map, elements);
    }

    @Override
    protected void deserialize(ObjectInputStream input) throws Exception {
        int size = input.readInt();
        map = new LinkedHashMap<>(capacity(size));
        for (int i = 0; i < size; i++)
            add(cast(input.readObject()));
    }


    /**
     * Return a {@link LinkHashSet} of the given values.
     *
     * @param elements the given values
     * @param <E> the element type
     * @return a {@link LinkHashSet} of the given values
     */
    @SafeVarargs
    public static <E> LinkHashSet<E> set(E... elements) {
        return new LinkHashSet<>(elements);
    }
}
