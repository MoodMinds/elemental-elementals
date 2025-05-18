package org.moodminds.elemental;

import java.io.Serializable;

import static java.lang.String.format;
import static java.util.stream.IntStream.range;
import static org.moodminds.sneaky.Cast.cast;

/**
 * An array implementation of the {@link Tuple} as a distinct structure.
 */
public class ArrayTuple extends AbstractTuple implements Tuple, Serializable {

    private static final long serialVersionUID = -9192546557351540173L;

    /**
     * Field to hold the elements.
     */
    protected final Object[] data;

    /**
     * Construct the object with the specified values array.
     *
     * @param data the specified values array.
     */
    public ArrayTuple(Object... data) {
        this.data = data;
    }

    /**
     * Construct the object with the specified {@link Tuple}.
     *
     * @param tuple the specified {@link Tuple}.
     */
    public ArrayTuple(Tuple tuple) {
        this(range(0, tuple.width()).mapToObj(tuple::get).toArray());
    }

    /**
     * Construct the object with the specified {@link ArrayTuple}.
     *
     * @param tuple the specified {@link ArrayTuple}.
     */
    public ArrayTuple(ArrayTuple tuple) {
        this(tuple.data.clone());
    }

    /**
     * {@inheritDoc}
     *
     * @param index {@inheritDoc}
     * @return {@inheritDoc}
     * @throws IndexOutOfBoundsException if the index is out of range
     */
    @Override
    public <R> R get(int index) {
        if (index < 0 || index >= data.length)
            throw new IndexOutOfBoundsException(format("Index is out of bounds: %d.", index));
        return cast(data[index]);
    }

    /**
     * {@inheritDoc}
     *
     * @return {@inheritDoc}
     */
    @Override
    public int width() {
        return data.length;
    }


    /**
     * Return n-width {@link ArrayTuple} of the specified values.
     *
     * @param values the specified values
     * @return n-width {@link ArrayTuple} of the specified values
     */
    public static ArrayTuple tuple(Object... values) {
        return new ArrayTuple(values);
    }
}
