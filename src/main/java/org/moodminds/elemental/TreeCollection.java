package org.moodminds.elemental;

import java.io.Serializable;
import java.util.Comparator;
import java.util.NavigableMap;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.function.BiPredicate;
import java.util.stream.Stream;

/**
 * A {@link TreeMap}-powered implementation of the {@link NavigableCollection} interface.
 *
 * @param <E> the element type
 */
public class TreeCollection<E> extends AbstractNavigableCollection<E,
            TreeCollection.Node<E>, TreeCollection.Bucket<E>, NavigableMap<E, Object>>
        implements RandomMatch {

    private static final long serialVersionUID = -7986762787884728044L;

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
    public TreeCollection(E... elements) {
        this(new TreeMap<>(), Stream.of(elements));
    }

    /**
     * Construct the object with the given {@link Comparator} and elements array.
     *
     * @param comparator the given elements {@link Comparator}
     * @param elements the given elements array
     */
    @SafeVarargs
    public TreeCollection(Comparator<? super E> comparator, E... elements) {
        this(new TreeMap<>(comparator), Stream.of(elements));
    }

    /**
     * Construct the object with the given elements {@link Stream}.
     *
     * @param elements the given elements {@link Stream}
     */
    public TreeCollection(Stream<? extends E> elements) {
        this(new TreeMap<>(), elements);
    }

    /**
     * Construct the object with the given {@link Comparator} and elements {@link Stream}.
     *
     * @param comparator the given elements {@link Comparator}
     * @param elements the given elements {@link Stream}
     */
    public TreeCollection(Comparator<? super E> comparator, Stream<? extends E> elements) {
        this(new TreeMap<>(comparator), elements);
    }

    /**
     * Construct the object with the given elements {@link Container}.
     *
     * @param elements the given elements {@link Container}
     */
    public TreeCollection(Container<? extends E> elements) {
        this(new TreeMap<>(), elements.stream());
    }

    /**
     * Construct the object with the given {@link Comparator} and elements {@link Container}.
     *
     * @param comparator the given elements {@link Comparator}
     * @param elements the given elements {@link Container}
     */
    public TreeCollection(Comparator<? super E> comparator, Container<? extends E> elements) {
        this(new TreeMap<>(comparator), elements.stream());
    }

    /**
     * Construct the object with the given elements {@link java.util.Collection}.
     *
     * @param elements the given elements {@link java.util.Collection}
     */
    public TreeCollection(java.util.Collection<? extends E> elements) {
        this(new TreeMap<>(), elements.stream());
    }

    /**
     * Construct the object with the given {@link Comparator} and elements {@link java.util.Collection}.
     *
     * @param comparator the given elements {@link Comparator}
     * @param elements the given elements {@link java.util.Collection}
     */
    public TreeCollection(Comparator<? super E> comparator, java.util.Collection<? extends E> elements) {
        this(new TreeMap<>(comparator), elements.stream());
    }

    /**
     * Construct the object with the given elements {@link Collection}.
     *
     * @param elements the given elements {@link Collection}
     */
    public TreeCollection(Collection<? extends E> elements) {
        this((java.util.Collection<? extends E>) elements);
    }

    /**
     * Construct the object with the given {@link Comparator} and elements {@link Collection}.
     *
     * @param comparator the given elements {@link Comparator}
     * @param elements the given elements {@link Collection}
     */
    public TreeCollection(Comparator<? super E> comparator, Collection<? extends E> elements) {
        this(comparator, (java.util.Collection<? extends E>) elements);
    }

    /**
     * Construct the object by the given {@link NavigableMap} map, parent Collection
     * and {@link BiPredicate} elements range check bound for subs.
     *
     * @param map the given {@link NavigableMap} map
     * @param parent the given parent Collection
     * @param range the given {@link BiPredicate} elements range check bound
     * @param <P> the type of the {@link Serializable} {@link BiPredicate} intersection
     */
    protected <P extends BiPredicate<Comparator<E>, E> & Serializable> TreeCollection(NavigableMap<E, Object> map,
                                                                                      AbstractNavigableCollection<E, Node<E>, Bucket<E>, ?> parent,
                                                                                      P range) {
        super(map, parent, range);
    }

    /**
     * Construct the object by the given {@link NavigableMap} map, size and parent Collection.
     *
     * @param map the given {@link NavigableMap} map
     * @param size the given size
     * @param parent the given parent Collection
     */
    protected TreeCollection(NavigableMap<E, Object> map, Integer size,
                             AbstractNavigableCollection<E, Node<E>, Bucket<E>, ?> parent) {
        super(map, size, parent);
    }

    /**
     * Construct the object with the given {@link TreeMap} and elements {@link Stream}.
     *
     * @param map the given  {@link TreeMap}
     * @param elements the given elements {@link Stream}
     */
    protected TreeCollection(TreeMap<E, Object> map, Stream<? extends E> elements) {
        super(map, 0); elements.sequential().peek(ignored -> size++).forEach(this::put);
    }

    @Override public SortedCollection<E> sub(E fromElement, E toElement) {
        return new SortedSubCollection<E>(map.subMap(fromElement, toElement), this, (comp, element) ->
                comp.compare(element, fromElement) >= 0 && comp.compare(element, toElement) < 0); }
    @Override public SortedCollection<E> head(E toElement) {
        return new SortedSubCollection<E>(map.headMap(toElement), this, (comp, element) ->
                comp.compare(element, toElement) < 0); }
    @Override public SortedCollection<E> tail(E fromElement) {
        return new SortedSubCollection<E>(map.tailMap(fromElement), this, (comp, element) ->
                comp.compare(element, fromElement) >= 0); }

    @Override public NavigableCollection<E> descending() {
        return new TreeCollection<>(map.descendingMap(), size, this); }

    @Override public NavigableCollection<E> sub(E fromElement, boolean fromInclusive, E toElement, boolean toInclusive) {
        return new TreeCollection<>(map.subMap(fromElement, fromInclusive, toElement, toInclusive), this, (comp, element) ->
                comp.compare(element, fromElement) >= ((fromInclusive ? 0 : 1))
                        && comp.compare(element, toElement) <= ((toInclusive ? 0 : -1))); }

    @Override public NavigableCollection<E> head(E toElement, boolean inclusive) {
        return new TreeCollection<>(map.headMap(toElement, inclusive), this, (comp, element) ->
                comp.compare(element, toElement) <= ((inclusive ? 0 : -1))); }

    @Override public NavigableCollection<E> tail(E fromElement, boolean inclusive) {
        return new TreeCollection<>(map.tailMap(fromElement, inclusive), this, (comp, element) ->
                comp.compare(element, fromElement) >= ((inclusive ? 0 : 1))); }

    @Override protected int totalMod() {
        return modCount; }
    @Override protected void countMod() {
        modCount++; }

    @Override protected Node<E> node(E item) {
        return new Node<>(item); }
    @Override protected Bucket<E> bucket(Node<E> node) {
        return new Bucket<>(node); }
    @Override protected boolean isBucket(Object value) {
        return value instanceof Bucket; }
    @Override protected boolean isMulti() {
        return size > map.size(); }


    /**
     * Implementation of the {@link AbstractNavigableCollection.Node}.
     *
     * @param <E> the type of elements
     */
    protected static class Node<E> extends AbstractNavigableCollection.Node<E, Node<E>> {

        private static final long serialVersionUID = 5224725205035986747L;

        protected Node(E item) {
            super(item);
        }
    }

    /**
     * Implementation of the {@link AbstractNavigableCollection.Bucket}.
     *
     * @param <E> the type of elements
     */
    protected static class Bucket<E> extends AbstractNavigableCollection.Bucket<E, Node<E>, Bucket<E>> {

        private static final long serialVersionUID = 5239445184518394031L;

        protected Bucket(Node<E> head) {
            super(head);
        }

        @Override
        public BucketIterator iterator() {
            return new TailIterator();
        }
    }

    /**
     * Sub-collection extension of the {@link AbstractSortedCollection}.
     *
     * @param <E> the element type
     */
    protected static class SortedSubCollection<E> extends AbstractSortedCollection.SortedSubCollection<E, Node<E>, Bucket<E>>
            implements RandomMatch {

        private static final long serialVersionUID = 1141000736188406027L;

        protected <P extends BiPredicate<Comparator<E>, E> & Serializable> SortedSubCollection(SortedMap<E, Object> map,
                                                                                               AbstractSortedCollection<E, TreeCollection.Node<E>, TreeCollection.Bucket<E>, ?> parent,
                                                                                               P range) {
            super(map, parent, range);
        }

        @Override public SortedCollection<E> sub(E fromElement, E toElement) {
            return new TreeCollection.SortedSubCollection<>(map.subMap(fromElement, toElement), this, (comp, element) ->
                    comp.compare(element, fromElement) >= 0 && comp.compare(element, toElement) < 0); }
        @Override public SortedCollection<E> head(E toElement) {
            return new TreeCollection.SortedSubCollection<>(map.headMap(toElement), this, (comp, element) ->
                    comp.compare(element, toElement) < 0); }
        @Override public SortedCollection<E> tail(E fromElement) {
            return new TreeCollection.SortedSubCollection<>(map.tailMap(fromElement), this, (comp, element) ->
                    comp.compare(element, fromElement) >= 0); }
    }

    /**
     * Return a {@link TreeCollection} of the given {@link Comparable} values.
     *
     * @param elements the given {@link Comparable} values
     * @param <E> the element type
     * @return a {@link TreeCollection} of the given {@link Comparable} values
     */
    @SafeVarargs
    public static <E extends Comparable<E>> TreeCollection<E> collection(E... elements) {
        return new TreeCollection<>(elements);
    }

    /**
     * Return a {@link TreeCollection} of the given {@link Comparator} of the given values.
     *
     * @param comparator the given values {@link Comparator}
     * @param elements the given values
     * @param <E> the element type
     * @return a {@link TreeCollection} of the given {@link Comparator} of the given values
     */
    @SafeVarargs
    public static <E> TreeCollection<E> collection(Comparator<? super E> comparator, E... elements) {
        return new TreeCollection<>(comparator, elements);
    }
}
