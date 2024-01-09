package org.moodminds.elemental;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

import static java.lang.Math.max;

/**
 * A {@link HashMap}-powered unique-values implementation of the {@link Container} interface.
 *
 * @param <E> the element type
 */
public class HashUnitainer<E> extends AbstractUnitainer<E, Map<E, E>>
        implements RandomMatch {

    private static final long serialVersionUID = -7129889425562902968L;

    /**
     * Construct the object with the given elements array.
     *
     * @param elements the given elements array
     */
    @SafeVarargs
    public HashUnitainer(E... elements) {
        this(new HashMap<>(max((int) (elements.length/.75f) + 1, 16)), Stream.of(elements));
    }

    /**
     * Construct the object with the given elements {@link Stream}.
     *
     * @param elements the given elements {@link Stream}
     */
    public HashUnitainer(Stream<? extends E> elements) {
        this(new HashMap<>(), elements);
    }

    /**
     * Construct the object with the given elements {@link Container}.
     *
     * @param elements the given elements {@link Container}
     */
    public HashUnitainer(Container<? extends E> elements) {
        this(new HashMap<>(max((int) (elements.size()/.75f) + 1, 16)), elements.stream());
    }

    /**
     * Construct the object with the given elements {@link java.util.Collection}.
     *
     * @param elements the given elements {@link java.util.Collection}
     */
    public HashUnitainer(java.util.Collection<? extends E> elements) {
        this(new HashMap<>(max((int) (elements.size()/.75f) + 1, 16)), elements.stream());
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
     * Construct the object with the given {@link HashMap}, elements {@link Stream} and size.
     *
     * @param map the given  {@link HashMap}
     * @param elements the given elements {@link Stream}
     */
    protected HashUnitainer(HashMap<E, E> map, Stream<? extends E> elements) {
        super(map); elements.forEach(element -> map.put(element, element));
    }

    /**
     * Return a {@link HashUnitainer} of the given values.
     *
     * @param elements the given values
     * @param <E> the element type
     * @return a {@link HashUnitainer} of the given values
     */
    @SafeVarargs
    public static <E> HashUnitainer<E> container(E... elements) {
        return new HashUnitainer<>(elements);
    }
}
