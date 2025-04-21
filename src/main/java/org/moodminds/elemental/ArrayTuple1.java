package org.moodminds.elemental;

/**
 * A 1-value width {@link ArrayTuple}.
 *
 * @param <V> the type of the value
 */
public class ArrayTuple1<V> extends ArrayTuple implements Tuple1<V> {

    private static final long serialVersionUID = 7859600852015223301L;

    /**
     * Construct the object with specified value.
     *
     * @param value the specified value
     */
    public ArrayTuple1(V value) {
        super(value);
    }

    /**
     * Construct the object with specified {@link Tuple1}.
     *
     * @param tuple the specified {@link Tuple1}.
     */
    public ArrayTuple1(Tuple1<V> tuple) {
        super(tuple);
    }

    /**
     * Construct the object with specified {@link ArrayTuple1}.
     *
     * @param tuple the specified {@link ArrayTuple1}.
     */
    public ArrayTuple1(ArrayTuple1<V> tuple) {
        super(tuple);
    }


    /**
     * Return 1-width {@link ArrayTuple1} of the specified value.
     *
     * @param value the specified value
     * @return 1-width {@link ArrayTuple1} of the specified value
     */
    public static <V> ArrayTuple1<V> tuple(V value) {
        return new ArrayTuple1<>(value);
    }
}
