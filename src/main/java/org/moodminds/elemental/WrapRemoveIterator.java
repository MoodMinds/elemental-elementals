package org.moodminds.elemental;

import java.util.Iterator;
import java.util.ListIterator;
import java.util.function.Function;

import static java.util.function.Function.identity;

/**
 * Wrapping and converting {@link Iterator} implementation of the {@link Iterator}
 * interface that supports {@link Iterator#remove()} method.
 *
 * @param <S> the type of source elements
 * @param <V> the type of target elements
 * @param <I> the type of source wrapped {@link java.util.Iterator}
 */
public abstract class WrapRemoveIterator<S, V, I extends Iterator<S>> extends WrapIterator<S, V, I> {

    /**
     * Construct the object with the given {@link I} iterator.
     *
     * @param iterator the given {@link I} iterator to wrap
     */
    protected WrapRemoveIterator(I iterator) {
        super(iterator);
    }

    @Override public void remove() {
        iterator.remove(); }


    /**
     * Return wrapping {@link Iterator} instance of the given source {@link Iterator}.
     *
     * @param iterator the given source {@link Iterator}
     * @param <V> the type of target elements
     * @return wrapping {@link Iterator} instance of the given source {@link Iterator}
     * @throws NullPointerException if the given {@link Iterator} iterator is {@code null}
     */
    public static <V> Iterator<V> wrap(Iterator<V> iterator) {
        return iterator instanceof ListIterator ? WrapListIterator.wrap(iterator)
                : wrap(iterator, identity());
    }

    /**
     * Return wrapping converting {@link Iterator} instance of the given source {@link Iterator}
     * that supports {@link Iterator#remove()} operation and value mapper {@link Function}.
     *
     * @param iterator the given source {@link Iterator}
     * @param mapper the given value mapper {@link Function}
     * @param <S> the type of source elements
     * @param <V> the type of target elements
     * @return wrapping converting {@link Iterator} instance of the given source {@link Iterator}
     * @throws NullPointerException if the given {@link Iterator} iterator or {@link Function} mapper is {@code null}
     */
    public static <S, V> Iterator<V> wrap(Iterator<S> iterator, Function<? super S, ? extends V> mapper) {
        return new WrapRemoveIterator<S, V, Iterator<S>>(iterator) {
            @Override protected V getTarget(S source) {
                return mapper.apply(source); }
        };
    }
}
