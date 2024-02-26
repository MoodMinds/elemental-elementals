package org.moodminds.elemental;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;

import static java.lang.Math.max;
import static org.moodminds.sneaky.Cast.cast;
import static org.moodminds.sneaky.Sneak.sneak;

/**
 * A {@link HashMap}-powered implementation of the {@link Container} interface.
 *
 * @param <E> the element type
 */
public class HashContainer<E> extends AbstractMultiContainer<E,
            HashContainer.Node<E>, HashContainer.Bucket<E>, Map<E, Object>>
        implements RandomMatch {

    private static final long serialVersionUID = 4140307042993429240L;

    /**
     * Container size holder field.
     */
    private transient int size;

    /**
     * Construct the object with the given elements array.
     *
     * @param elements the given elements array
     */
    @SafeVarargs
    public HashContainer(E... elements) {
        this(new HashMap<>(capacity(elements.length)), Stream.of(elements));
    }

    /**
     * Construct the object with the given elements {@link Stream}.
     *
     * @param elements the given elements {@link Stream}
     */
    public HashContainer(Stream<? extends E> elements) {
        this(new HashMap<>(), elements);
    }

    /**
     * Construct the object with the given elements {@link Container}.
     *
     * @param elements the given elements {@link Container}
     */
    public HashContainer(Container<? extends E> elements) {
        this(new HashMap<>(capacity(elements.size())), elements.stream());
    }

    /**
     * Construct the object with the given elements {@link java.util.Collection}.
     *
     * @param elements the given elements {@link java.util.Collection}
     */
    public HashContainer(java.util.Collection<? extends E> elements) {
        this(new HashMap<>(capacity(elements.size())), elements.stream());
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
     * Construct the object with the given {@link HashMap}, elements {@link Stream} and size.
     *
     * @param map the given  {@link HashMap}
     * @param elements the given elements {@link Stream}
     */
    protected HashContainer(HashMap<E, Object> map, Stream<? extends E> elements) {
        super(map); populate(elements.sequential()::forEach);
    }

    @Override public int size() {
        return size; }

    @Override protected boolean isBucket(Object value) {
        return value instanceof Bucket; }
    @Override protected Optional<Integer> getSize() {
        return Optional.of(size); }
    @Override protected boolean isMulti() {
        return size > map.size(); }

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
        populate(consumer -> {
            try {
                while (this.size < size)
                    consumer.accept(cast(input.readObject()));
            } catch (Exception e) { sneak(e); }
        });
    }

    private void populate(Producer<? extends E> elements) {
        Map<Bucket<E>, Node<E>> tails = new HashMap<>(); elements.provide(element -> {
            Object value = map.get(element);
            if (value == null && (element != null || !map.containsKey(null))) {
                map.put(element, element);
            } else {
                Bucket<E> bucket; Node<E> tail, next = new Node<>(element);
                if (isBucket(value)) {
                    tail = (bucket = asBucket(value)).size == 2 ? bucket.head.next : tails.get(bucket);
                    tails.put(bucket, next);
                } else {
                    bucket = new Bucket<>(tail = new Node<>(cast(value)));
                    map.put(cast(value), bucket);
                } tail.next = next; bucket.size++;
            } size++;
        });
    }


    /**
     * Implementation of the {@link AbstractMultiContainer.Node}.
     *
     * @param <E> the type of elements
     */
    protected static class Node<E> extends AbstractMultiContainer.Node<E, Node<E>> {

        private static final long serialVersionUID = -2250150721955068129L;

        protected Node(E item) {
            super(item); }
    }

    /**
     * Implementation of the {@link AbstractMultiContainer.Bucket}.
     *
     * @param <E> the type of elements
     */
    protected static class Bucket<E> extends AbstractMultiContainer.Bucket<E, Node<E>, Bucket<E>> {

        private static final long serialVersionUID = -1581372653963471796L;

        protected Bucket(Node<E> head) {
            super(head); }
    }

    /**
     * Calculate the initial {@link HashMap} capacity basing on the given size.
     *
     * @param size the given size
     * @return the initial {@link HashMap} capacity basing on the given size
     */
    private static int capacity(int size) {
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
