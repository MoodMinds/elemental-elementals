package org.moodminds.elemental;

import java.util.Comparator;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.BinaryOperator;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Collector;
import java.util.stream.Stream;

import static java.util.Comparator.comparing;
import static java.util.Objects.requireNonNull;
import static java.util.function.Function.identity;
import static java.util.stream.Collector.Characteristics.CONCURRENT;

/**
 * A container object that may or may not contain a value, including {@code null} values.
 * Unlike {@link Optional}, this implementation explicitly distinguishes between:
 * <ul>
 *   <li>An empty container (no value present)</li>
 *   <li>A container with a {@code null} value (value present, but {@code null})</li>
 * </ul>
 *
 * <p>This is particularly useful when {@code null} is a valid value that needs to be preserved
 * and distinguished from the absence of a value entirely.
 *
 * @param <V> the type of value that may be contained
 */
public final class OptionalNullable<V> {

    private final V value;
    private final boolean present;

    /**
     * Construct an instance with the {@code null} value and presence flag.
     *
     * @param present {@code true} if a value is present, {@code false} otherwise
     */
    private OptionalNullable(boolean present) {
        this.value = null; this.present = present;
    }

    /**
     * Construct an instance with the specified value and presence flag.
     *
     * @param value the value to store (may be {@code null})
     * @param present {@code true} if a value is present, {@code false} otherwise
     */
    private OptionalNullable(V value, boolean present) {
        this.value = value; this.present = present;
    }

    /**
     * Return the value if present, otherwise throw {@code NoSuchElementException}.
     *
     * @return the value (may be {@code null} if that was the stored value)
     * @throws NoSuchElementException if no value is present
     */
    public V get() {
        if (isPresent()) return value;
        throw new NoSuchElementException("No value present");
    }

    /**
     * Return {@code true} if a value is present, {@code false} otherwise.
     *
     * @return {@code true} if a value is present, {@code false} otherwise
     */
    public boolean isPresent() {
        return present;
    }

    /**
     * Return {@code true} if no value is present, {@code false} otherwise.
     *
     * @return {@code true} if no value is present, {@code false} otherwise
     */
    public boolean isEmpty() {
        return !isPresent();
    }

    /**
     * If a value is present, perform the given action with the value, otherwise do nothing.
     *
     * @param action the action to be performed, if a value is present
     * @throws NullPointerException if value is present and the given action is {@code null}
     */
    public void ifPresent(Consumer<? super V> action) {
        if (isPresent()) action.accept(value);
    }

    /**
     * If a value is present, perform the given action with the value,
     * otherwise perform the given empty-based action.
     *
     * @param action the action to be performed, if a value is present
     * @param emptyAction the empty-based action to be performed, if no value is present
     * @throws NullPointerException if a value is present and the given action is {@code null},
     *         or no value is present and the given empty-based action is {@code null}
     */
    public void ifPresentOrElse(Consumer<? super V> action, Runnable emptyAction) {
        if (isEmpty())
            emptyAction.run();
        else action.accept(value);
    }

    /**
     * If a value is present and matches the given predicate, return this {@code OptionalNullable},
     * otherwise return an empty {@code OptionalNullable}.
     *
     * @param predicate the predicate to apply to a value, if present
     * @return this {@code OptionalNullable} if a value is present and matches the predicate,
     *         otherwise an empty {@code OptionalNullable}
     * @throws NullPointerException if the predicate is {@code null}
     */
    public OptionalNullable<V> filter(Predicate<? super V> predicate) {
        requireNonNull(predicate);
        if (isEmpty()) return this;
        else return predicate.test(value) ? this : empty();
    }

    /**
     * If a value is present, return an {@code OptionalNullable} describing the result
     * of applying the given mapping function to the value, otherwise return an empty
     * {@code OptionalNullable}.
     *
     * @param <U> the type of the value returned from the mapping function
     * @param mapper the mapping function to apply to a value, if present
     * @return an {@code OptionalNullable} describing the result of applying a mapping
     *         function to the value of this {@code OptionalNullable}, if a value is present,
     *         otherwise an empty {@code OptionalNullable}
     * @throws NullPointerException if the mapping function is {@code null}
     */
    public <U> OptionalNullable<U> map(Function<? super V, ? extends U> mapper) {
        requireNonNull(mapper);
        if (isEmpty()) return empty();
        else return nullable(mapper.apply(value));
    }

    /**
     * If a value is present, return the result of applying the given
     * {@code OptionalNullable}-bearing mapping function to the value,
     * otherwise return an empty {@code OptionalNullable}.
     *
     * @param <U> the type parameter to the {@code OptionalNullable} returned by the mapping function
     * @param mapper the mapping function to apply to a value, if present
     * @return the result of applying an {@code OptionalNullable}-bearing mapping function
     *         to the value of this {@code OptionalNullable}, if a value is present,
     *         otherwise an empty {@code OptionalNullable}
     * @throws NullPointerException if the mapping function is {@code null} or if it returns a {@code null} result
     */
    public <U> OptionalNullable<U> flatMap(Function<? super V, ? extends OptionalNullable<? extends U>> mapper) {
        requireNonNull(mapper);
        if (isEmpty()) return empty();
        else return requireNonNull(mapper.apply(value)).flatMap(OptionalNullable::nullable);
    }

    /**
     * If a value is present, return the result of applying the given
     * {@link Optional}-bearing mapping function to the value,
     * otherwise return an empty {@link Optional}.
     *
     * @param <U> the type parameter to the {@link Optional} returned by the mapping function
     * @param mapper the mapping function to apply to a value, if present
     * @return the result of applying an {@link Optional}-bearing mapping function
     *         to the value of this {@code OptionalNullable}, if a value is present,
     *         otherwise an empty {@link Optional}
     * @throws NullPointerException if the mapping function is {@code null} or if it returns a {@code null} result
     */
    public <U> Optional<U> flatMapOptional(Function<? super V, ? extends Optional<? extends U>> mapper) {
        requireNonNull(mapper);
        if (isEmpty()) return Optional.empty();
        else return requireNonNull(mapper.apply(value)).flatMap(Optional::of);
    }

    /**
     * If a value is present, return this {@code OptionalNullable},
     * otherwise return the {@code OptionalNullable} produced by the supplying function.
     *
     * @param supplier the supplying function that produces an {@code OptionalNullable} to be returned
     * @return this {@code OptionalNullable}, if a value is present, otherwise the
     *         {@code OptionalNullable} produced by the supplying function
     * @throws NullPointerException if the supplying function is {@code null} or produces a {@code null} result
     */
    public OptionalNullable<V> or(Supplier<? extends OptionalNullable<? extends V>> supplier) {
        requireNonNull(supplier);
        if (isPresent()) return this;
        else return requireNonNull(supplier.get()).map(identity());
    }

    /**
     * If a value is present, return an {@link Optional} containing the value,
     * otherwise return the {@link Optional} produced by the supplying function.
     *
     * @param supplier the supplying function that produces an {@link Optional} to be returned
     * @return an {@link Optional} containing the value if present, otherwise the
     *         {@link Optional} produced by the supplying function
     * @throws NullPointerException if the supplying function is {@code null} or produces a {@code null} result
     */
    public Optional<V> orOptional(Supplier<? extends Optional<? extends V>> supplier) {
        requireNonNull(supplier);
        if (isPresent()) return Optional.ofNullable(value);
        else return requireNonNull(supplier.get()).map(identity());
    }

    /**
     * If a value is present, return the value, otherwise return {@code other}.
     *
     * @param other the value to be returned, if no value is present (may be {@code null})
     * @return the value, if present, otherwise {@code other}
     */
    public V orElse(V other) {
        return isPresent() ? value : other;
    }

    /**
     * If a value is present, return the value, otherwise return the result
     * produced by the supplying function.
     *
     * @param supplier the supplying function that produces a value to be returned
     * @return the value, if present, otherwise the result produced by the supplying function
     * @throws NullPointerException if no value is present and the supplying function is {@code null}
     */
    public V orElseGet(Supplier<? extends V> supplier) {
        return isPresent() ? value : supplier.get();
    }

    /**
     * If a value is present, return the value, otherwise throw {@code NoSuchElementException}.
     *
     * @return the value, if present
     * @throws NoSuchElementException if no value is present
     */
    public V orElseThrow() {
        return get();
    }

    /**
     * If a value is present, return the value, otherwise throw an exception
     * produced by the exception supplying function.
     *
     * @param <X> type of the exception to be thrown
     * @param exceptionSupplier the supplying function that produces an exception to be thrown
     * @return the value, if present
     * @throws X if no value is present
     * @throws NullPointerException if no value is present and the exception supplying function is {@code null}
     */
    public <X extends Throwable> V orElseThrow(Supplier<? extends X> exceptionSupplier) throws X {
        if (isPresent()) return value;
        else throw exceptionSupplier.get();
    }

    /**
     * If a value is present, return a sequential {@link Stream} containing only that value,
     * otherwise return an empty {@link Stream}.
     *
     * @return the optional value as a {@link Stream}
     */
    public Stream<V> stream() {
        return isEmpty() ? Stream.empty() : Stream.of(value);
    }

    /**
     * Convert this {@code OptionalNullable} to an {@link Optional}.
     * <p>
     * If no value is present, return an empty {@code Optional}.
     * If a non-{@code null} value is present, returns an {@code Optional} containing that value.
     * If a {@code null} value is present, returns an empty {@code Optional} since
     * {@code Optional} cannot represent {@code null} values.
     *
     * @return an {@code Optional} representation of this {@code OptionalNullable}
     */
    public Optional<V> optional() {
        return Optional.ofNullable(value);
    }

    /**
     * Return the hash code of the value, if present, otherwise {@code 0} (zero).
     *
     * @return hash code value of the present value or {@code 0} if no value is present
     */
    @Override
    public int hashCode() {
        return present ? Objects.hashCode(value) : 0;
    }

    /**
     * Indicate whether some other object is "equal to" this {@code OptionalNullable}.
     * The other object is considered equal if:
     * <ul>
     * <li>it is also an {@code OptionalNullable} and;
     * <li>both instances have no value present or;
     * <li>the present values are "equal to" each other via {@code Objects.equals()}.
     * </ul>
     *
     * @param obj an object to be tested for equality
     * @return {@code true} if the other object is "equal to" this object otherwise {@code false}
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof OptionalNullable)) return false;
        OptionalNullable<?> other = (OptionalNullable<?>) obj;
        return present && other.present && Objects.equals(value, other.value);
    }

    /**
     * Return a non-empty string representation of this {@code OptionalNullable}.
     *
     * @return the string representation of this instance
     */
    @Override
    public String toString() {
        return isPresent() ? String.format("OptionalNullable[%s]", value) : "OptionalNullable.empty";
    }

    /**
     * Return an empty {@code OptionalNullable} instance. No value is present
     * for this {@code OptionalNullable}.
     *
     * @param <V> the type of the non-existent value
     * @return an empty {@code OptionalNullable}
     */
    public static <V> OptionalNullable<V> empty() {
        return new OptionalNullable<>(false);
    }

    /**
     * Return an {@code OptionalNullable} describing the {@code null} value.
     *
     * @param <V> the type of the value
     * @return an {@code OptionalNullable} with the value present
     */
    public static <V> OptionalNullable<V> nullable() {
        return new OptionalNullable<>(true);
    }

    /**
     * Return an {@code OptionalNullable} describing the given value.
     *
     * @param <V> the type of the value
     * @param value the value to describe; may be {@code null}
     * @return an {@code OptionalNullable} with the value present
     */
    public static <V> OptionalNullable<V> nullable(V value) {
        return new OptionalNullable<>(value, true);
    }

    /**
     * Return an {@code OptionalNullable} describing an arbitrary element from the given stream,
     * or an empty {@code OptionalNullable} if the stream is empty.
     * <p>
     * Unlike {@link Stream#findAny()}, this method preserves {@code null} values that may
     * be present in the stream, distinguishing between an empty stream and a stream
     * containing a {@code null} element.
     *
     * @param <V> the type of stream elements
     * @param stream the stream from which to find an element
     * @return an {@code OptionalNullable} describing an arbitrary element from the stream,
     *         or an empty {@code OptionalNullable} if the stream is empty
     * @throws NullPointerException if the stream is {@code null}
     */
    public static <V> OptionalNullable<V> findAny(Stream<? extends V> stream) {
        return stream.<OptionalNullable<V>>map(OptionalNullable::nullable).findAny()
                .orElseGet(OptionalNullable::empty);
    }

    /**
     * Return an {@code OptionalNullable} describing the first element of the given stream,
     * or an empty {@code OptionalNullable} if the stream is empty.
     * <p>
     * Unlike {@link Stream#findFirst()}, this method preserves {@code null} values that may
     * be the first element in the stream, distinguishing between an empty stream and a stream
     * whose first element is {@code null}.
     *
     * @param <V> the type of stream elements
     * @param stream the stream from which to find the first element
     * @return an {@code OptionalNullable} describing the first element of the stream,
     *         or an empty {@code OptionalNullable} if the stream is empty
     * @throws NullPointerException if the stream is {@code null}
     */
    public static <V> OptionalNullable<V> findFirst(Stream<? extends V> stream) {
        return stream.<OptionalNullable<V>>map(OptionalNullable::nullable).findFirst()
                .orElseGet(OptionalNullable::empty);
    }

    /**
     * Return an {@code OptionalNullable} describing the minimum element of the given stream
     * according to the provided {@link Comparator}, or an empty {@code OptionalNullable}
     * if the stream is empty.
     * <p>
     * Unlike {@link Stream#min(Comparator)}, this method can return an {@code OptionalNullable}
     * containing a {@code null} value if {@code null} is determined to be the minimum element
     * according to the comparator's null-handling strategy.
     *
     * @param <V> the type of stream elements
     * @param stream the stream from which to find the minimum
     * @param comparator a {@link Comparator} to compare elements of the stream
     * @return an {@code OptionalNullable} describing the minimum element of the stream,
     *         or an empty {@code OptionalNullable} if the stream is empty
     * @throws NullPointerException if the stream or comparator is {@code null}
     */
    public static <V> OptionalNullable<V> min(Stream<? extends V> stream, Comparator<? super V> comparator) {
        return stream.<OptionalNullable<V>>map(OptionalNullable::nullable).min(comparing(OptionalNullable::get, comparator))
                .orElseGet(OptionalNullable::empty);
    }

    /**
     * Return an {@code OptionalNullable} describing the maximum element of the given stream
     * according to the provided {@link Comparator}, or an empty {@code OptionalNullable}
     * if the stream is empty.
     * <p>
     * Unlike {@link Stream#max(Comparator)}, this method can return an {@code OptionalNullable}
     * containing a {@code null} value if {@code null} is determined to be the maximum element
     * according to the comparator's null-handling strategy.
     *
     * @param <V> the type of stream elements
     * @param stream the stream from which to find the maximum
     * @param comparator a {@link Comparator} to compare elements of the stream
     * @return an {@code OptionalNullable} describing the maximum element of the stream,
     *         or an empty {@code OptionalNullable} if the stream is empty
     * @throws NullPointerException if the stream or comparator is {@code null}
     */
    public static <V> OptionalNullable<V> max(Stream<? extends V> stream, Comparator<? super V> comparator) {
        return stream.<OptionalNullable<V>>map(OptionalNullable::nullable).max(comparing(OptionalNullable::get, comparator))
                .orElseGet(OptionalNullable::empty);
    }

    /**
     * Perform a reduction on the elements of the given stream, using the provided
     * binary operator, and return an {@code OptionalNullable} describing the reduced value,
     * or an empty {@code OptionalNullable} if the stream is empty.
     * <p>
     * Unlike {@link Stream#reduce(BinaryOperator)}, this method can return an
     * {@code OptionalNullable} containing a {@code null} value if the reduction process
     * produces {@code null} as the result.
     *
     * @param <V> the type of stream elements
     * @param stream the stream to reduce
     * @param accumulator an associative, non-interfering, stateless binary operator for
     *                   combining stream elements
     * @return an {@code OptionalNullable} describing the result of the reduction,
     *         or an empty {@code OptionalNullable} if the stream is empty
     * @throws NullPointerException if the stream or accumulator is {@code null}
     */
    public static <V> OptionalNullable<V> reduce(Stream<? extends V> stream, BiFunction<? super V, ? super V, ? extends V> accumulator) {
        return stream.<OptionalNullable<V>>map(OptionalNullable::nullable).reduce((opt1, opt2) ->
                        OptionalNullable.nullable(accumulator.apply(opt1.get(), opt2.get())))
                .orElseGet(OptionalNullable::empty);
    }

    /**
     * Perform a mutable reduction operation on the elements of the given stream using a
     * {@link Collector} that produces an {@link Optional} result, and return an
     * {@code OptionalNullable} that preserves the distinction between "no result found"
     * and "null result found".
     * <p>
     * This method enhances collectors that return {@code Optional<V>} by tracking whether
     * any elements were processed during the collection. This allows distinguishing between:
     * <ul>
     *   <li>An empty stream (no elements to process) → {@code OptionalNullable.empty()}</li>
     *   <li>A stream with elements but no result found → {@code OptionalNullable.nullable()}</li>
     *   <li>A stream with elements and a result found → {@code OptionalNullable.nullable(result)}</li>
     * </ul>
     *
     * @param <V> the type of input elements and the result
     * @param <A> the intermediate accumulation type of the {@code Collector}
     * @param stream the stream to collect from
     * @param collector a {@code Collector} that produces an {@code Optional} result
     * @return an {@code OptionalNullable} preserving the collection result and element presence information
     * @throws NullPointerException if the stream or collector is {@code null}
     */
    public static <V, A> OptionalNullable<V> collect(Stream<? extends V> stream, Collector<? super V, A, Optional<V>> collector) {
        return stream.collect(collector(collector));
    }

    /**
     * Return a {@code Collector} that adapts an existing {@code Optional}-producing collector
     * to produce {@code OptionalNullable} results with enhanced null-handling capabilities.
     * <p>
     * This method transforms a {@code Collector<T, A, Optional<T>>} into a
     * {@code Collector<T, ?, OptionalNullable<T>>} that preserves the distinction between:
     * <ul>
     *   <li>No elements processed (empty stream) → {@code OptionalNullable.empty()}</li>
     *   <li>Elements processed but collector found no result → {@code OptionalNullable.nullable()}</li>
     *   <li>Elements processed and collector found a result → {@code OptionalNullable.nullable(result)}</li>
     * </ul>
     *
     * <p>The returned collector maintains the same characteristics as the original collector,
     * including thread-safety behavior for parallel streams. For {@link Collector.Characteristics#CONCURRENT}
     * collectors, thread-safe presence tracking is automatically used.
     *
     * @param <V> the type of input elements to the collector
     * @param <A> the intermediate accumulation type of the original collector
     * @param collector the original collector that produces {@code Optional} results
     * @return a new {@code Collector} that produces {@code OptionalNullable} results with
     *         enhanced null-handling and element presence tracking
     * @throws NullPointerException if the collector is {@code null}
     */
    public static <V, A> Collector<V, ?, OptionalNullable<V>> collector(Collector<? super V, A, Optional<V>> collector) {

        abstract class Accumulation { final A accumulation;
            Accumulation(A accumulation) {
                this.accumulation = accumulation; }
            abstract Accumulation present();
            abstract boolean isPresent();
        }

        class VariableAccumulation extends Accumulation { boolean present;
            VariableAccumulation(A accumulation, boolean present) {
                super(accumulation); this.present = present; }
            @Override Accumulation present() { present = true; return this; }
            @Override public boolean isPresent() { return present; }
        }

        class VolatileAccumulation extends Accumulation { volatile boolean present;
            VolatileAccumulation(A accumulation, boolean present) {
                super(accumulation); this.present = present; }
            @Override Accumulation present() { present = true; return this; }
            @Override public boolean isPresent() { return present; }
        }

        return new Collector<V, Accumulation, OptionalNullable<V>>() {

            final BiFunction<A, Boolean, Accumulation> accumulationFactory = collector.characteristics().contains(CONCURRENT)
                    ? VolatileAccumulation::new : VariableAccumulation::new;

            @Override public Supplier<Accumulation> supplier() {
                Supplier<A> supplier = collector.supplier(); return () -> accumulationFactory.apply(supplier.get(), false); }

            @Override public BiConsumer<Accumulation, V> accumulator() {
                BiConsumer<A, ? super V> accumulator = collector.accumulator(); return (accumulation, v) ->
                        accumulator.accept(accumulation.present().accumulation, v); }

            @Override public BinaryOperator<Accumulation> combiner() {
                BinaryOperator<A> combiner = collector.combiner(); return (a1, a2) ->
                        accumulationFactory.apply(combiner.apply(a1.accumulation, a2.accumulation), a1.isPresent() || a2.isPresent()); }

            @Override public Function<Accumulation, OptionalNullable<V>> finisher() {
                Function<A, Optional<V>> finisher = collector.finisher(); return accumulation ->
                        finisher.apply(accumulation.accumulation).map(OptionalNullable::nullable)
                                .orElseGet(() -> new OptionalNullable<>(accumulation.isPresent())); }

            @Override public Set<Characteristics> characteristics() {
                return collector.characteristics(); }
        };
    }
}