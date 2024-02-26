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
public abstract class WrapSpliterator<S, V, W extends Spliterator<S>> implements Spliterator<V> {

    /**
     * Wrapping {@link W spliterator} holder field.
     */
    protected final W spliterator;

    /**
     * Construct the object with the given {@link W} spliterator.
     *
     * @param spliterator the given {@link W} spliterator to wrap
     */
    protected WrapSpliterator(W spliterator) {
        this.spliterator = requireNonNull(spliterator);
    }

    @Override public boolean tryAdvance(Consumer<? super V> action) {
        return spliterator.tryAdvance(consumer(action)); }
    @Override public void forEachRemaining(Consumer<? super V> action) {
        spliterator.forEachRemaining(consumer(action)); }
    @Override public Spliterator<V> trySplit() {
        return ofNullable(spliterator.trySplit()).map(this::spliterator).orElse(null); }
    @Override public long estimateSize() {
        return spliterator.estimateSize(); }
    @Override public long getExactSizeIfKnown() {
        return spliterator.getExactSizeIfKnown(); }
    @Override public int characteristics() {
        return characteristics(spliterator.characteristics()); }
    @Override public boolean hasCharacteristics(int characteristics) {
        return spliterator.hasCharacteristics(characteristics); }
    @Override public Comparator<? super V> getComparator() {
        return ofNullable(spliterator.getComparator()).<Comparator<V>>map(comparator -> (s1, s2) ->
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
     * @param spliterator the given source {@link Spliterator}
     * @param <V>     the type of target elements
     * @return wrapping converting {@link Spliterator} instance of the given source {@link Spliterator}
     * @throws NullPointerException if the given {@link Spliterator} is {@code null}
     */
    public static <V> Spliterator<V> wrap(Spliterator<V> spliterator) {
        return wrap(spliterator, ch -> ch);
    }

    /**
     * Return wrapping converting {@link Spliterator} instance of the given source {@link Spliterator}
     * and characteristics mapper {@link IntToLongFunction}.
     *
     * @param spliterator               the given source {@link Spliterator}
     * @param characteristicsMapper the given characteristics mapper {@link IntToLongFunction}
     * @param <V>                   the type of target elements
     * @return wrapping converting {@link Spliterator} instance of the given source {@link Spliterator}
     * @throws NullPointerException if the given {@link Spliterator} or characteristics mapper {@link IntToLongFunction} is {@code null}
     */
    public static <V> Spliterator<V> wrap(Spliterator<V> spliterator, IntToLongFunction characteristicsMapper) {
        return wrap(spliterator, identity(), identity(), characteristicsMapper);
    }

    /**
     * Return wrapping converting {@link Spliterator} instance of the given source {@link Spliterator}
     * and value mapping {@link Function}s.
     *
     * @param <S>          the type of source elements
     * @param <V>          the type of target elements
     * @param spliterator      the given source {@link Spliterator}
     * @param sourceMapper the given source value mapper {@link Function}
     * @param targetMapper the given target value mapper {@link Function}
     * @return wrapping converting {@link Spliterator} instance of the given source {@link Spliterator}
     * @throws NullPointerException if the given {@link Spliterator} or any of the mapping {@link Function}s is {@code null}
     */
    public static <S, V> Spliterator<V> wrap(Spliterator<S> spliterator, Function<? super V, ? extends S> sourceMapper, Function<? super S, ? extends V> targetMapper) {
        return wrap(spliterator, sourceMapper, targetMapper, ch -> ch);
    }

    /**
     * Return wrapping converting {@link Spliterator} instance of the given source {@link Spliterator},
     * value mapping {@link Function}s and characteristics mapper {@link IntToLongFunction}.
     *
     * @param <S>                   the type of source elements
     * @param <V>                   the type of target elements
     * @param spliterator               the given source {@link Spliterator}
     * @param sourceMapper          the given source value mapper {@link Function}
     * @param targetMapper          the given target value mapper {@link Function}
     * @param characteristicsMapper the given characteristics mapper {@link IntToLongFunction}
     * @return wrapping converting {@link Spliterator} instance of the given source {@link Spliterator}
     * @throws NullPointerException if the given {@link Spliterator} or any of the mapping {@link Function}s
     *                              or characteristics mapper {@link IntToLongFunction} is {@code null}
     */
    public static <S, V> Spliterator<V> wrap(Spliterator<S> spliterator, Function<? super V, ? extends S> sourceMapper, Function<? super S, ? extends V> targetMapper,
                                             IntToLongFunction characteristicsMapper) {
        requireNonNull(targetMapper); requireNonNull(sourceMapper); requireNonNull(characteristicsMapper);
        return new WrapSpliterator<S, V, Spliterator<S>>(spliterator) {
            @Override protected S getSource(V target) {
                return sourceMapper.apply(target); }
            @Override protected V getTarget(S source) {
                return targetMapper.apply(source); }
            @Override protected Spliterator<V> spliterator(Spliterator<S> spliterator) {
                return wrap(this.spliterator, sourceMapper, targetMapper, characteristicsMapper); }
            @Override protected int characteristics(int characteristics) {
                return (int) characteristicsMapper.applyAsLong(characteristics); }
        };
    }
}
