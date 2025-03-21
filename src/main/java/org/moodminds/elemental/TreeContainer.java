package org.moodminds.elemental;

import java.util.Comparator;
import java.util.NavigableMap;
import java.util.Optional;
import java.util.TreeMap;
import java.util.stream.Stream;

import static org.moodminds.elemental.Producer.producer;

/**
 * A {@link TreeMap}-powered implementation of the {@link NavigableContainer} interface.
 *
 * @param <E> the element type
 */
public class TreeContainer<E> extends AbstractNavigableContainer<E, NavigableMap<E, Object>> {

    private static final long serialVersionUID = 4964896021903868804L;

    /**
     * Construct the object with the given elements vararg.
     *
     * @param elements the given elements vararg
     */
    @SafeVarargs
    public TreeContainer(E... elements) {
        this(new TreeMap<>(), producer(elements));
    }

    /**
     * Construct the object with the given {@link Comparator} and elements array.
     *
     * @param comparator the given elements {@link Comparator}
     * @param elements the given elements array
     */
    @SafeVarargs
    public TreeContainer(Comparator<? super E> comparator, E... elements) {
        this(new TreeMap<>(comparator), producer(elements));
    }

    /**
     * Construct the object with the given elements {@link Stream}.
     *
     * @param elements the given elements {@link Stream}
     */
    public TreeContainer(Stream<? extends E> elements) {
        this(new TreeMap<>(), elements.sequential()::forEach);
    }

    /**
     * Construct the object with the given {@link Comparator} and elements {@link Stream}.
     *
     * @param comparator the given elements {@link Comparator}
     * @param elements the given elements {@link Stream}
     */
    public TreeContainer(Comparator<? super E> comparator, Stream<? extends E> elements) {
        this(new TreeMap<>(comparator), elements.sequential()::forEach);
    }

    /**
     * Construct the object with the given elements {@link Container}.
     *
     * @param elements the given elements {@link Container}
     */
    public TreeContainer(Container<? extends E> elements) {
        this(new TreeMap<>(), elements::forEach);
    }

    /**
     * Construct the object with the given {@link Comparator} and elements {@link Container}.
     *
     * @param comparator the given elements {@link Comparator}
     * @param elements the given elements {@link Container}
     */
    public TreeContainer(Comparator<? super E> comparator, Container<? extends E> elements) {
        this(new TreeMap<>(comparator), elements::forEach);
    }

    /**
     * Construct the object with the given elements {@link java.util.Collection}.
     *
     * @param elements the given elements {@link java.util.Collection}
     */
    public TreeContainer(java.util.Collection<? extends E> elements) {
        this(new TreeMap<>(), elements::forEach);
    }

    /**
     * Construct the object with the given {@link Comparator} and elements {@link java.util.Collection}.
     *
     * @param comparator the given elements {@link Comparator}
     * @param elements the given elements {@link java.util.Collection}
     */
    public TreeContainer(Comparator<? super E> comparator, java.util.Collection<? extends E> elements) {
        this(new TreeMap<>(comparator), elements::forEach);
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
     * Construct the object with the given {@link TreeMap map}
     * and sequential single-threaded {@link Producer} of elements.
     *
     * @param map the given {@link TreeMap map}
     * @param elements the given sequential single-threaded {@link Producer} of elements
     */
    protected TreeContainer(TreeMap<E, Object> map, Producer<? extends E> elements) {
        super(map, elements);
    }

    @Override protected Optional<Boolean> isDistinct() {
        return getSize().map(size -> size == map.size()); }


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
