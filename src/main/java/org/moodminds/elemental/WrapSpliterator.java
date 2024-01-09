package org.moodminds.elemental;

import java.util.Comparator;
import java.util.Spliterator;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.IntToLongFunction;

import static java.util.Objects.requireNonNull;
import static java.util.Optional.ofNullable;
import static java.util.function.Function.identity;

/**
 * Wrapping and converting {@link Spliterator} implementation of the {@link Spliterator} interface.
 *
 * @param <S> the type of source elements
 * @param <V> the type of target elements
 * @param <W> the type of source wrapped {@link Spliterator}
 */
abstract class WrapSpliterator<S, V, W extends Spliterator<S>> implements Spliterator<V> {

    protected final W wrapped;

    /**
     * Construct the object with the given {@link W} spliterator.
     *
     * @param wrapped the given {@link W} spliterator to wrap
     */
    protected WrapSpliterator(W wrapped) {
        this.wrapped = requireNonNull(wrapped);
    }

    @Override public boolean tryAdvance(Consumer<? super V> action) {
        return wrapped.tryAdvance(consumer(action)); }
    @Override public void forEachRemaining(Consumer<? super V> action) {
        wrapped.forEachRemaining(consumer(action)); }
    @Override public Spliterator<V> trySplit() {
        return ofNullable(wrapped.trySplit()).map(this::spliterator).orElse(null); }
    @Override public long estimateSize() {
        return wrapped.estimateSize(); }
    @Override public long getExactSizeIfKnown() {
        return wrapped.getExactSizeIfKnown(); }
    @Override public int characteristics() {
        return characteristics(wrapped.characteristics()); }
    @Override public boolean hasCharacteristics(int characteristics) {
        return wrapped.hasCharacteristics(characteristics); }
    @Override public Comparator<? super V> getComparator() {
        return ofNullable(wrapped.getComparator()).<Comparator<V>>map(comparator -> (s1, s2) ->
                        comparator.compare(getSource(s1), getSource(s2)))
                .orElse(null); }

    protected abstract S getSource(V target);

    protected abstract V getTarget(S source);

    protected abstract Spliterator<V> spliterator(Spliterator<S> spliterator);

    protected abstract int characteristics(int characteristics);

    private Consumer<S> consumer(Consumer<? super V> action) {
        return ofNullable(action).<Consumer<S>>map(consumer -> s -> consumer.accept(getTarget(s))).orElse(null);
    }

    /**
     * Return wrapping converting {@link Spliterator} instance of the given source {@link Spliterator}.
     *
     * @param wrapped the given source {@link Spliterator}
     * @param <V>     the type of target elements
     * @return wrapping converting {@link Spliterator} instance of the given source {@link Spliterator}
     * @throws NullPointerException if the given {@link Spliterator} is {@code null}
     */
    static <V> Spliterator<V> wrap(Spliterator<V> wrapped) {
        return wrap(wrapped, ch -> ch);
    }

    /**
     * Return wrapping converting {@link Spliterator} instance of the given source {@link Spliterator}
     * and characteristics mapper {@link IntToLongFunction}.
     *
     * @param wrapped               the given source {@link Spliterator}
     * @param characteristicsMapper the given characteristics mapper {@link IntToLongFunction}
     * @param <V>                   the type of target elements
     * @return wrapping converting {@link Spliterator} instance of the given source {@link Spliterator}
     * @throws NullPointerException if the given {@link Spliterator} or characteristics mapper {@link IntToLongFunction} is {@code null}
     */
    static <V> Spliterator<V> wrap(Spliterator<V> wrapped, IntToLongFunction characteristicsMapper) {
        return wrap(wrapped, identity(), identity(), characteristicsMapper);
    }

    /**
     * Return wrapping converting {@link Spliterator} instance of the given source {@link Spliterator}
     * and value mapping {@link Function}s.
     *
     * @param <S>          the type of source elements
     * @param <V>          the type of target elements
     * @param wrapped      the given source {@link Spliterator}
     * @param sourceMapper the given source value mapper {@link Function}
     * @param targetMapper the given target value mapper {@link Function}
     * @return wrapping converting {@link Spliterator} instance of the given source {@link Spliterator}
     * @throws NullPointerException if the given {@link Spliterator} or any of the mapping {@link Function}s is {@code null}
     */
    static <S, V> Spliterator<V> wrap(Spliterator<S> wrapped, Function<? super V, ? extends S> sourceMapper, Function<? super S, ? extends V> targetMapper) {
        return wrap(wrapped, sourceMapper, targetMapper, ch -> ch);
    }

    /**
     * Return wrapping converting {@link Spliterator} instance of the given source {@link Spliterator},
     * value mapping {@link Function}s and characteristics mapper {@link IntToLongFunction}.
     *
     * @param <S>                   the type of source elements
     * @param <V>                   the type of target elements
     * @param wrapped               the given source {@link Spliterator}
     * @param sourceMapper          the given source value mapper {@link Function}
     * @param targetMapper          the given target value mapper {@link Function}
     * @param characteristicsMapper the given characteristics mapper {@link IntToLongFunction}
     * @return wrapping converting {@link Spliterator} instance of the given source {@link Spliterator}
     * @throws NullPointerException if the given {@link Spliterator} or any of the mapping {@link Function}s
     *                              or characteristics mapper {@link IntToLongFunction} is {@code null}
     */
    static <S, V> Spliterator<V> wrap(Spliterator<S> wrapped, Function<? super V, ? extends S> sourceMapper, Function<? super S, ? extends V> targetMapper,
                                      IntToLongFunction characteristicsMapper) {
        requireNonNull(targetMapper); requireNonNull(sourceMapper); requireNonNull(characteristicsMapper);
        return new WrapSpliterator<S, V, Spliterator<S>>(wrapped) {
            @Override protected S getSource(V target) {
                return sourceMapper.apply(target); }
            @Override protected V getTarget(S source) {
                return targetMapper.apply(source); }
            @Override protected Spliterator<V> spliterator(Spliterator<S> spliterator) {
                return wrap(wrapped, sourceMapper, targetMapper, characteristicsMapper); }
            @Override protected int characteristics(int characteristics) {
                return (int) characteristicsMapper.applyAsLong(characteristics); }
        };
    }
}
