package org.moodminds.elemental;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

import static java.lang.Math.max;
import static org.moodminds.sneaky.Cast.cast;

/**
 * A {@link HashMap}-powered implementation of the {@link Container} interface.
 *
 * @param <E> the element type
 */
public class HashContainer<E> extends AbstractMultiContainer<E,
            HashContainer.Node<E>, HashContainer.Bucket<E>, Map<E, Object>>
        implements RandomMatch {

    private static final long serialVersionUID = 6768864566324116783L;

    /**
     * Construct the object with the given elements array.
     *
     * @param elements the given elements array
     */
    @SafeVarargs
    public HashContainer(E... elements) {
        this(new HashMap<>(max((int) (elements.length/.75f) + 1, 16)), Stream.of(elements), elements.length);
    }

    /**
     * Construct the object with the given elements {@link Stream}.
     *
     * @param elements the given elements {@link Stream}
     */
    public HashContainer(Stream<? extends E> elements) {
        this(new HashMap<>(), elements, null);
    }

    /**
     * Construct the object with the given elements {@link Container}.
     *
     * @param elements the given elements {@link Container}
     */
    public HashContainer(Container<? extends E> elements) {
        this(new HashMap<>(max((int) (elements.size()/.75f) + 1, 16)), elements.stream(), elements.size());
    }

    /**
     * Construct the object with the given elements {@link java.util.Collection}.
     *
     * @param elements the given elements {@link java.util.Collection}
     */
    public HashContainer(java.util.Collection<? extends E> elements) {
        this(new HashMap<>(max((int) (elements.size()/.75f) + 1, 16)), elements.stream(), elements.size());
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
     * @param size the given size
     */
    protected HashContainer(HashMap<E, Object> map, Stream<? extends E> elements, Integer size) {
        super(map, size); elements.forEach(this::put);
    }

    @Override protected boolean isBucket(Object value) {
        return value instanceof Bucket; }
    @Override protected Bucket<E> asBucket(Object value) {
        return cast(value); }
    @Override protected Bucket<E> newBucket() {
        return new Bucket<>(); }


    /**
     * Implementation of the {@link AbstractMultiContainer.Bucket}.
     *
     * @param <E> the type of elements
     */
    protected static class Bucket<E> extends AbstractMultiContainer.Bucket<E, Node<E>, Bucket<E>> {

        private static final long serialVersionUID = 7431766758521162279L;

        @Override
        protected Node<E> link(Node<E> prev, E item) {
            return new Node<>(prev, item);
        }
    }

    /**
     * Implementation of the {@link AbstractMultiContainer.Node}.
     *
     * @param <E> the type of elements
     */
    protected static class Node<E> extends AbstractMultiContainer.Node<E, Node<E>> {

        protected Node(Node<E> prev, E item) {
            super(prev, item);
        }
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
