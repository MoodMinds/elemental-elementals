package org.moodminds.elemental;

/**
 * This class provides a template implementation of the {@link SequenceIterator} interface.
 *
 * @param <E> the type of elements
 */
public abstract class AbstractSequenceIterator<E> extends AbstractIterator<E>
        implements SequenceIterator<E> {

    /**
     * Next element index holding field.
     */
    protected int index;

    /**
     * Construct the object with the given {@link Sequence} element index.
     *
     * @param index the given {@link Sequence} element index
     */
    protected AbstractSequenceIterator(int index) {
        this.index = index;
    }

    /**
     * Construct the object with the given {@link Sequence} element index and
     * {@link Runnable} removal operation.
     *
     * @param index the given {@link Sequence} element index
     * @param removal the given {@link Runnable} removal operation
     */
    protected AbstractSequenceIterator(int index, Runnable removal) {
        super(removal); this.index = index;
    }

    /**
     * Return the index of the element that would be retrieved
     * by a subsequent call to {@link #previous}. If the sequence
     * iterator is positioned at the beginning, return -1.
     *
     * @return the index of the next element to be returned by {@link #previous},
     * or -1 if at the beginning of the sequence
     */
    protected int previousIndex() {
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
}
