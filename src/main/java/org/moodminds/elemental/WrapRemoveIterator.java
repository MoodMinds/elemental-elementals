package org.moodminds.elemental;

import java.util.Iterator;
import java.util.function.Function;

/**
 * Wrapping and converting {@link Iterator} implementation of the {@link Iterator}
 * interface that supports {@link Iterator#remove()} method.
 *
 * @param <S> the type of source elements
 * @param <V> the type of target elements
 * @param <I> the type of source wrapped {@link java.util.Iterator}
 */
abstract class WrapRemoveIterator<S, V, I extends Iterator<S>> extends WrapIterator<S, V, I> {

    /**
     * Construct the object with the given {@link I} iterator.
     *
     * @param wrapped the given {@link I} iterator to wrap
     */
    protected WrapRemoveIterator(I wrapped) {
        super(wrapped);
    }

    @Override public void remove() {
        wrapped.remove(); }


    /**
     * Return wrapping converting {@link Iterator} instance of the given source {@link Iterator}
     * that supports {@link Iterator#remove()} operation and value mapper {@link Function}.
     *
     * @param wrapped the given source {@link Iterator}
     * @param mapper the given value mapper {@link Function}
     * @param <S> the type of source elements
     * @param <V> the type of target elements
     * @return wrapping converting {@link Iterator} instance of the given source {@link Iterator}
     * @throws NullPointerException if the given {@link Iterator} iterator or {@link Function} mapper is {@code null}
     */
    static <S, V> Iterator<V> wrap(Iterator<S> wrapped, Function<? super S, ? extends V> mapper) {
        return new WrapRemoveIterator<S, V, Iterator<S>>(wrapped) {
            @Override protected V getTarget(S source) {
                return mapper.apply(source); }
        };
    }
}
