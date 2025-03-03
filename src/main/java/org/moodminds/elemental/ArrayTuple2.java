package org.moodminds.elemental;

/**
 * A 2-values width {@link ArrayTuple}.
 *
 * @param <V1> the type of value 1
 * @param <V2> the type of value 2
 */
public class ArrayTuple2<V1, V2> extends ArrayTuple
        implements Tuple2<V1, V2> {

    private static final long serialVersionUID = 8119893385406259207L;

    /**
     * Construct the object with specified values.
     *
     * @param value1 the specified value 1
     * @param value2 the specified value 2
     */
    public ArrayTuple2(V1 value1, V2 value2) {
        super(value1, value2);
    }

    /**
     * Construct the object with specified {@link Tuple2}.
     *
     * @param tuple the specified {@link Tuple2}.
     */
    public ArrayTuple2(Tuple2<V1, V2> tuple) {
        super(tuple);
    }

    /**
     * Construct the object with specified {@link ArrayTuple2}.
     *
     * @param tuple the specified {@link ArrayTuple2}.
     */
    public ArrayTuple2(ArrayTuple2<V1, V2> tuple) {
        super(tuple);
    }


    /**
     * Return 2-width {@link ArrayTuple2} of the specified values.
     *
     * @param value1 the specified value 1
     * @param value2 the specified value 2
     * @return 2-width {@link ArrayTuple2} of the specified values
     */
    public static <V1, V2> ArrayTuple2<V1, V2> tuple(V1 value1, V2 value2) {
        return new ArrayTuple2<>(value1, value2);
    }
}
