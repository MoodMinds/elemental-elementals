package org.moodminds.elemental;

import java.util.Iterator;

/**
 * A single element {@link Iterator}.
 *
 * @param <E> the type of element value
 */
public class SingleIterator<E> extends AbstractIterator<E> {

    /**
     * The next element holder field.
     */
    protected E value;

    /**
     * The hasNext flag holder field.
     */
    protected boolean hasNext = true;

    /**
     * Construct the object by the given value.
     *
     * @param value the given value
     */
    public SingleIterator(E value) {
        this.value = value;
    }

    /**
     * Construct the object by the given value and the {@link Runnable} removal operation.
     *
     * @param value the given value
     * @param removal the given {@link Runnable} removal operation
     */
    public SingleIterator(E value, Runnable removal) {
        super(removal); this.value = value;
    }

    /**
     * {@inheritDoc}
     *
     * @return {@inheritDoc}
     */
    @Override
    protected boolean hasPreviousElement() {
        return !hasNext;
    }

    /**
     * {@inheritDoc}
     *
     * @return {@inheritDoc}
     */
    @Override
    protected E previousElement() {
        hasNext = true; return value;
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
        hasNext = false; return value;
    }

    /**
     * Return a single element {@link Iterator} by the given value.
     *
     * @param value the given next value
     * @return a single element {@link Iterator} by the given value
     * @param <V> the type of element value
     */
    public static <V> Iterator<V> iterator(V value) {
        return new SingleIterator<>(value);
    }

    /**
     * Return a single element {@link Iterator} by the given value and the {@link Runnable} removal operation.
     *
     * @param value the given next value
     * @param removal the given {@link Runnable} removal operation
     * @return a single element {@link Iterator} by the given value and the {@link Runnable} removal operation
     * @param <V> the type of element value
     */
    public static <V> Iterator<V> iterator(V value, Runnable removal) {
        return new SingleIterator<>(value, removal);
    }
}
