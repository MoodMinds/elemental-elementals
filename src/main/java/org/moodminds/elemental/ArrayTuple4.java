package org.moodminds.elemental;

/**
 * A 4-values width {@link ArrayTuple}.
 *
 * @param <V1> the type of value 1
 * @param <V2> the type of value 2
 * @param <V3> the type of value 3
 * @param <V4> the type of value 4
 */
public class ArrayTuple4<V1, V2, V3, V4> extends ArrayTuple
        implements Tuple4<V1, V2, V3, V4> {

    private static final long serialVersionUID = 3220510641733623457L;

    /**
     * Construct the object with specified values.
     *
     * @param value1 the specified value 1
     * @param value2 the specified value 2
     * @param value3 the specified value 3
     * @param value4 the specified value 4
     */
    public ArrayTuple4(V1 value1, V2 value2, V3 value3, V4 value4) {
        super(value1, value2, value3, value4);
    }

    /**
     * Construct the object with specified {@link Tuple4}.
     *
     * @param tuple the specified {@link Tuple4}.
     */
    public ArrayTuple4(Tuple4<V1, V2, V3, V4> tuple) {
        super(tuple);
    }

    /**
     * Construct the object with specified {@link ArrayTuple4}.
     *
     * @param tuple the specified {@link ArrayTuple4}.
     */
    public ArrayTuple4(ArrayTuple4<V1, V2, V3, V4> tuple) {
        super(tuple);
    }


    /**
     * Return 4-width {@link ArrayTuple4} of the specified values.
     *
     * @param value1 the specified value 1
     * @param value2 the specified value 2
     * @param value3 the specified value 3
     * @param value4 the specified value 4
     * @return 4-width {@link ArrayTuple4} of the specified values
     */
    public static <V1, V2, V3, V4> ArrayTuple4<V1, V2, V3, V4> tuple(V1 value1, V2 value2, V3 value3, V4 value4) {
        return new ArrayTuple4<>(value1, value2, value3, value4);
    }
}
