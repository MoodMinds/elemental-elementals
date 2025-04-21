package org.moodminds.elemental;

/**
 * A 5-values width {@link ArrayTuple}.
 *
 * @param <V1> the type of value 1
 * @param <V2> the type of value 2
 * @param <V3> the type of value 3
 * @param <V4> the type of value 4
 * @param <V5> the type of value 5
 */
public class ArrayTuple5<V1, V2, V3, V4, V5> extends ArrayTuple
        implements Tuple5<V1, V2, V3, V4, V5> {

    private static final long serialVersionUID = -369724762379074849L;

    /**
     * Construct the object with specified values.
     *
     * @param value1 the specified value 1
     * @param value2 the specified value 2
     * @param value3 the specified value 3
     * @param value4 the specified value 4
     * @param value5 the specified value 5
     */
    public ArrayTuple5(V1 value1, V2 value2, V3 value3, V4 value4, V5 value5) {
        super(value1, value2, value3, value4, value5);
    }

    /**
     * Construct the object with specified {@link Tuple5}.
     *
     * @param tuple the specified {@link Tuple5}.
     */
    public ArrayTuple5(Tuple5<V1, V2, V3, V4, V5> tuple) {
        super(tuple);
    }

    /**
     * Construct the object with specified {@link ArrayTuple5}.
     *
     * @param tuple the specified {@link ArrayTuple5}.
     */
    public ArrayTuple5(ArrayTuple5<V1, V2, V3, V4, V5> tuple) {
        super(tuple);
    }


    /**
     * Return 5-width {@link ArrayTuple5} of the specified values.
     *
     * @param value1 the specified value 1
     * @param value2 the specified value 2
     * @param value3 the specified value 3
     * @param value4 the specified value 4
     * @param value5 the specified value 5
     * @return 5-width {@link ArrayTuple5} of the specified values
     */
    public static <V1, V2, V3, V4, V5> ArrayTuple5<V1, V2, V3, V4, V5> tuple(V1 value1, V2 value2, V3 value3, V4 value4, V5 value5) {
        return new ArrayTuple5<>(value1, value2, value3, value4, value5);
    }
}
