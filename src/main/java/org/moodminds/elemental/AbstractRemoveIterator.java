package org.moodminds.elemental;

import java.util.Iterator;

/**
 * Template implementation of the {@link Iterator} interface with support of {@link #remove()} method.
 *
 * @param <E> the type of elements
 */
public abstract class AbstractRemoveIterator<E> extends AbstractIterator<E> {

    /**
     * {@inheritDoc}
     *
     * @return {@inheritDoc}
     */
    @Override
    protected final Runnable removal() {
        return this::removeElement;
    }

    /**
     * Remove the last element returned by this iterator.
     */
    protected abstract void removeElement();
}
