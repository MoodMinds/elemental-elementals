package org.moodminds.elemental;

import java.util.function.Consumer;

/**
 * This class provides a template implementation of the {@link TailedSequenceIterator} interface.
 *
 * @param <E> the type of elements
 */
public abstract class AbstractTailedSequenceIterator<E> extends AbstractSequenceIterator<E>
        implements TailedSequenceIterator<E> {

    /**
     * Construct the object with the given {@link Sequence} element index.
     *
     * @param index the given {@link Sequence} element index
     */
    protected AbstractTailedSequenceIterator(int index) {
        super(index);
    }

    /**
     * Construct the object with the given {@link Sequence} element index and
     * {@link Runnable} removal operation.
     *
     * @param index the given {@link Sequence} element index
     * @param removal the given {@link Runnable} removal operation
     */
    protected AbstractTailedSequenceIterator(int index, Runnable removal) {
        super(index, removal);
    }

    /**
     * {@inheritDoc}
     *
     * @return {@inheritDoc}
     */
    @Override
    public final boolean hasPrevious() {
        return super.hasPrevious();
    }

    /**
     * {@inheritDoc}
     *
     * @return {@inheritDoc}
     */
    @Override
    public final E previous() {
        return super.previous();
    }

    /**
     * {@inheritDoc}
     *
     * @return {@inheritDoc}
     */
    @Override
    public int previousIndex() {
        return super.previousIndex();
    }

    /**
     * {@inheritDoc}
     *
     * @param action {@inheritDoc}
     * @throws NullPointerException {@inheritDoc}
     */
    @Override
    public void forEachRemaining(Consumer<? super E> action) {
        super.forEachRemaining(action);
    }

    /**
     * {@inheritDoc}
     *
     * @param action {@inheritDoc}
     * @throws NullPointerException {@inheritDoc}
     */
    @Override
    public void forEachPreceding(Consumer<? super E> action) {
        super.forEachPreceding(action);
    }

    /**
     * {@inheritDoc}
     *
     * @return {@inheritDoc}
     */
    @Override
    protected abstract boolean hasPreviousElement();
}
