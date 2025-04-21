package org.moodminds.elemental;

import java.io.InvalidObjectException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

import static java.lang.Math.max;
import static org.moodminds.elemental.Producer.producer;
import static org.moodminds.sneaky.Cast.cast;

/**
 * A {@link HashMap}-powered unique-values implementation of the {@link Container} interface.
 *
 * @param <E> the element type
 */
public class HashUnitainer<E> extends AbstractMapUnitainer<E, Map<E, E>> {

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
        map = new HashMap<>(capacity(size));
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
