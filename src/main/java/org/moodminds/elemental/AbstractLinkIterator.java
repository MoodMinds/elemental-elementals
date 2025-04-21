package org.moodminds.elemental;

import java.util.Iterator;

/**
 * This class provides a template implementation of an {@link Iterator}
 * for a source that is structured as a series of linked value nodes.
 *
 * @param <E> the type of elements
 * @param <L> the type of link
 */
public abstract class AbstractLinkIterator<E, L> extends AbstractIterator<E> {

    /**
     * Previous and next link holding fields.
     */
    protected L previous, next;

    /**
     * Construct the object with the given previous {@link L} link.
     *
     * @param previous the given previous {@link L} link
     */
    protected AbstractLinkIterator(L previous) {
        this.previous = previous;
    }

    /**
     * Construct the object with the given previous {@link L} link and
     * {@link Runnable} removal operation.
     *
     * @param previous the given previous {@link L} link
     * @param removal the given {@link Runnable} removal operation
     */
    protected AbstractLinkIterator(L previous, Runnable removal) {
        super(removal); this.previous = previous;
    }

    /**
     * {@inheritDoc}
     *
     * @return {@inheritDoc}
     */
    @Override
    protected boolean hasPreviousElement() {
        return hasPreviousLink();
    }

    /**
     * {@inheritDoc}
     *
     * @return {@inheritDoc}
     */
    @Override
    protected E previousElement() {
        E element;
        if (next != null) {
            element = item(next); next = null;
        } else {
            element = item(previous); previous = previous(previous);
        } return element;
    }

    /**
     * {@inheritDoc}
     *
     * @return {@inheritDoc}
     */
    @Override
    protected boolean hasNextElement() {
        return hasNext(next != null ? next : previous);
    }

    /**
     * {@inheritDoc}
     *
     * @return {@inheritDoc}
     */
    @Override
    protected E nextElement() {
        return item(next = next(next != null ? (previous = next) : previous));
    }

    /**
     * Check if the current link has a previous link in the sequence.
     *
     * @return {@code true} if there is a previous link; {@code false} otherwise
     */
    protected boolean hasPreviousLink() {
        return previous != null;
    }

    /**
     * Return previous link for the given {@link L} one.
     *
     * @param link the given {@link L} link
     * @return previous link for the given {@link L} one
     */
    protected abstract L previous(L link);

    /**
     * Determine if the given link has a subsequent link in the sequence.
     *
     * @param link the link to check for a subsequent link
     * @return {@code true} if the given link has a next link; {@code false} otherwise
     */
    protected abstract boolean hasNext(L link);

    /**
     * Return next link node for the given {@link L} one.
     *
     * @param link the given {@link L} link
     * @return next link for the given {@link L} one
     */
    protected abstract L next(L link);

    /**
     * Return the given {@link L} linking node item value.
     *
     * @param link the given {@link L} linking node
     * @return the given {@link L} linking node item value
     */
    protected abstract E item(L link);
}
