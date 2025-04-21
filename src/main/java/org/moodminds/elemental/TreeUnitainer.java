package org.moodminds.elemental;

import java.util.Comparator;
import java.util.NavigableMap;
import java.util.TreeMap;
import java.util.stream.Stream;

import static org.moodminds.elemental.Producer.producer;

/**
 * A {@link TreeMap}-powered unique-values implementation of the {@link NavigableContainer} interface.
 *
 * @param <E> the element type
 */
public class TreeUnitainer<E> extends AbstractNavigableUnitainer<E, NavigableMap<E, E>> {

    private static final long serialVersionUID = 1700051345468416546L;

    /**
     * Construct the object with the given elements vararg.
     *
     * @param elements the given elements vararg
     */
    @SafeVarargs
    public TreeUnitainer(E... elements) {
        this(new TreeMap<>(), producer(elements));
    }

    /**
     * Construct the object with the given {@link Comparator} and elements array.
     *
     * @param comparator the given elements {@link Comparator}
     * @param elements the given elements array
     */
    @SafeVarargs
    public TreeUnitainer(Comparator<? super E> comparator, E... elements) {
        this(new TreeMap<>(comparator), producer(elements));
    }

    /**
     * Construct the object with the given elements {@link Stream}.
     *
     * @param elements the given elements {@link Stream}
     */
    public TreeUnitainer(Stream<? extends E> elements) {
        this(new TreeMap<>(), elements.sequential()::forEach);
    }

    /**
     * Construct the object with the given {@link Comparator} and elements {@link Stream}.
     *
     * @param comparator the given elements {@link Comparator}
     * @param elements the given elements {@link Stream}
     */
    public TreeUnitainer(Comparator<? super E> comparator, Stream<? extends E> elements) {
        this(new TreeMap<>(comparator), elements.sequential()::forEach);
    }

    /**
     * Construct the object with the given elements {@link Container}.
     *
     * @param elements the given elements {@link Container}
     */
    public TreeUnitainer(Container<? extends E> elements) {
        this(new TreeMap<>(), elements::forEach);
    }

    /**
     * Construct the object with the given {@link Comparator} and elements {@link Container}.
     *
     * @param comparator the given elements {@link Comparator}
     * @param elements the given elements {@link Container}
     */
    public TreeUnitainer(Comparator<? super E> comparator, Container<? extends E> elements) {
        this(new TreeMap<>(comparator), elements::forEach);
    }

    /**
     * Construct the object with the given elements {@link java.util.Collection}.
     *
     * @param elements the given elements {@link java.util.Collection}
     */
    public TreeUnitainer(java.util.Collection<? extends E> elements) {
        this(new TreeMap<>(), elements::forEach);
    }

    /**
     * Construct the object with the given {@link Comparator} and elements {@link java.util.Collection}.
     *
     * @param comparator the given elements {@link Comparator}
     * @param elements the given elements {@link java.util.Collection}
     */
    public TreeUnitainer(Comparator<? super E> comparator, java.util.Collection<? extends E> elements) {
        this(new TreeMap<>(comparator), elements::forEach);
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
     * Construct the object with the given {@link TreeMap}
     * and sequential single-threaded {@link Producer} of elements.
     *
     * @param map the given  {@link TreeMap}
     * @param elements the given sequential single-threaded {@link Producer} of elements
     */
    protected TreeUnitainer(TreeMap<E, E> map, Producer<? extends E> elements) {
        super(map, elements);
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
