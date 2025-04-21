package org.moodminds.elemental;

import java.util.Iterator;

/**
 * A template implementation of the {@link TailedSequence} interface.
 *
 * @param <E> the type of elements
 */
public abstract class AbstractTailedSequence<E> extends AbstractSequence<E>
        implements TailedSequence<E> {

    /**
     * {@inheritDoc}
     *
     * @param o {@inheritDoc}
     * @return an {@inheritDoc}
     * @throws ClassCastException {@inheritDoc}
     * @throws NullPointerException {@inheritDoc}
     */
    @Override
    public Iterator<E> getAllDescending(Object o) {
        return iterator(descendingIterator(), o);
    }
}
