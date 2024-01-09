package org.moodminds.elemental;

import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * Template implementation of the {@link Iterator} interface.
 *
 * @param <E> the type of elements
 */
public abstract class AbstractIterator<E> implements Iterator<E> {

    /**
     * Marker for the current element state.
     */
    private boolean next;

    /**
     * The remove {@link Runnable} operation.
     */
    private final Runnable removal;

    /**
     * Construct the object. Initialize removal operation.
     */
    public AbstractIterator() {
        Runnable removal; this.removal = (removal = removal()) != null ? () -> {
            if (!next)
                throw new IllegalStateException();
            removal.run(); next = false;
        } : () -> { throw new UnsupportedOperationException("remove"); };
    }

    /**
     * {@inheritDoc}
     *
     * @return {@inheritDoc}
     * @throws NoSuchElementException {@inheritDoc}
     */
    @Override
    public E next() {
        if (!hasNext())
            throw new NoSuchElementException();
        E next = element(); this.next = true; return next;
    }

    /**
     * {@inheritDoc}
     *
     * @throws IllegalStateException {@inheritDoc}
     * @throws UnsupportedOperationException {@inheritDoc}
     */
    @Override
    public final void remove() {
        removal.run();
    }

    /**
     * Returns the next element.
     *
     * @return the next element
     * @throws NoSuchElementException if the iteration has no more elements
     */
    protected abstract E element();

    /**
     * Return the {@link Runnable} current element removal operation.
     *
     * @return the {@link Runnable} current element removal operation
     */
    protected Runnable removal() {
        return null;
    }
}
