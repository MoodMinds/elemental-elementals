package org.moodminds.elemental;

import java.io.Serializable;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.stream.Stream;

import static java.lang.String.format;
import static java.lang.System.arraycopy;
import static java.util.Arrays.copyOf;
import static java.util.Arrays.copyOfRange;
import static java.util.Spliterator.IMMUTABLE;
import static java.util.Spliterator.ORDERED;
import static org.moodminds.sneaky.Cast.cast;

/**
 * A {@link RandomGet} array {@link Sequence} implementation.
 *
 * @param <E> the type of elements
 */
public class ArraySequence<E> extends AbstractSequence<E> implements RandomGet, Serializable {

    private static final long serialVersionUID = -667506475198692850L;

    /**
     * Field to hold the elements.
     */
    protected final Object[] array;

    /**
     * Construct the object with the specified elements array.
     *
     * @param elements the specified elements array
     */
    @SafeVarargs
    public ArraySequence(E... elements) {
        array = elements;
    }

    /**
     * Construct the object with the specified elements {@link Container}.
     *
     * @param elements the specified elements {@link Container}
     */
    public ArraySequence(Container<? extends E> elements) {
        this(elements.stream());
    }

    /**
     * Construct the object with the specified elements {@link java.util.Collection}.
     *
     * @param elements the specified elements {@link java.util.Collection}
     */
    public ArraySequence(java.util.Collection<? extends E> elements) {
        this(elements.stream());
    }

    /**
     * Construct the object with the specified elements {@link Collection}.
     *
     * @param elements the specified elements {@link Collection}
     */
    public ArraySequence(Collection<? extends E> elements) {
        this((java.util.Collection<? extends E>) elements);
    }

    /**
     * Construct the object with the specified elements {@link Stream}.
     *
     * @param elements the specified elements {@link Stream}
     */
    public ArraySequence(Stream<? extends E> elements) {
        array = elements.toArray();
    }

    /**
     * {@inheritDoc}
     *
     * @param index {@inheritDoc}
     * @return {@inheritDoc}
     * @throws IndexOutOfBoundsException {@inheritDoc}
     */
    @Override
    public <R extends E> R get(int index) {
        if (index < 0 || index >= array.length)
            throw new IndexOutOfBoundsException("Index out of range: " + index);
        return cast(array[index]);
    }

    /**
     * {@inheritDoc}
     *
     * @return {@inheritDoc}
     */
    @Override
    public Object[] toArray() {
        return copyOf(array, size());
    }

    /**
     * {@inheritDoc}
     *
     * @param array {@inheritDoc}
     * @return {@inheritDoc}
     */
    @Override
    public <T> T[] toArray(T[] array) {
        if (array.length < size())
            return cast(copyOf(this.array, size(), array.getClass()));
        arraycopy(this.array, 0, array, 0, size());
        if (array.length > size())
            array[size()] = null;
        return array;
    }

    /**
     * {@inheritDoc}
     *
     * @return {@inheritDoc}
     */
    @Override
    public int size() {
        return array.length;
    }

    /**
     * {@inheritDoc}
     *
     * @return {@inheritDoc}
     */
    @Override
    public Iterator<E> iterator() {
        return new ArrayIterator<>(this);
    }

    /**
     * {@inheritDoc}
     *
     * @param index {@inheritDoc}
     * @return {@inheritDoc}
     */
    @Override
    public SequenceIterator<E> sequenceIterator(int index) {
        return new ArraySequenceIterator<>(this, index);
    }

    /**
     * {@inheritDoc}
     *
     * @return {@inheritDoc}
     */
    @Override
    public Spliterator<E> spliterator() {
        return Spliterators.spliterator(array, ORDERED | IMMUTABLE);
    }

    /**
     * {@inheritDoc}
     *
     * @param fromIndex {@inheritDoc}
     * @param toIndex {@inheritDoc}
     * @return {@inheritDoc}
     * @throws IndexOutOfBoundsException {@inheritDoc}
     */
    @Override
    public Sequence<E> sub(int fromIndex, int toIndex) {
        return new ArraySubSequence<Sequence<E>, SequenceIterator<E>>(this, fromIndex, toIndex) {
            @Override public Sequence<E> sub(int fromIndex, int toIndex) {
                return subSub(this, fromIndex, toIndex); }

            Sequence<E> subSub(ArraySubSequence<Sequence<E>, SequenceIterator<E>> sequence, int fromIndex, int toIndex) {
                return new ArraySubSequence<Sequence<E>, SequenceIterator<E>>(sequence, fromIndex, toIndex) {
                    @Override public Sequence<E> sub(int fromIndex, int toIndex) {
                        return subSub(this, fromIndex, toIndex); } }; }
        };
    }


    /**
     * Array sub-sequence implementation of the {@link Sequence} interface.
     *
     * @param <S> the type of the parent {@link Sequence}
     * @param <I> the type of the parent {@link SequenceIterator}
     */
    protected abstract class ArraySubSequence<S extends Sequence<E>, I extends SequenceIterator<E>>
            extends AbstractSubSequence<E, S> implements RandomGet {

        protected ArraySubSequence(S sequence, int fromIndex, int toIndex) {
            super(sequence, fromIndex, toIndex);
        }

        @Override public Iterator<E> iterator() {
            return new ArrayIterator<>(this); }
        @Override public SequenceIterator<E> sequenceIterator(int index) {
            return new ArraySequenceIterator<>(this, index); }
        @Override public Spliterator<E> spliterator() {
            return Spliterators.spliterator(array, offset, offset + size, ORDERED | IMMUTABLE); }
        @Override public Object[] toArray() {
            return copyOfRange(ArraySequence.this.array, offset, offset + size); }
        @Override public <T> T[] toArray(T[] array) {
            if (array.length < size)
                return cast(copyOfRange(ArraySequence.this.array, offset, offset + size, array.getClass()));
            arraycopy(ArraySequence.this.array, offset, array, 0, size);
            if (array.length > size)
                array[size] = null;
            return array; }
    }

    /**
     * The {@link Iterator} for this Sequence.
     *
     * @param <E> the type of elements
     */
    protected static class ArrayIterator<E> implements Iterator<E> {

        protected final Sequence<E> sequence; protected int curr = 0, last = -1;

        protected ArrayIterator(Sequence<E> sequence) { this.sequence = sequence; }

        @Override public boolean hasNext() {
            return curr != sequence.size(); }
        @Override public E next() {
            try { int i = curr; last = i; curr = i + 1; return sequence.get(i); }
            catch (IndexOutOfBoundsException e) { throw new NoSuchElementException(); } }
    }

    /**
     * The array {@link SequenceIterator} for this Sequence.
     *
     * @param <E> the type of elements
     */
    protected static class ArraySequenceIterator<E> extends ArrayIterator<E> implements SequenceIterator<E> {

        protected ArraySequenceIterator(Sequence<E> sequence, int index) {
            super(sequence);
            if (index < 0 || index > sequence.size())
                throw new IndexOutOfBoundsException(format("Index: %d, Size: %d", index, sequence.size()));
            curr = index;
        }

        @Override public boolean hasPrevious() {
            return curr != 0; }
        @Override public E previous() {
            try { int i = curr - 1; last = curr = i; return sequence.get(i); }
            catch (IndexOutOfBoundsException e) { throw new NoSuchElementException(); }}
        @Override public int nextIndex() {
            return curr; }
        @Override public int previousIndex() {
            return curr - 1; }
    }


    /**
     * Return an {@link ArraySequence} of the given values.
     *
     * @param values the given values
     * @param <E> the type of elements
     * @return an {@link ArraySequence} of the given values
     */
    @SafeVarargs
    public static <E> ArraySequence<E> sequence(E... values) {
        return new ArraySequence<>(values);
    }
}
