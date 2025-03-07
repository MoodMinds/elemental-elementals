package org.moodminds.elemental;

/**
 * A 7-values width {@link ArrayTuple}.
 *
 * @param <V1> the type of value 1
 * @param <V2> the type of value 2
 * @param <V3> the type of value 3
 * @param <V4> the type of value 4
 * @param <V5> the type of value 5
 * @param <V6> the type of value 6
 * @param <V7> the type of value 7
 */
public class ArrayTuple7<V1, V2, V3, V4, V5, V6, V7> extends ArrayTuple
        implements Tuple7<V1, V2, V3, V4, V5, V6, V7> {

    private static final long serialVersionUID = 5417107780603347000L;

    /**
     * Construct the object with specified values.
     *
     * @param value1 the specified value 1
     * @param value2 the specified value 2
     * @param value3 the specified value 3
     * @param value4 the specified value 4
     * @param value5 the specified value 5
     * @param value6 the specified value 6
     * @param value7 the specified value 7
     */
    public ArrayTuple7(V1 value1, V2 value2, V3 value3, V4 value4, V5 value5, V6 value6, V7 value7) {
        super(value1, value2, value3, value4, value5, value6, value7);
    }

    /**
     * Construct the object with specified {@link Tuple7}.
     *
     * @param tuple the specified {@link Tuple7}.
     */
    public ArrayTuple7(Tuple7<V1, V2, V3, V4, V5, V6, V7> tuple) {
        super(tuple);
    }

    /**
     * Construct the object with specified {@link ArrayTuple7}.
     *
     * @param tuple the specified {@link ArrayTuple7}.
     */
    public ArrayTuple7(ArrayTuple7<V1, V2, V3, V4, V5, V6, V7> tuple) {
        super(tuple);
    }


    /**
     * Return 7-width {@link ArrayTuple7} of the specified values.
     *
     * @param value1 the specified value 1
     * @param value2 the specified value 2
     * @param value3 the specified value 3
     * @param value4 the specified value 4
     * @param value5 the specified value 5
     * @param value6 the specified value 6
     * @param value7 the specified value 7
     * @return 7-width {@link ArrayTuple7} of the specified values
     */
    public static <V1, V2, V3, V4, V5, V6, V7> ArrayTuple7<V1, V2, V3, V4, V5, V6, V7> tuple(V1 value1, V2 value2, V3 value3, V4 value4, V5 value5, V6 value6, V7 value7) {
        return new ArrayTuple7<>(value1, value2, value3, value4, value5, value6, value7);
    }
}
