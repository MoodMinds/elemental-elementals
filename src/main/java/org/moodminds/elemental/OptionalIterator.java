package org.moodminds.elemental;

import java.util.Iterator;
import java.util.function.Supplier;

/**
 * An optional element {@link Iterator}.
 *
 * @param <E> the type of element value
 */
public class OptionalIterator<E> extends AbstractIterator<E> {

    /**
     * The next element {@link Supplier} field.
     */
    protected Supplier<? extends E> supplier;

    /**
     * The present and hasNext flags holder fields.
     */
    protected boolean present, hasNext;

    /**
     * Construct the object by the given optional value and presence flag.
     *
     * @param value the given next value
     * @param present the given value presence flag
     */
    public OptionalIterator(E value, boolean present) {
        this(() -> value, present, null);
    }

    /**
     * Construct the object by the given optional value {@link Supplier} and presence flag.
     *
     * @param supplier the given next value {@link Supplier}
     * @param present the given value presence flag
     */
    public OptionalIterator(Supplier<? extends E> supplier, boolean present) {
        this(supplier, present, null);
    }

    /**
     * Construct the object by the given optional value, presence flag
     * and the {@link Runnable} removal operation.
     *
     * @param value the given next value
     * @param present the given value presence flag
     * @param removal the given {@link Runnable} removal operation
     */
    public OptionalIterator(E value, boolean present, Runnable removal) {
        this(() -> value, present, removal);
    }

    /**
     * Construct the object by the given optional value {@link Supplier}, presence flag
     * and the {@link Runnable} removal operation.
     *
     * @param supplier the given next value {@link Supplier}
     * @param present the given value presence flag
     * @param removal the given {@link Runnable} removal operation
     */
    public OptionalIterator(Supplier<? extends E> supplier, boolean present, Runnable removal) {
        super(removal); this.supplier = supplier; hasNext = this.present = present;
    }

    /**
     * {@inheritDoc}
     *
     * @return {@inheritDoc}
     */
    @Override
    protected boolean hasPreviousElement() {
        return present && !hasNext;
    }

    /**
     * {@inheritDoc}
     *
     * @return {@inheritDoc}
     */
    @Override
    protected E previousElement() {
        hasNext = present; return supplier.get();
    }

    /**
     * {@inheritDoc}
     *
     * @return {@inheritDoc}
     */
    @Override
    public boolean hasNextElement() {
        return hasNext;
    }

    /**
     * {@inheritDoc}
     *
     * @return {@inheritDoc}
     */
    @Override
    protected E nextElement() {
        hasNext = false; return supplier.get();
    }


    /**
     * Return an optional element {@link Iterator} by the given optional value and presence flag.
     *
     * @param value the given next value
     * @param present the given value presence flag
     * @return an optional element {@link Iterator} by the given optional value and presence flag
     * @param <V> the type of element value
     */
    public static <V> Iterator<V> iterator(V value, boolean present) {
        return new OptionalIterator<>(value, present);
    }

    /**
     * Return an optional element {@link Iterator} by the given optional value {@link Supplier} and presence flag.
     *
     * @param supplier the given next value {@link Supplier}
     * @param present the given value presence flag
     * @return an optional element {@link Iterator} by the given optional value {@link Supplier} and presence flag
     * @param <V> the type of element value
     */
    public static <V> Iterator<V> iterator(Supplier<? extends V> supplier, boolean present) {
        return new OptionalIterator<>(supplier, present);
    }

    /**
     * Return an optional element {@link Iterator} by the given optional value, presence flag
     * and the {@link Runnable} removal operation.
     *
     * @param value the given next value
     * @param present the given value presence flag
     * @param removal the given {@link Runnable} removal operation
     * @return an optional element {@link Iterator} by the given optional value, presence flag
     * and the {@link Runnable} removal operation
     * @param <V> the type of element value
     */
    public static <V> Iterator<V> iterator(V value, boolean present, Runnable removal) {
        return new OptionalIterator<>(value, present, removal);
    }

    /**
     * Return an optional element {@link Iterator} by the given optional value {@link Supplier}, presence flag
     * and the {@link Runnable} removal operation.
     *
     * @param supplier the given next value {@link Supplier}
     * @param present the given value presence flag
     * @param removal the given {@link Runnable} removal operation
     * @return an optional element {@link Iterator} by the given optional value {@link Supplier}, presence flag
     * and the {@link Runnable} removal operation
     * @param <V> the type of element value
     */
    public static <V> Iterator<V> iterator(Supplier<? extends V> supplier, boolean present, Runnable removal) {
        return new OptionalIterator<>(supplier, present, removal);
    }
}
