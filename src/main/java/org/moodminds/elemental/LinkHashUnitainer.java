package org.moodminds.elemental;

import java.io.InvalidObjectException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Stream;

import static java.lang.Math.max;
import static org.moodminds.elemental.Producer.producer;
import static org.moodminds.sneaky.Cast.cast;

/**
 * A {@link LinkedHashMap}-powered unique-values implementation
 * of the {@link Container} interface, preserving the order of elements
 * as defined during construction.
 *
 * @param <E> the element type
 */
public class LinkHashUnitainer<E> extends AbstractMapUnitainer<E, Map<E, E>> {

    private static final long serialVersionUID = 2972747402154333469L;

    /**
     * Construct the object with the given elements vararg.
     *
     * @param elements the given elements vararg
     */
    @SafeVarargs
    public LinkHashUnitainer(E... elements) {
        this(new LinkedHashMap<>(capacity(elements.length)), producer(elements));
    }

    /**
     * Construct the object with the given elements {@link Stream}.
     *
     * @param elements the given elements {@link Stream}
     */
    public LinkHashUnitainer(Stream<? extends E> elements) {
        this(new LinkedHashMap<>(), elements.sequential()::forEach);
    }

    /**
     * Construct the object with the given elements {@link Container}.
     *
     * @param elements the given elements {@link Container}
     */
    public LinkHashUnitainer(Container<? extends E> elements) {
        this(new LinkedHashMap<>(capacity(elements.size())), elements::forEach);
    }

    /**
     * Construct the object with the given elements {@link java.util.Collection}.
     *
     * @param elements the given elements {@link java.util.Collection}
     */
    public LinkHashUnitainer(java.util.Collection<? extends E> elements) {
        this(new LinkedHashMap<>(capacity(elements.size())), elements::forEach);
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
     * Construct the object with the given {@link LinkedHashMap}
     * and sequential single-threaded {@link Producer} of elements.
     *
     * @param map the given  {@link LinkedHashMap}
     * @param elements the given sequential single-threaded {@link Producer} of elements
     */
    protected LinkHashUnitainer(LinkedHashMap<E, E> map, Producer<? extends E> elements) {
        super(map); elements.provide(element -> map.putIfAbsent(element, element));
    }

    @Override
    protected void serialize(ObjectOutputStream output) throws Exception {
        output.writeInt(map.size()); for (E e : this)
            output.writeObject(e);
    }

    @Override
    protected void deserialize(ObjectInputStream input) throws Exception {
        int size; if ((size = input.readInt()) < 0)
            throw new InvalidObjectException("Negative size: " + size);
        map = new LinkedHashMap<>(capacity(size));
        for (int i = 0; i < size; i++) {
            E element = cast(input.readObject()); map.putIfAbsent(element, element);
        }
    }

    /**
     * Calculate the initial {@link HashMap} capacity basing on the given size.
     *
     * @param size the given size
     * @return the initial {@link HashMap} capacity basing on the given size
     */
    protected static int capacity(int size) {
        return max((int) (size/.75f) + 1, 16);
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
