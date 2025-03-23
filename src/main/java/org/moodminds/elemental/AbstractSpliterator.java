package org.moodminds.elemental;

import java.util.Spliterator;

import static java.lang.Long.MAX_VALUE;

/**
 * A template implementation of a {@link Spliterator} that provides
 * minimal information about the data source. By default, this spliterator
 * cannot be parallelized, as {@link #trySplit()} return {@code null}.
 *
 * @param <E> the type of elements
 */
public abstract class AbstractSpliterator<E> implements Spliterator<E> {

    /**
     * {@inheritDoc}
     *
     * @return {@inheritDoc}
     */
    @Override
    public Spliterator<E> trySplit() {
        return null;
    }

    /**
     * {@inheritDoc}
     *
     * @return {@inheritDoc}
     */
    @Override
    public long estimateSize() {
        return MAX_VALUE;
    }

    /**
     * {@inheritDoc}
     *
     * @return {@inheritDoc}
     */
    @Override
    public int characteristics() {
        return 0;
    }
}
