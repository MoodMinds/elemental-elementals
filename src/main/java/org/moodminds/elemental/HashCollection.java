package org.moodminds.elemental;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

import static java.lang.Math.max;
import static org.moodminds.sneaky.Cast.cast;

/**
 * A {@link HashMap}-powered implementation of the {@link Collection} interface.
 *
 * @param <E> the element type
 */
public class HashCollection<E> extends AbstractMultiCollection<E,
            HashCollection.Node<E>, HashCollection.Bucket<E>, Map<E, Object>>
        implements RandomMatch {

    private static final long serialVersionUID = -997097515485529353L;

    private transient int modCount;

    /**
     * Construct the object with the given elements vararg.
     *
     * @param elements the given elements vararg
     */
    @SafeVarargs
    public HashCollection(E... elements) {
        this(new HashMap<>(max((int) (elements.length/.75f) + 1, 16)), Stream.of(elements));
    }

    /**
     * Construct the object with the given elements {@link Stream}.
     *
     * @param elements the given elements {@link Stream}
     */
    public HashCollection(Stream<? extends E> elements) {
        this(new HashMap<>(), elements);
    }

    /**
     * Construct the object with the given elements {@link Container}.
     *
     * @param elements the given elements {@link Container}
     */
    public HashCollection(Container<? extends E> elements) {
        this(new HashMap<>(max((int) (elements.size()/.75f) + 1, 16)), elements.stream());
    }

    /**
     * Construct the object with the given elements {@link java.util.Collection}.
     *
     * @param elements the given elements {@link java.util.Collection}
     */
    public HashCollection(java.util.Collection<? extends E> elements) {
        this(new HashMap<>(max((int) (elements.size()/.75f) + 1, 16)), elements.stream());
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
        this(new HashMap<>(initialCapacity), Stream.empty());
    }

    /**
     * Construct the object with the specified initial capacity and load factor.
     *
     * @param initialCapacity the initial capacity of the hash map
     * @param loadFactor the load factor of the hash map
     * @throws IllegalArgumentException if the initial capacity is negative, or if the load factor is not positive
     */
    public HashCollection(int initialCapacity, float loadFactor) {
        this(new HashMap<>(initialCapacity, loadFactor), Stream.empty());
    }

    /**
     * Construct the object with the given {@link HashMap} and elements {@link Stream}.
     *
     * @param map the given  {@link HashMap}
     * @param elements the given elements {@link Stream}
     */
    protected HashCollection(HashMap<E, Object> map, Stream<? extends E> elements) {
        super(map, 0); elements.forEach(this::put);
    }

    @Override protected int totalMod() {
        return modCount; }
    @Override protected void countMod() {
        modCount++; }

    @Override protected boolean isBucket(Object value) {
        return value instanceof Bucket; }
    @Override protected Bucket<E> asBucket(Object value) {
        return cast(value); }
    @Override protected Bucket<E> newBucket() {
        return new Bucket<>(); }


    /**
     * Implementation of the {@link AbstractMultiCollection.Bucket}.
     *
     * @param <E> the type of elements
     */
    protected static class Bucket<E> extends AbstractMultiCollection.Bucket<E, Node<E>, Bucket<E>> {

        private static final long serialVersionUID = 6724346614999764414L;

        @Override
        protected Node<E> link(Node<E> prev, E item) {
            return new Node<>(prev, item);
        }
    }

    /**
     * Implementation of the {@link AbstractMultiCollection.Node}.
     *
     * @param <E> the type of elements
     */
    protected static class Node<E> extends AbstractMultiCollection.Node<E, Node<E>> {

        protected Node(Node<E> prev, E item) {
            super(prev, item);
        }
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
