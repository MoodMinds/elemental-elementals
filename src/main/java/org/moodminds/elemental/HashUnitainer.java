package org.moodminds.elemental;

import java.util.HashMap;
import java.util.stream.Stream;

import static org.moodminds.elemental.Producer.producer;

/**
 * A {@link HashMap}-powered unique-values implementation of the {@link Container} interface.
 *
 * @param <E> the element type
 */
public class HashUnitainer<E> extends AbstractHashUnitainer<E> {

    private static final long serialVersionUID = -7129889425562902968L;

    /**
     * Construct the object with the given elements array.
     *
     * @param elements the given elements array
     */
    @SafeVarargs
    public HashUnitainer(E... elements) {
        this(new HashMap<>(capacity(elements.length)), producer(elements));
    }

    /**
     * Construct the object with the given sequential
     * single-threaded {@link Producer} of elements.
     *
     * @param elements the given sequential single-threaded {@link Producer} of elements
     */
    public HashUnitainer(Producer<? extends E> elements) {
        this(new HashMap<>(), elements);
    }

    /**
     * Construct the object with the given elements {@link Stream}.
     *
     * @param elements the given elements {@link Stream}
     */
    public HashUnitainer(Stream<? extends E> elements) {
        this(new HashMap<>(), elements.sequential()::forEach);
    }

    /**
     * Construct the object with the given elements {@link Container}.
     *
     * @param elements the given elements {@link Container}
     */
    public HashUnitainer(Container<? extends E> elements) {
        this(new HashMap<>(capacity(elements.size())), elements::forEach);
    }

    /**
     * Construct the object with the given elements {@link java.util.Collection}.
     *
     * @param elements the given elements {@link java.util.Collection}
     */
    public HashUnitainer(java.util.Collection<? extends E> elements) {
        this(new HashMap<>(capacity(elements.size())), elements::forEach);
    }

    /**
     * Construct the object with the given elements {@link Collection}.
     *
     * @param elements the given elements {@link Collection}
     */
    public HashUnitainer(Collection<? extends E> elements) {
        this((java.util.Collection<? extends E>) elements);
    }

    /**
     * Construct the object with the given {@link HashMap}
     * and sequential single-threaded {@link Producer} of elements.
     *
     * @param map the given {@link HashMap}
     * @param elements the given sequential single-threaded {@link Producer} of elements
     */
    protected HashUnitainer(HashMap<E, E> map, Producer<? extends E> elements) {
        super(map, elements);
    }

    @Override protected HashMap<E, E> map(int initialCapacity) {
        return new HashMap<>(initialCapacity); }


    /**
     * Return a {@link HashUnitainer} of the given values.
     *
     * @param elements the given values
     * @param <E> the element type
     * @return a {@link HashUnitainer} of the given values
     */
    @SafeVarargs
    public static <E> HashUnitainer<E> unitainer(E... elements) {
        return new HashUnitainer<>(elements);
    }
}
