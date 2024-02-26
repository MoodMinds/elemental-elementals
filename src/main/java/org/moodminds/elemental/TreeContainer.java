package org.moodminds.elemental;

import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.NavigableMap;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.stream.Stream;

import static org.moodminds.sneaky.Cast.cast;

/**
 * A {@link TreeMap}-powered implementation of the {@link NavigableContainer} interface.
 *
 * @param <E> the element type
 */
public class TreeContainer<E> extends AbstractNavigableContainer<E,
            TreeContainer.Node<E>, TreeContainer.Bucket<E>, NavigableMap<E, Object>>
        implements RandomMatch {

    private static final long serialVersionUID = 4964896021903868804L;

    /**
     * Construct the object with the given elements vararg.
     *
     * @param elements the given elements vararg
     */
    @SafeVarargs
    public TreeContainer(E... elements) {
        this(new TreeMap<>(), Stream.of(elements));
    }

    /**
     * Construct the object with the given {@link Comparator} and elements array.
     *
     * @param comparator the given elements {@link Comparator}
     * @param elements the given elements array
     */
    @SafeVarargs
    public TreeContainer(Comparator<? super E> comparator, E... elements) {
        this(new TreeMap<>(comparator), Stream.of(elements));
    }

    /**
     * Construct the object with the given elements {@link Stream}.
     *
     * @param elements the given elements {@link Stream}
     */
    public TreeContainer(Stream<? extends E> elements) {
        this(new TreeMap<>(), elements);
    }

    /**
     * Construct the object with the given {@link Comparator} and elements {@link Stream}.
     *
     * @param comparator the given elements {@link Comparator}
     * @param elements the given elements {@link Stream}
     */
    public TreeContainer(Comparator<? super E> comparator, Stream<? extends E> elements) {
        this(new TreeMap<>(comparator), elements);
    }

    /**
     * Construct the object with the given elements {@link Container}.
     *
     * @param elements the given elements {@link Container}
     */
    public TreeContainer(Container<? extends E> elements) {
        this(new TreeMap<>(), elements.stream());
    }

    /**
     * Construct the object with the given {@link Comparator} and elements {@link Container}.
     *
     * @param comparator the given elements {@link Comparator}
     * @param elements the given elements {@link Container}
     */
    public TreeContainer(Comparator<? super E> comparator, Container<? extends E> elements) {
        this(new TreeMap<>(comparator), elements.stream());
    }

    /**
     * Construct the object with the given elements {@link java.util.Collection}.
     *
     * @param elements the given elements {@link java.util.Collection}
     */
    public TreeContainer(java.util.Collection<? extends E> elements) {
        this(new TreeMap<>(), elements.stream());
    }

    /**
     * Construct the object with the given {@link Comparator} and elements {@link java.util.Collection}.
     *
     * @param comparator the given elements {@link Comparator}
     * @param elements the given elements {@link java.util.Collection}
     */
    public TreeContainer(Comparator<? super E> comparator, java.util.Collection<? extends E> elements) {
        this(new TreeMap<>(comparator), elements.stream());
    }

    /**
     * Construct the object with the given elements {@link Collection}.
     *
     * @param elements the given elements {@link Collection}
     */
    public TreeContainer(Collection<? extends E> elements) {
        this((java.util.Collection<? extends E>) elements);
    }

    /**
     * Construct the object with the given {@link Comparator} and elements {@link Collection}.
     *
     * @param comparator the given elements {@link Comparator}
     * @param elements the given elements {@link Collection}
     */
    public TreeContainer(Comparator<? super E> comparator, Collection<? extends E> elements) {
        this(comparator, (java.util.Collection<? extends E>) elements);
    }

    /**
     * Construct the object with the given {@link NavigableMap} map and parent Container.
     *
     * @param map the given {@link NavigableMap} map
     * @param parent the given parent Container
     */
    protected TreeContainer(NavigableMap<E, Object> map, AbstractNavigableContainer<E, Node<E>, Bucket<E>, ?> parent) {
        super(map, parent);
    }

    /**
     * Construct the object with the given {@link NavigableMap} map, size and parent Container if it does exist.
     *
     * @param map the given {@link NavigableMap} map
     * @param size the given size
     * @param parent the given parent Container
     */
    protected TreeContainer(NavigableMap<E, Object> map, Integer size, AbstractNavigableContainer<E, Node<E>, Bucket<E>, ?> parent) {
        super(map, size, parent);
    }

    /**
     * Construct the object with the given {@link TreeMap}, elements {@link Stream} and size.
     *
     * @param map the given  {@link TreeMap}
     * @param elements the given elements {@link Stream}
     */
    protected TreeContainer(TreeMap<E, Object> map, Stream<? extends E> elements) {
        super(map, 0); Map<Bucket<E>, Node<E>> tails = new HashMap<>(); elements.sequential().forEach(element -> {
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

    @Override public SortedContainer<E> sub(E fromElement, E toElement) {
        return new SortedSubContainer<E>(map.subMap(fromElement, toElement), this); }
    @Override public SortedContainer<E> head(E toElement) {
        return new SortedSubContainer<E>(map.headMap(toElement), this); }
    @Override public SortedContainer<E> tail(E fromElement) {
        return new SortedSubContainer<E>(map.tailMap(fromElement), this); }

    @Override public NavigableContainer<E> descending() {
        return new TreeContainer<>(map.descendingMap(), size, this); }

    @Override public NavigableContainer<E> sub(E fromElement, boolean fromInclusive, E toElement, boolean toInclusive) {
        return new TreeContainer<>(map.subMap(fromElement, fromInclusive, toElement, toInclusive), this); }
    @Override public NavigableContainer<E> head(E toElement, boolean inclusive) {
        return new TreeContainer<>(map.headMap(toElement, inclusive), this); }
    @Override public NavigableContainer<E> tail(E fromElement, boolean inclusive) {
        return new TreeContainer<>(map.tailMap(fromElement, inclusive), this); }

    @Override protected boolean isBucket(Object value) {
        return value instanceof Bucket; }
    @Override protected boolean isMulti() {
        return size > map.size(); }

    /**
     * Implementation of the {@link AbstractNavigableContainer.Node}.
     *
     * @param <E> the type of elements
     */
    protected static class Node<E> extends AbstractNavigableContainer.Node<E, Node<E>> {

        private static final long serialVersionUID = -2189656327506756149L;

        protected Node(E item) {
            super(item);
        }
    }

    /**
     * Implementation of the {@link AbstractNavigableContainer.Bucket}.
     *
     * @param <E> the type of elements
     */
    protected static class Bucket<E> extends AbstractNavigableContainer.Bucket<E, Node<E>, Bucket<E>> {

        private static final long serialVersionUID = 1609353765298667858L;

        protected Bucket(Node<E> head) {
            super(head);
        }
    }

    /**
     * Sub-container extension of the {@link AbstractSortedContainer}.
     *
     * @param <E> the element type
     */
    protected static class SortedSubContainer<E> extends AbstractSortedContainer.SortedSubContainer<E, Node<E>, Bucket<E>>
            implements RandomMatch {

        private static final long serialVersionUID = -5646956340075624242L;

        protected SortedSubContainer(SortedMap<E, Object> map, AbstractSortedContainer<E, TreeContainer.Node<E>, TreeContainer.Bucket<E>, ?> parent) {
            super(map, parent); }

        @Override public SortedContainer<E> sub(E fromElement, E toElement) {
            return new TreeContainer.SortedSubContainer<>(map.subMap(fromElement, toElement), this); }
        @Override public SortedContainer<E> head(E toElement) {
            return new TreeContainer.SortedSubContainer<>(map.headMap(toElement), this); }
        @Override public SortedContainer<E> tail(E fromElement) {
            return new TreeContainer.SortedSubContainer<>(map.tailMap(fromElement), this); }
    }


    /**
     * Return a {@link TreeContainer} of the given {@link Comparable} values.
     *
     * @param elements the given {@link Comparable} values
     * @param <E> the element type
     * @return a {@link TreeContainer} of the given {@link Comparable} values
     */
    @SafeVarargs
    public static <E extends Comparable<E>> TreeContainer<E> container(E... elements) {
        return new TreeContainer<>(elements);
    }

    /**
     * Return a {@link TreeContainer} of the given {@link Comparator} of the given values.
     *
     * @param comparator the given values {@link Comparator}
     * @param elements the given values
     * @param <E> the element type
     * @return a {@link TreeContainer} of the given {@link Comparator} of the given values
     */
    @SafeVarargs
    public static <E> TreeContainer<E> container(Comparator<? super E> comparator, E... elements) {
        return new TreeContainer<>(comparator, elements);
    }
}
