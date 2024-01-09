package org.moodminds.elemental;

import java.util.Iterator;
import java.util.Objects;

import static java.lang.String.format;

/**
 * Template implementation of the {@link Sequence} interface.
 *
 * @param <E> the type of elements
 */
public abstract class AbstractSequence<E> extends AbstractContainer<E> implements Sequence<E> {

    /**
     * {@inheritDoc}
     *
     * @return {@code true} {@inheritDoc}
     */
    @Override
    public int hashCode() {
        int hashCode = 1;
        for (Object e : this)
            hashCode = 31 * hashCode + Objects.hashCode(e);
        return hashCode;
    }

    /**
     * {@inheritDoc}
     *
     * @param obj {@inheritDoc}
     * @return {@code true} {@inheritDoc}
     */
    @Override
    public boolean equatable(Object obj) {
        return obj instanceof Sequence;
    }

    /**
     * {@inheritDoc}
     *
     * @param c {@inheritDoc}
     * @return {@inheritDoc}
     */
    @Override
    protected boolean equals(Container<?> c) {
        return equals((Sequence<?>) c);
    }

    /**
     * Return {@code true} if this {@link Sequence} is equal to the given {@link Sequence},
     * or {@code false} otherwise.
     *
     * @param c the given {@link Sequence} to check this for equality to
     * @return {@code true} if this {@link Sequence} is equal to the given {@link Sequence}
     */
    protected boolean equals(Sequence<?> c) {
        if (size() != c.size())
            return false;
        Iterator<?> it1 = iterator(), it2 = c.iterator();
        while (it1.hasNext() && it2.hasNext())
            if (!(Objects.equals(it1.next(), it2.next())))
                return false;
        return true;
    }

    /**
     * {@inheritDoc}
     *
     * @return {@inheritDoc}
     */
    @Override
    protected String toStringThis() {
        return "(this Sequence)";
    }


    /**
     * Template sub-sequence implementation of the {@link Sequence} interface.
     *
     * @param <E> the type of elements
     * @param <S> the type of the parent {@link Sequence}
     */
    protected abstract static class AbstractSubSequence<E, S extends Sequence<E>> extends AbstractSequence<E> {

        protected final S sequence;

        protected final int offset, size;

        protected AbstractSubSequence(S sequence, int fromIndex, int toIndex) {
            this(sequence, sequence.size(), 0, fromIndex, toIndex);
        }

        private AbstractSubSequence(S sequence, int size, int offset, int fromIndex, int toIndex) {
            if (fromIndex < 0)
                throw new IndexOutOfBoundsException(format("fromIndex = %d", fromIndex));
            if (toIndex > size)
                throw new IndexOutOfBoundsException(format("toIndex = %d", toIndex));
            if (fromIndex > toIndex)
                throw new IllegalArgumentException(format("fromIndex(%d) > toIndex(%d)", fromIndex, toIndex));
            this.sequence = sequence; this.offset = offset + fromIndex; this.size = toIndex - fromIndex;
        }

        @Override public <R extends E> R get(int index) {
            if (index < 0 || index >= size)
                throw new IndexOutOfBoundsException("Index out of range: " + index);
            return sequence.get(offset + index); }
        @Override public int size() {
            return size; }
        @Override public Iterator<E> iterator() {
            return sequenceIterator(); }
    }
}
