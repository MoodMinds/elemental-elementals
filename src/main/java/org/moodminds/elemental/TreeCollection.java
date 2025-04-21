package org.moodminds.elemental;

import java.util.Comparator;
import java.util.NavigableMap;
import java.util.Optional;
import java.util.TreeMap;
import java.util.stream.Stream;

import static org.moodminds.elemental.Producer.producer;

/**
 * A {@link TreeMap}-powered implementation of the {@link NavigableCollection} interface.
 *
 * @param <E> the element type
 */
public class TreeCollection<E> extends AbstractNavigableCollection<E, NavigableMap<E, Object>> {

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
        this(new TreeMap<>(), producer(elements));
    }

    /**
     * Construct the object with the given {@link Comparator} and elements array.
     *
     * @param comparator the given elements {@link Comparator}
     * @param elements the given elements array
     */
    @SafeVarargs
    public TreeCollection(Comparator<? super E> comparator, E... elements) {
        this(new TreeMap<>(comparator), producer(elements));
    }

    /**
     * Construct the object with the given elements {@link Stream}.
     *
     * @param elements the given elements {@link Stream}
     */
    public TreeCollection(Stream<? extends E> elements) {
        this(new TreeMap<>(), elements.sequential()::forEach);
    }

    /**
     * Construct the object with the given {@link Comparator} and elements {@link Stream}.
     *
     * @param comparator the given elements {@link Comparator}
     * @param elements the given elements {@link Stream}
     */
    public TreeCollection(Comparator<? super E> comparator, Stream<? extends E> elements) {
        this(new TreeMap<>(comparator), elements.sequential()::forEach);
    }

    /**
     * Construct the object with the given elements {@link Container}.
     *
     * @param elements the given elements {@link Container}
     */
    public TreeCollection(Container<? extends E> elements) {
        this(new TreeMap<>(), elements::forEach);
    }

    /**
     * Construct the object with the given {@link Comparator} and elements {@link Container}.
     *
     * @param comparator the given elements {@link Comparator}
     * @param elements the given elements {@link Container}
     */
    public TreeCollection(Comparator<? super E> comparator, Container<? extends E> elements) {
        this(new TreeMap<>(comparator), elements::forEach);
    }

    /**
     * Construct the object with the given elements {@link java.util.Collection}.
     *
     * @param elements the given elements {@link java.util.Collection}
     */
    public TreeCollection(java.util.Collection<? extends E> elements) {
        this(new TreeMap<>(), elements::forEach);
    }

    /**
     * Construct the object with the given {@link Comparator} and elements {@link java.util.Collection}.
     *
     * @param comparator the given elements {@link Comparator}
     * @param elements the given elements {@link java.util.Collection}
     */
    public TreeCollection(Comparator<? super E> comparator, java.util.Collection<? extends E> elements) {
        this(new TreeMap<>(comparator), elements::forEach);
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
     * Construct the object with the given {@link TreeMap map}
     * and sequential single-threaded {@link Producer} of elements.
     *
     * @param map the given {@link TreeMap map}
     * @param elements the given sequential single-threaded {@link Producer} of elements
     */
    protected TreeCollection(TreeMap<E, Object> map, Producer<? extends E> elements) {
        super(map, elements); }

    @Override public SortedCollection<E> sub(E fromElement, E toElement) {
        return new SortedSubCollection<>(this, this, map.subMap(fromElement, toElement), new Range<>(map.comparator(), fromElement, true, toElement, false)); }
    @Override public SortedCollection<E> head(E toElement) {
        return new SortedSubCollection<>(this, this, map.headMap(toElement), new Range<>(map.comparator(), null, false, toElement, false)); }
    @Override public SortedCollection<E> tail(E fromElement) {
        return new SortedSubCollection<>(this, this, map.tailMap(fromElement), new Range<>(map.comparator(), fromElement, true, null, false)); }

    @Override public NavigableCollection<E> descending() {
        return new DescendingNavigableSubCollection<>(this, this, map.descendingMap(), size, new Range<>(map.comparator())); }

    @Override public NavigableCollection<E> sub(E fromElement, boolean fromInclusive, E toElement, boolean toInclusive) {
        return new NavigableSubCollection<>(this, this, map.subMap(fromElement, fromInclusive, toElement, toInclusive), new Range<>(map.comparator(), fromElement, fromInclusive, toElement, toInclusive)); }
    @Override public NavigableCollection<E> head(E toElement, boolean inclusive) {
        return new NavigableSubCollection<>(this, this, map.headMap(toElement, inclusive), new Range<>(map.comparator(), null, false, toElement, inclusive)); }
    @Override public NavigableCollection<E> tail(E fromElement, boolean inclusive) {
        return new NavigableSubCollection<>(this, this, map.tailMap(fromElement, inclusive), new Range<>(map.comparator(), fromElement, inclusive, null, false)); }

    @Override protected int totalMod() { return modCount; }
    @Override protected void countMod() { modCount++; }

    @Override protected Optional<Boolean> isDistinct() {
        return getSize().map(size -> size == map.size()); }


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
