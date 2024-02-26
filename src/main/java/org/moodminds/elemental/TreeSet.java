package org.moodminds.elemental;

import java.util.Comparator;
import java.util.NavigableMap;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.stream.Stream;

/**
 * A {@link TreeMap}-powered implementation of the {@link NavigableSet} interface.
 * <p>
 * The main reason to prefer this class over wrapping {@link WrapNavigableSet}
 * is the efficient implementation of the {@link #getAll(Object)} method.
 *
 * @param <E> the element type
 */
public class TreeSet<E> extends AbstractNavigableSet<E, NavigableMap<E, Object>>
        implements RandomMatch {

    private static final long serialVersionUID = -2387185327556095202L;

    /**
     * Construct the object with the given elements vararg.
     *
     * @param elements the given elements vararg
     */
    @SafeVarargs
    public TreeSet(E... elements) {
        this(new TreeMap<>(), Stream.of(elements));
    }

    /**
     * Construct the object with the given {@link Comparator} and elements array.
     *
     * @param comparator the given elements {@link Comparator}
     * @param elements the given elements array
     */
    @SafeVarargs
    public TreeSet(Comparator<? super E> comparator, E... elements) {
        this(new TreeMap<>(comparator), Stream.of(elements));
    }

    /**
     * Construct the object with the given elements {@link Stream}.
     *
     * @param elements the given elements {@link Stream}
     */
    public TreeSet(Stream<? extends E> elements) {
        this(new TreeMap<>(), elements);
    }

    /**
     * Construct the object with the given {@link Comparator} and elements {@link Stream}.
     *
     * @param comparator the given elements {@link Comparator}
     * @param elements the given elements {@link Stream}
     */
    public TreeSet(Comparator<? super E> comparator, Stream<? extends E> elements) {
        this(new TreeMap<>(comparator), elements);
    }

    /**
     * Construct the object with the given elements {@link Container}.
     *
     * @param elements the given elements {@link Container}
     */
    public TreeSet(Container<? extends E> elements) {
        this(new TreeMap<>(), elements.stream());
    }

    /**
     * Construct the object with the given {@link Comparator} and elements {@link Container}.
     *
     * @param comparator the given elements {@link Comparator}
     * @param elements the given elements {@link Container}
     */
    public TreeSet(Comparator<? super E> comparator, Container<? extends E> elements) {
        this(new TreeMap<>(comparator), elements.stream());
    }

    /**
     * Construct the object with the given elements {@link java.util.Collection}.
     *
     * @param elements the given elements {@link java.util.Collection}
     */
    public TreeSet(java.util.Collection<? extends E> elements) {
        this(new TreeMap<>(), elements.stream());
    }

    /**
     * Construct the object with the given {@link Comparator} and elements {@link java.util.Collection}.
     *
     * @param comparator the given elements {@link Comparator}
     * @param elements the given elements {@link java.util.Collection}
     */
    public TreeSet(Comparator<? super E> comparator, java.util.Collection<? extends E> elements) {
        this(new TreeMap<>(comparator), elements.stream());
    }

    /**
     * Construct the object with the given elements {@link Collection}.
     *
     * @param elements the given elements {@link Collection}
     */
    public TreeSet(Collection<? extends E> elements) {
        this((java.util.Collection<? extends E>) elements);
    }

    /**
     * Construct the object with the given {@link Comparator} and elements {@link Collection}.
     *
     * @param comparator the given elements {@link Comparator}
     * @param elements the given elements {@link Collection}
     */
    public TreeSet(Comparator<? super E> comparator, Collection<? extends E> elements) {
        this(comparator, (java.util.Collection<? extends E>) elements);
    }

    /**
     * Construct the object with the given {@link NavigableMap} map.
     *
     * @param map the given {@link NavigableMap} map
     */
    protected TreeSet(NavigableMap<E, Object> map) {
        super(map);
    }

    /**
     * Construct the object with the given {@link TreeMap} and elements {@link Stream}.
     *
     * @param map the given  {@link TreeMap}
     * @param elements the given elements {@link Stream}
     */
    protected TreeSet(TreeMap<E, Object> map, Stream<? extends E> elements) {
        super(map); elements.sequential().forEach(this::add);
    }

    @Override public SortedSet<E> subSet(E fromElement, E toElement) {
        return new SortedSubSet<>(map.subMap(fromElement, toElement)); }
    @Override public SortedSet<E> headSet(E toElement) {
        return new SortedSubSet<>(map.headMap(toElement)); }
    @Override public SortedSet<E> tailSet(E fromElement) {
        return new SortedSubSet<>(map.tailMap(fromElement)); }

    @Override public NavigableSet<E> descendingSet() {
        return new TreeSet<>(map.descendingMap()); }

    @Override public NavigableSet<E> subSet(E fromElement, boolean fromInclusive, E toElement, boolean toInclusive) {
        return new TreeSet<>(map.subMap(fromElement, fromInclusive, toElement, toInclusive)); }
    @Override public NavigableSet<E> headSet(E toElement, boolean inclusive) {
        return new TreeSet<>(map.headMap(toElement, inclusive)); }
    @Override public NavigableSet<E> tailSet(E fromElement, boolean inclusive) {
        return new TreeSet<>(map.tailMap(fromElement, inclusive)); }


    /**
     * Sub-set extension of the {@link AbstractSortedUnitainer}.
     *
     * @param <E> the element type
     */
    protected static class SortedSubSet<E> extends AbstractSortedSet<E, SortedMap<E, Object>>
            implements RandomMatch {

        private static final long serialVersionUID = -6882257609001927320L;

        protected SortedSubSet(SortedMap<E, Object> map) { super(map); }

        @Override public SortedSet<E> subSet(E fromElement, E toElement) {
            return new SortedSubSet<>(map.subMap(fromElement, toElement)); }
        @Override public SortedSet<E> headSet(E toElement) {
            return new SortedSubSet<>(map.headMap(toElement)); }
        @Override public SortedSet<E> tailSet(E fromElement) {
            return new SortedSubSet<>(map.tailMap(fromElement)); }
    }


    /**
     * Return a {@link TreeSet} of the given {@link Comparable} values.
     *
     * @param elements the given {@link Comparable} values
     * @param <E> the element type
     * @return a {@link TreeSet} of the given {@link Comparable} values
     */
    @SafeVarargs
    public static <E extends Comparable<E>> TreeSet<E> set(E... elements) {
        return new TreeSet<>(elements);
    }

    /**
     * Return a {@link TreeSet} of the given {@link Comparator} of the given values.
     *
     * @param comparator the given values {@link Comparator}
     * @param elements the given values
     * @param <E> the element type
     * @return a {@link TreeSet} of the given {@link Comparator} of the given values
     */
    @SafeVarargs
    public static <E> TreeSet<E> set(Comparator<? super E> comparator, E... elements) {
        return new TreeSet<>(comparator, elements);
    }
}
