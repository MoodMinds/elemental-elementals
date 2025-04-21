package org.moodminds.elemental;

/**
 * A 6-values width {@link ArrayTuple}.
 *
 * @param <V1> the type of value 1
 * @param <V2> the type of value 2
 * @param <V3> the type of value 3
 * @param <V4> the type of value 4
 * @param <V5> the type of value 5
 * @param <V6> the type of value 6
 */
public class ArrayTuple6<V1, V2, V3, V4, V5, V6> extends ArrayTuple
        implements Tuple6<V1, V2, V3, V4, V5, V6> {

    private static final long serialVersionUID = 8779828523348264544L;

    /**
     * Construct the object with specified values.
     *
     * @param value1 the specified value 1
     * @param value2 the specified value 2
     * @param value3 the specified value 3
     * @param value4 the specified value 4
     * @param value5 the specified value 5
     * @param value6 the specified value 6
     */
    public ArrayTuple6(V1 value1, V2 value2, V3 value3, V4 value4, V5 value5, V6 value6) {
        super(value1, value2, value3, value4, value5, value6);
    }

    /**
     * Construct the object with specified {@link Tuple6}.
     *
     * @param tuple the specified {@link Tuple6}.
     */
    public ArrayTuple6(Tuple6<V1, V2, V3, V4, V5, V6> tuple) {
        super(tuple);
    }

    /**
     * Construct the object with specified {@link ArrayTuple6}.
     *
     * @param tuple the specified {@link ArrayTuple6}.
     */
    public ArrayTuple6(ArrayTuple6<V1, V2, V3, V4, V5, V6> tuple) {
        super(tuple);
    }


    /**
     * Return 6-width {@link ArrayTuple6} of the specified values.
     *
     * @param value1 the specified value 1
     * @param value2 the specified value 2
     * @param value3 the specified value 3
     * @param value4 the specified value 4
     * @param value5 the specified value 5
     * @param value6 the specified value 6
     * @return 6-width {@link ArrayTuple6} of the specified values
     */
    public static <V1, V2, V3, V4, V5, V6> ArrayTuple6<V1, V2, V3, V4, V5, V6> tuple(V1 value1, V2 value2, V3 value3, V4 value4, V5 value5, V6 value6) {
        return new ArrayTuple6<>(value1, value2, value3, value4, value5, value6);
    }
}
