package org.moodminds.elemental;

/**
 * A 3-values width {@link ArrayTuple}.
 *
 * @param <V1> the type of value 1
 * @param <V2> the type of value 2
 * @param <V3> the type of value 3
 */
public class ArrayTuple3<V1, V2, V3> extends ArrayTuple
        implements Tuple3<V1, V2, V3> {

    private static final long serialVersionUID = 7362828742017624373L;

    /**
     * Construct the object with specified values.
     *
     * @param value1 the specified value 1
     * @param value2 the specified value 2
     * @param value3 the specified value 3
     */
    public ArrayTuple3(V1 value1, V2 value2, V3 value3) {
        super(value1, value2, value3);
    }

    /**
     * Construct the object with specified {@link Tuple3}.
     *
     * @param tuple the specified {@link Tuple3}.
     */
    public ArrayTuple3(Tuple3<V1, V2, V3> tuple) {
        super(tuple);
    }

    /**
     * Construct the object with specified {@link ArrayTuple3}.
     *
     * @param tuple the specified {@link ArrayTuple3}.
     */
    public ArrayTuple3(ArrayTuple3<V1, V2, V3> tuple) {
        super(tuple);
    }


    /**
     * Return 3-width {@link ArrayTuple3} of the specified values.
     *
     * @param value1 the specified value 1
     * @param value2 the specified value 2
     * @param value3 the specified value 3
     * @return 3-width {@link ArrayTuple3} of the specified values
     */
    public static <V1, V2, V3> ArrayTuple3<V1, V2, V3> tuple(V1 value1, V2 value2, V3 value3) {
        return new ArrayTuple3<>(value1, value2, value3);
    }
}
