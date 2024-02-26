package org.moodminds.elemental;

import java.io.ObjectInputStream;
import java.util.LinkedHashMap;
import java.util.stream.Stream;

import static org.moodminds.sneaky.Cast.cast;

/**
 * A {@link LinkedHashMap}-powered unique-values implementation
 * of the {@link Container} interface, preserving the order of elements
 * as defined during construction.
 *
 * @param <E> the element type
 */
public class LinkHashUnitainer<E> extends HashUnitainer<E> {

    private static final long serialVersionUID = 2972747402154333469L;

    /**
     * Construct the object with the given elements vararg.
     *
     * @param elements the given elements vararg
     */
    @SafeVarargs
    public LinkHashUnitainer(E... elements) {
        this(new LinkedHashMap<>(capacity(elements.length)), Stream.of(elements));
    }

    /**
     * Construct the object with the given elements {@link Stream}.
     *
     * @param elements the given elements {@link Stream}
     */
    public LinkHashUnitainer(Stream<? extends E> elements) {
        this(new LinkedHashMap<>(), elements);
    }

    /**
     * Construct the object with the given elements {@link Container}.
     *
     * @param elements the given elements {@link Container}
     */
    public LinkHashUnitainer(Container<? extends E> elements) {
        this(new LinkedHashMap<>(capacity(elements.size())), elements.stream());
    }

    /**
     * Construct the object with the given elements {@link java.util.Collection}.
     *
     * @param elements the given elements {@link java.util.Collection}
     */
    public LinkHashUnitainer(java.util.Collection<? extends E> elements) {
        this(new LinkedHashMap<>(capacity(elements.size())), elements.stream());
    }

    /**
     * Construct the object with the given elements {@link Collection}.
     *
     * @param elements the given elements {@link Collection}
     */
    public LinkHashUnitainer(Collection<? extends E> elements) {
        this((java.util.Collection<? extends E>) elements);
    }

    /**
     * Construct the object with the given {@link LinkedHashMap}, elements {@link Stream} and size.
     *
     * @param map the given  {@link LinkedHashMap}
     * @param elements the given elements {@link Stream}
     */
    protected LinkHashUnitainer(LinkedHashMap<E, E> map, Stream<? extends E> elements) {
        super(map, elements);
    }

    @Override
    protected void deserialize(ObjectInputStream input) throws Exception {
        int size = input.readInt();
        map = new LinkedHashMap<>(capacity(size));
        for (int i = 0; i < size; i++) {
            E item = cast(input.readObject()); map.put(item, item);
        }
    }


    /**
     * Return a {@link LinkHashUnitainer} of the given values.
     *
     * @param elements the given values
     * @param <E> the element type
     * @return a {@link LinkHashUnitainer} of the given values
     */
    @SafeVarargs
    public static <E> LinkHashUnitainer<E> unitainer(E... elements) {
        return new LinkHashUnitainer<>(elements);
    }
}
