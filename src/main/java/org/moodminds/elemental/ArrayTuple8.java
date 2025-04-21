package org.moodminds.elemental;

/**
 * An 8-values width {@link ArrayTuple}.
 *
 * @param <V1> the type of value 1
 * @param <V2> the type of value 2
 * @param <V3> the type of value 3
 * @param <V4> the type of value 4
 * @param <V5> the type of value 5
 * @param <V6> the type of value 6
 * @param <V7> the type of value 7
 * @param <V8> the type of value 8
 */
public class ArrayTuple8<V1, V2, V3, V4, V5, V6, V7, V8> extends ArrayTuple
        implements Tuple8<V1, V2, V3, V4, V5, V6, V7, V8> {

    private static final long serialVersionUID = -7743720444893620127L;

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
     * @param value8 the specified value 8
     */
    public ArrayTuple8(V1 value1, V2 value2, V3 value3, V4 value4, V5 value5, V6 value6, V7 value7, V8 value8) {
        super(value1, value2, value3, value4, value5, value6, value7, value8);
    }


    /**
     * Construct the object with specified {@link Tuple8}.
     *
     * @param tuple the specified {@link Tuple8}.
     */
    public ArrayTuple8(Tuple8<V1, V2, V3, V4, V5, V6, V7, V8> tuple) {
        super(tuple);
    }

    /**
     * Construct the object with specified {@link ArrayTuple8}.
     *
     * @param tuple the specified {@link ArrayTuple8}.
     */
    public ArrayTuple8(ArrayTuple8<V1, V2, V3, V4, V5, V6, V7, V8> tuple) {
        super(tuple);
    }


    /**
     * Return 8-width {@link ArrayTuple8} of the specified values.
     *
     * @param value1 the specified value 1
     * @param value2 the specified value 2
     * @param value3 the specified value 3
     * @param value4 the specified value 4
     * @param value5 the specified value 5
     * @param value6 the specified value 6
     * @param value7 the specified value 7
     * @param value8 the specified value 8
     * @return 8-width {@link ArrayTuple8} of the specified values
     */
    public static <V1, V2, V3, V4, V5, V6, V7, V8> ArrayTuple8<V1, V2, V3, V4, V5, V6, V7, V8> tuple(V1 value1, V2 value2, V3 value3, V4 value4, V5 value5, V6 value6, V7 value7, V8 value8) {
        return new ArrayTuple8<>(value1, value2, value3, value4, value5, value6, value7, value8);
    }
}
