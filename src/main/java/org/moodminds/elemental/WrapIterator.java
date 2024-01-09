package org.moodminds.elemental;

import java.util.Iterator;
import java.util.function.Consumer;
import java.util.function.Function;

import static java.util.Objects.requireNonNull;
import static java.util.Optional.ofNullable;
import static java.util.function.Function.identity;

/**
 * Wrapping and converting {@link Iterator} implementation of the {@link Iterator} interface.
 *
 * @param <S> the type of source elements
 * @param <V> the type of target elements
 * @param <I> the type of source wrapped {@link Iterator}
 */
abstract class WrapIterator<S, V, I extends Iterator<S>> implements Iterator<V> {

    protected final I wrapped;

    /**
     * Construct the object with the given {@link I} iterator.
     *
     * @param wrapped the given {@link I} iterator to wrap
     */
    protected WrapIterator(I wrapped) {
        this.wrapped = requireNonNull(wrapped);
    }

    @Override public boolean hasNext() {
        return wrapped.hasNext(); }
    @Override public V next() {
        return getTarget(wrapped.next()); }
    @Override public void forEachRemaining(Consumer<? super V> action) {
        wrapped.forEachRemaining(ofNullable(action).<Consumer<S>>map(consumer ->
                        s -> consumer.accept(getTarget(s)))
                .orElse(null)); }

    protected abstract V getTarget(S source);


    /**
     * Return wrapping converting {@link Iterator} instance of the given source {@link Iterator}.
     *
     * @param wrapped the given source {@link Iterator}
     * @param <V> the type of target elements
     * @return wrapping converting {@link Iterator} instance of the given source {@link Iterator}
     * @throws NullPointerException if the given {@link Iterator} iterator is {@code null}
     */
    static <V> Iterator<V> wrap(Iterator<V> wrapped) {
        return wrap(wrapped, identity());
    }

    /**
     * Return wrapping converting {@link Iterator} instance of the given source {@link Iterator}
     * and value mapping {@link Function}.
     *
     * @param wrapped the given source {@link Iterator}
     * @param mapper the given value mapping {@link Function}
     * @param <S> the type of source elements
     * @param <V> the type of target elements
     * @return wrapping converting {@link Iterator} instance of the given source {@link Iterator}
     * @throws NullPointerException if the given {@link Iterator} iterator or mapping {@link Function} is {@code null}
     */
    static <S, V> Iterator<V> wrap(Iterator<S> wrapped, Function<? super S, ? extends V> mapper) {
        requireNonNull(mapper); return new WrapIterator<S, V, Iterator<S>>(wrapped) {
            @Override protected V getTarget(S source) {
                return mapper.apply(source); }
        };
    }
}
