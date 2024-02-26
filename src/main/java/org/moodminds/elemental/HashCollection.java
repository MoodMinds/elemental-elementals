package org.moodminds.elemental;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;

import static java.lang.Math.max;
import static java.util.stream.Stream.empty;
import static org.moodminds.sneaky.Cast.cast;

/**
 * A {@link HashMap}-powered implementation of the {@link Collection} interface.
 *
 * @param <E> the element type
 */
public class HashCollection<E> extends AbstractMultiCollection<E,
            HashCollection.Node<E>, HashCollection.Bucket<E>, Map<E, Object>>
        implements RandomMatch {

    private static final long serialVersionUID = -8125380692227516710L;

    /**
     * Container size holder field.
     */
    private transient int size;

    /**
     * Modification count holder field.
     */
    private transient int modCount;

    /**
     * Construct the object with the given elements vararg.
     *
     * @param elements the given elements vararg
     */
    @SafeVarargs
    public HashCollection(E... elements) {
        this(new HashMap<>(capacity(elements.length)), Stream.of(elements));
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
        this(new HashMap<>(capacity(elements.size())), elements.stream());
    }

    /**
     * Construct the object with the given elements {@link java.util.Collection}.
     *
     * @param elements the given elements {@link java.util.Collection}
     */
    public HashCollection(java.util.Collection<? extends E> elements) {
        this(new HashMap<>(capacity(elements.size())), elements.stream());
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
        this(new HashMap<>(initialCapacity), empty());
    }

    /**
     * Construct the object with the specified initial capacity and load factor.
     *
     * @param initialCapacity the initial capacity of the hash map
     * @param loadFactor the load factor of the hash map
     * @throws IllegalArgumentException if the initial capacity is negative, or if the load factor is not positive
     */
    public HashCollection(int initialCapacity, float loadFactor) {
        this(new HashMap<>(initialCapacity, loadFactor), empty());
    }

    /**
     * Construct the object with the given {@link HashMap} and elements {@link Stream}.
     *
     * @param map the given  {@link HashMap}
     * @param elements the given elements {@link Stream}
     */
    protected HashCollection(HashMap<E, Object> map, Stream<? extends E> elements) {
        super(map); elements.sequential().forEach(this::put);
    }

    @Override public int size() {
        return size; }

    @Override protected boolean isMulti() {
        return size > map.size(); }
    @Override protected Optional<Integer> getSize() {
        return Optional.of(size); }

    @Override protected void put(E e) {
        super.put(e); size++; }

    @Override protected boolean isBucket(Object value) {
        return value instanceof Bucket; }
    @Override protected Node<E> node(E item) {
        return new Node<>(item); }
    @Override protected Bucket<E> bucket(Node<E> node) {
        return new Bucket<>(node); }

    @Override protected int totalMod() {
        return modCount; }
    @Override protected void countMod() {
        modCount++; }

    @Override protected Iterator<E> iterator(Bucket<E> bucket) {
        return new CollectionBucketIterator(bucket.iterator()) {
            @Override public void remove() {
                super.remove(); size--; }
        }; }
    @Override protected Iterator<E> iterator(Object value, boolean hasNext) {
        return hasNext ? new CollectionSingleIterator(cast(value)) {
            @Override protected void removeElement() {
                super.removeElement(); size--; }
        } : new CollectionEmptyIterator(); }

    @Override protected Iterator<E> iterator(Iterator<Map.Entry<E, Object>> iterator) {
        return new CollectionIterator(iterator) {
            @Override protected void removeElement() {
                super.removeElement(); size--; }
        }; }

    @Override
    protected void serialiaze(ObjectOutputStream output) throws Exception {
        output.writeInt(size);
        for (E e : this)
            output.writeObject(e);
    }

    @Override
    protected void deserialize(ObjectInputStream input) throws Exception {
        int size = input.readInt();
        map = new HashMap<>(capacity(size));
        for (int i = 0; i < size; i++)
            put(cast(input.readObject()));
    }

    /**
     * Implementation of the {@link AbstractMultiCollection.Node}.
     *
     * @param <E> the type of elements
     */
    protected static class Node<E> extends AbstractMultiCollection.Node<E, Node<E>> {

        private static final long serialVersionUID = -6610880557592754708L;

        protected Node(E item) {
            super(item); }
    }

    /**
     * Implementation of the {@link AbstractMultiCollection.Bucket}.
     *
     * @param <E> the type of elements
     */
    protected static class Bucket<E> extends AbstractMultiCollection.Bucket<E, Node<E>, Bucket<E>> {

        private static final long serialVersionUID = -3777077832667120648L;

        protected Bucket(Node<E> head) {
            super(head); }

        @Override public BucketIterator iterator() {
            return new TailIterator(); }
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
