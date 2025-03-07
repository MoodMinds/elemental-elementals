package org.moodminds.elemental;

import java.io.InvalidObjectException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.HashMap;

import static java.lang.Math.max;
import static org.moodminds.sneaky.Cast.cast;

/**
 * A template unique-values implementation of the {@link Set} interface,
 * which is powered by an internal {@link HashMap}.
 *
 * @param <E> the element type
 */
abstract class AbstractHashSet<E> extends AbstractMapSet<E, java.util.Map<E, E>> {

    private static final long serialVersionUID = 5868368367048927160L;

    /**
     * Construct the object with the given {@link HashMap map}
     * and sequential single-threaded {@link Producer} of elements.
     *
     * @param map the given {@link HashMap map}
     * @param elements the given sequential single-threaded {@link Producer} of elements
     */
    protected AbstractHashSet(HashMap<E, E> map, Producer<? extends E> elements) {
        super(map, elements);
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
        map = map(capacity(size));
        for (int i = 0; i < size; i++)
            add(cast(input.readObject()));
    }

    /**
     * Construct the {@link HashMap} map by the given initial capacity.
     *
     * @param initialCapacity the given initial capacity.
     * @return the {@link HashMap} map by the given initial capacity
     */
    protected abstract HashMap<E, E> map(int initialCapacity);

    /**
     * Calculate the initial {@link HashMap} capacity basing on the given size.
     *
     * @param size the given size
     * @return the initial {@link HashMap} capacity basing on the given size
     */
    protected static int capacity(int size) {
        return max((int) (size/.75f) + 1, 16);
    }
}
