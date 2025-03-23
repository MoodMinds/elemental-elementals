package org.moodminds.elemental;

import java.util.Comparator;
import java.util.NavigableMap;
import java.util.TreeMap;
import java.util.stream.Stream;

import static org.moodminds.elemental.Producer.producer;

/**
 * A {@link TreeMap}-powered implementation of the {@link NavigableSet} interface.
 * <p>
 * The main reason to prefer this class over wrapping {@link WrapNavigableSet}
 * is the efficient implementation of the {@link #getAll(Object)} method.
 *
 * @param <E> the element type
 */
public class TreeSet<E> extends AbstractNavigableSet<E, NavigableMap<E, E>> {

    private static final long serialVersionUID = -2387185327556095202L;

    /**
     * Construct the object with the given elements vararg.
     *
     * @param elements the given elements vararg
     */
    @SafeVarargs
    public TreeSet(E... elements) {
        this(new TreeMap<>(), producer(elements));
    }

    /**
     * Construct the object with the given {@link Comparator} and elements array.
     *
     * @param comparator the given elements {@link Comparator}
     * @param elements the given elements array
     */
    @SafeVarargs
    public TreeSet(Comparator<? super E> comparator, E... elements) {
        this(new TreeMap<>(comparator), producer(elements));
    }

    /**
     * Construct the object with the given elements {@link Stream}.
     *
     * @param elements the given elements {@link Stream}
     */
    public TreeSet(Stream<? extends E> elements) {
        this(new TreeMap<>(), elements.sequential()::forEach);
    }

    /**
     * Construct the object with the given {@link Comparator} and elements {@link Stream}.
     *
     * @param comparator the given elements {@link Comparator}
     * @param elements the given elements {@link Stream}
     */
    public TreeSet(Comparator<? super E> comparator, Stream<? extends E> elements) {
        this(new TreeMap<>(comparator), elements.sequential()::forEach);
    }

    /**
     * Construct the object with the given elements {@link Container}.
     *
     * @param elements the given elements {@link Container}
     */
    public TreeSet(Container<? extends E> elements) {
        this(new TreeMap<>(), elements::forEach);
    }

    /**
     * Construct the object with the given {@link Comparator} and elements {@link Container}.
     *
     * @param comparator the given elements {@link Comparator}
     * @param elements the given elements {@link Container}
     */
    public TreeSet(Comparator<? super E> comparator, Container<? extends E> elements) {
        this(new TreeMap<>(comparator), elements::forEach);
    }

    /**
     * Construct the object with the given elements {@link java.util.Collection}.
     *
     * @param elements the given elements {@link java.util.Collection}
     */
    public TreeSet(java.util.Collection<? extends E> elements) {
        this(new TreeMap<>(), elements::forEach);
    }

    /**
     * Construct the object with the given {@link Comparator} and elements {@link java.util.Collection}.
     *
     * @param comparator the given elements {@link Comparator}
     * @param elements the given elements {@link java.util.Collection}
     */
    public TreeSet(Comparator<? super E> comparator, java.util.Collection<? extends E> elements) {
        this(new TreeMap<>(comparator), elements::forEach);
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
     * Construct the object with the given {@link TreeMap}
     * and sequential single-threaded {@link Producer} of elements.
     *
     * @param map the given  {@link TreeMap}
     * @param elements the given sequential single-threaded {@link Producer} of elements
     */
    protected TreeSet(TreeMap<E, E> map, Producer<? extends E> elements) {
        super(map, elements);
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
