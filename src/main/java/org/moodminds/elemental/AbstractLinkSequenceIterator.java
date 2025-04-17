package org.moodminds.elemental;

import java.util.NoSuchElementException;
import java.util.function.Consumer;

/**
 * This class provides a template implementation of an {@link SequenceIterator}
 * for a source that is structured as a series of linked value nodes.
 *
 * @param <E> the type of elements
 * @param <L> the type of link
 */
public abstract class AbstractLinkSequenceIterator<E, L> extends AbstractLinkIterator<E, L>
        implements TailedSequenceIterator<E> {

    /**
     * Next element index holding field.
     */
    protected int index;

    /**
     * Construct the object with the given previous {@link L} link and next element index.
     *
     * @param previous the given previous {@link L} link
     * @param index the given next element index
     */
    protected AbstractLinkSequenceIterator(L previous, int index) {
        super(previous); this.index = index;
    }

    /**
     * Construct the object with the given previous {@link L} link, next element index
     * and {@link Runnable} removal operation.
     *
     * @param previous the given previous {@link L} link
     * @param index the given next element index
     * @param removal the given {@link Runnable} removal operation
     */
    protected AbstractLinkSequenceIterator(L previous, int index, Runnable removal) {
        super(previous, removal); this.index = index;
    }

    /**
     * {@inheritDoc}
     *
     * @return {@inheritDoc}
     */
    @Override
    public boolean hasPrevious() {
        return super.hasPrevious();
    }

    /**
     * {@inheritDoc}
     *
     * @return {@inheritDoc}
     * @throws NoSuchElementException {@inheritDoc}
     */
    @Override
    public E previous() {
        return super.previous();
    }

    /**
     * {@inheritDoc}
     *
     * @return {@inheritDoc}
     * @throws NoSuchElementException {@inheritDoc}
     */
    @Override
    protected E previousElement() {
        E previous = super.previousElement(); index--; return previous;
    }

    /**
     * {@inheritDoc}
     *
     * @return {@inheritDoc}
     * @throws NoSuchElementException {@inheritDoc}
     */
    @Override
    protected E nextElement() {
        E next = super.nextElement(); index++; return next;
    }

    /**
     * Return the index of the element that would be retrieved
     * by a subsequent call to {@link #previous}. If the sequence
     * iterator is positioned at the beginning, return -1.
     *
     * @return the index of the next element to be returned by {@link #previous},
     * or -1 if at the beginning of the sequence
     */
    @Override
    public int previousIndex() {
        return index - 1;
    }

    /**
     * {@inheritDoc}
     *
     * @return {@inheritDoc}
     */
    @Override
    public int nextIndex() {
        return index;
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
}
