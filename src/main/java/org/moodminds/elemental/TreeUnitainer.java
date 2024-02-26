package org.moodminds.elemental;

import java.util.Comparator;
import java.util.NavigableMap;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.stream.Stream;

/**
 * A {@link TreeMap}-powered unique-values implementation of the {@link NavigableContainer} interface.
 *
 * @param <E> the element type
 */
public class TreeUnitainer<E> extends AbstractNavigableUnitainer<E, NavigableMap<E, E>>
        implements RandomMatch {

    private static final long serialVersionUID = 1700051345468416546L;

    /**
     * Construct the object with the given elements vararg.
     *
     * @param elements the given elements vararg
     */
    @SafeVarargs
    public TreeUnitainer(E... elements) {
        this(new TreeMap<>(), Stream.of(elements));
    }

    /**
     * Construct the object with the given {@link Comparator} and elements array.
     *
     * @param comparator the given elements {@link Comparator}
     * @param elements the given elements array
     */
    @SafeVarargs
    public TreeUnitainer(Comparator<? super E> comparator, E... elements) {
        this(new TreeMap<>(comparator), Stream.of(elements));
    }

    /**
     * Construct the object with the given elements {@link Stream}.
     *
     * @param elements the given elements {@link Stream}
     */
    public TreeUnitainer(Stream<? extends E> elements) {
        this(new TreeMap<>(), elements);
    }

    /**
     * Construct the object with the given {@link Comparator} and elements {@link Stream}.
     *
     * @param comparator the given elements {@link Comparator}
     * @param elements the given elements {@link Stream}
     */
    public TreeUnitainer(Comparator<? super E> comparator, Stream<? extends E> elements) {
        this(new TreeMap<>(comparator), elements);
    }

    /**
     * Construct the object with the given elements {@link Container}.
     *
     * @param elements the given elements {@link Container}
     */
    public TreeUnitainer(Container<? extends E> elements) {
        this(new TreeMap<>(), elements.stream());
    }

    /**
     * Construct the object with the given {@link Comparator} and elements {@link Container}.
     *
     * @param comparator the given elements {@link Comparator}
     * @param elements the given elements {@link Container}
     */
    public TreeUnitainer(Comparator<? super E> comparator, Container<? extends E> elements) {
        this(new TreeMap<>(comparator), elements.stream());
    }

    /**
     * Construct the object with the given elements {@link java.util.Collection}.
     *
     * @param elements the given elements {@link java.util.Collection}
     */
    public TreeUnitainer(java.util.Collection<? extends E> elements) {
        this(new TreeMap<>(), elements.stream());
    }

    /**
     * Construct the object with the given {@link Comparator} and elements {@link java.util.Collection}.
     *
     * @param comparator the given elements {@link Comparator}
     * @param elements the given elements {@link java.util.Collection}
     */
    public TreeUnitainer(Comparator<? super E> comparator, java.util.Collection<? extends E> elements) {
        this(new TreeMap<>(comparator), elements.stream());
    }

    /**
     * Construct the object with the given elements {@link Collection}.
     *
     * @param elements the given elements {@link Collection}
     */
    public TreeUnitainer(Collection<? extends E> elements) {
        this((java.util.Collection<? extends E>) elements);
    }

    /**
     * Construct the object with the given {@link Comparator} and elements {@link Collection}.
     *
     * @param comparator the given elements {@link Comparator}
     * @param elements the given elements {@link Collection}
     */
    public TreeUnitainer(Comparator<? super E> comparator, Collection<? extends E> elements) {
        this(comparator, (java.util.Collection<? extends E>) elements);
    }

    /**
     * Construct the object with the given {@link NavigableMap} map.
     *
     * @param map the given {@link NavigableMap} map
     */
    protected TreeUnitainer(NavigableMap<E, E> map) {
        super(map);
    }

    /**
     * Construct the object with the given {@link TreeMap}, elements {@link Stream} and size.
     *
     * @param map the given  {@link TreeMap}
     * @param elements the given elements {@link Stream}
     */
    protected TreeUnitainer(TreeMap<E, E> map, Stream<? extends E> elements) {
        super(map); elements.sequential().forEach(element -> map.put(element, element));
    }

    @Override public SortedContainer<E> sub(E fromElement, E toElement) {
        return new SortedSubContainer<>(map.subMap(fromElement, toElement)); }
    @Override public SortedContainer<E> head(E toElement) {
        return new SortedSubContainer<>(map.headMap(toElement)); }
    @Override public SortedContainer<E> tail(E fromElement) {
        return new SortedSubContainer<>(map.tailMap(fromElement)); }

    @Override public NavigableContainer<E> descending() {
        return new TreeUnitainer<>(map.descendingMap()); }

    @Override public NavigableContainer<E> sub(E fromElement, boolean fromInclusive, E toElement, boolean toInclusive) {
        return new TreeUnitainer<>(map.subMap(fromElement, fromInclusive, toElement, toInclusive)); }
    @Override public NavigableContainer<E> head(E toElement, boolean inclusive) {
        return new TreeUnitainer<>(map.headMap(toElement, inclusive)); }
    @Override public NavigableContainer<E> tail(E fromElement, boolean inclusive) {
        return new TreeUnitainer<>(map.tailMap(fromElement, inclusive)); }


    /**
     * Sub-container extension of the {@link AbstractSortedUnitainer}.
     *
     * @param <E> the element type
     */
    protected static class SortedSubContainer<E> extends AbstractSortedUnitainer<E, SortedMap<E, E>>
            implements RandomMatch {

        private static final long serialVersionUID = 3481554803700758249L;

        protected SortedSubContainer(SortedMap<E, E> map) { super(map); }

        @Override public SortedContainer<E> sub(E fromElement, E toElement) {
            return new SortedSubContainer<>(map.subMap(fromElement, toElement)); }
        @Override public SortedContainer<E> head(E toElement) {
            return new SortedSubContainer<>(map.headMap(toElement)); }
        @Override public SortedContainer<E> tail(E fromElement) {
            return new SortedSubContainer<>(map.tailMap(fromElement)); }
    }


    /**
     * Return a {@link TreeUnitainer} of the given {@link Comparable} values.
     *
     * @param elements the given {@link Comparable} values
     * @param <E> the element type
     * @return a {@link TreeUnitainer} of the given {@link Comparable} values
     */
    @SafeVarargs
    public static <E extends Comparable<E>> TreeUnitainer<E> unitainer(E... elements) {
        return new TreeUnitainer<>(elements);
    }

    /**
     * Return a {@link TreeUnitainer} of the given {@link Comparator} of the given values.
     *
     * @param comparator the given values {@link Comparator}
     * @param elements the given values
     * @param <E> the element type
     * @return a {@link TreeUnitainer} of the given {@link Comparator} of the given values
     */
    @SafeVarargs
    public static <E> TreeUnitainer<E> unitainer(Comparator<? super E> comparator, E... elements) {
        return new TreeUnitainer<>(comparator, elements);
    }
}
