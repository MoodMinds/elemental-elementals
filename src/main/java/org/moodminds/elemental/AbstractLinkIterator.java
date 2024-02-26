package org.moodminds.elemental;

import java.util.Iterator;

/**
 * This class provides a template implementation of an {@link Iterator}
 * for a source that is structured as a series of linked value nodes.
 *
 * @param <E> the type of elements
 * @param <L> the type of linking node
 */
public abstract class AbstractLinkIterator<E, L> extends AbstractIterator<E> {

    /**
     * Next and current linking node holding fields.
     */
    protected L next, curr;

    /**
     * Construct the object with the given next {@link L} linking node.
     *
     * @param next the given next {@link L} linking node
     */
    protected AbstractLinkIterator(L next) {
        this.next = next;
    }

    /**
     * {@inheritDoc}
     *
     * @return {@inheritDoc}
     */
    @Override
    public boolean hasNext() {
        return next != null;
    }

    /**
     * {@inheritDoc}
     *
     * @return {@inheritDoc}
     */
    @Override
    protected E nextElement() {
        curr = next; next = next(curr); return value(curr);
    }

    /**
     * Return next linking node for the given {@link L} one.
     *
     * @param link the given {@link L} linking node
     * @return next linking node for the given {@link L} one
     */
    protected abstract L next(L link);

    /**
     * Return the given {@link L} linking node value.
     *
     * @param link the given {@link L} linking node
     * @return the given {@link L} linking node value
     */
    protected abstract E value(L link);
}
