package org.moodminds.elemental;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Iterator;
import java.util.Spliterator;
import java.util.Spliterators;

import static java.lang.String.format;
import static java.lang.System.arraycopy;
import static java.util.Arrays.copyOfRange;
import static java.util.Spliterator.IMMUTABLE;
import static java.util.Spliterator.ORDERED;
import static org.moodminds.sneaky.Cast.cast;

/**
 * A template {@link RandomGet} implementation of the {@link TailedSequence}
 * interface which is powered by an internal array.
 *
 * @param <E> the type of elements
 */
public abstract class AbstractArraySequence<E> extends AbstractTailedSequence<E>
        implements TailedSequence<E>, Serializable, RandomGet {

    private static final long serialVersionUID = -7609328079683545364L;

    /**
     * Sequence elements holding field.
     */
    protected transient Object[] array;

    /**
     * Sequence size holding field.
     */
    protected transient int size;

    /**
     * Construct the object with the given initial inner object array.
     *
     * @param array the given initial inner object array
     */
    protected AbstractArraySequence(Object[] array) {
        this(array, array.length);
    }

    /**
     * Construct the object with the given initial inner object array.
     *
     * @param array the given initial inner object array
     */
    protected AbstractArraySequence(Object[] array, int size) {
        this.array = array; this.size = size;
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
        return element(elementIndex(index, size));
    }

    /**
     * {@inheritDoc}
     *
     * @return {@inheritDoc}
     */
    @Override
    public Object[] toArray() {
        return toArray(0, size);
    }

    /**
     * {@inheritDoc}
     *
     * @param array {@inheritDoc}
     * @return {@inheritDoc}
     */
    @Override
    public <T> T[] toArray(T[] array) {
        return toArray(array, 0, size);
    }

    /**
     * {@inheritDoc}
     *
     * @return {@inheritDoc}
     */
    @Override
    public Iterator<E> iterator() {
        return iterator(0, this, 0);
    }

    /**
     * {@inheritDoc}
     *
     * @return {@inheritDoc}
     */
    @Override
    public TailedSequenceIterator<E> iterator(int index) {
        return iterator(0, this, positionIndex(index, size));
    }

    /**
     * {@inheritDoc}
     *
     * @return {@inheritDoc}
     */
    @Override
    public Spliterator<E> spliterator() {
        return spliterator(0, size);
    }

    /**
     * {@inheritDoc}
     *
     * @return {@inheritDoc}
     */
    @Override
    public int size() {
        return size;
    }

    /**
     * Return the element at the specified position in the internal array, cast to the desired type.
     *
     * @param index the index of the element to retrieve
     * @param <R> the target type for casting
     * @return the element at the specified index, cast to {@code R}
     */
    protected <R extends E> R element(int index) {
        return cast(array[index]);
    }

    /**
     * Return a {@link TailedSequenceIterator} for this sequence, starting at the given index
     * with the specified offset and iterating over at most the given number of elements.
     *
     * @param offset   the offset applied to iteration
     * @param sequence the given sequence
     * @param index    the starting index
     * @return a {@link TailedSequenceIterator} starting at the specified index,
     * with the given offset, and limited to at most the specified size
     */
    protected final TailedSequenceIterator<E> iterator(int offset, Sequence<E> sequence, int index) {
        return iterator(offset, sequence, index, null);
    }

    /**
     * Return a {@link TailedSequenceIterator} for this sequence, starting at the given index
     * with the specified offset and iterating over at most the given number of elements
     * and optionally supporting removal operation.
     *
     * @param offset   the offset applied to iteration
     * @param sequence the given sequence
     * @param index    the starting index
     * @param removal  a {@link Runnable} representing the removal operation (optional)
     * @return a {@link TailedSequenceIterator} starting at the specified index,
     * with the given offset, and limited to at most the specified size
     */
    protected TailedSequenceIterator<E> iterator(int offset, Sequence<E> sequence, int index, Runnable removal) {
        return new AbstractTailedSequenceIterator<E>(index, removal) {
            @Override protected boolean hasPreviousElement() { return index > 0; }
            @Override protected boolean hasNextElement() { return index < sequence.size(); }
            @Override protected E previousElement() { return element(offset + index-- - 1); }
            @Override protected E nextElement() { return element(offset + index++); }
        };
    }

    /**
     * Return a {@link Spliterator} fot this sequence with the specified offset
     * and iterating over at most the given number of elements.
     *
     * @param offset the offset applied to iteration
     * @param size the maximum number of elements to iterate over
     * @return a {@link Spliterator} fot this sequence with the specified offset
     * and iterating over at most the given number of elements
     */
    protected Spliterator<E> spliterator(int offset, int size) {
        return Spliterators.spliterator(array, offset, offset + size, ORDERED | IMMUTABLE);
    }

    /**
     * Return a new array containing elements from the internal array, starting at the specified offset and with the given size.
     *
     * @param offset the starting position in the internal array
     * @param size the number of elements to include in the returned array
     * @return a new array containing the elements from the internal array, from the given offset, up to the specified size
     */
    protected Object[] toArray(int offset, int size) {
        return copyOfRange(array, offset, offset + size);
    }

    /**
     * Fill the provided array with elements from the internal array, starting at the specified offset and with the given size.
     * If the provided array is too small, a new array of the appropriate type and size is returned.
     *
     * @param array the array to fill with elements
     * @param offset the starting position in the internal array
     * @param size the number of elements to copy into the provided array
     * @param <T> the type of the provided array
     * @return the provided array, filled with elements from the internal array, or a new array of the same type if the provided array is too small
     * @throws NullPointerException if the provided array is {@code null}
     */
    protected <T> T[] toArray(T[] array, int offset, int size) {
        if (array.length < size)
            return cast(copyOfRange(array, offset, offset + size, array.getClass()));
        arraycopy(this.array, offset, array, 0, size);
        if (array.length > size) array[size] = null; return array;
    }

    private void writeObject(ObjectOutputStream output) throws Exception {
        output.defaultWriteObject(); serialize(output);
    }

    private void readObject(ObjectInputStream input) throws Exception {
        input.defaultReadObject(); deserialize(input);
    }

    protected abstract void serialize(ObjectOutputStream output) throws Exception;

    protected abstract void deserialize(ObjectInputStream input) throws Exception;

    /**
     * Validate and return the given index within the specified size.
     *
     * <p>Ensure that the provided {@code index} falls within
     * the valid range {@code [0, size - 1]}. If the index is out of bounds,
     * throw an {@link IndexOutOfBoundsException}.
     *
     * @param index the index to validate
     * @param size  the upper bound (exclusive) for valid indices
     * @return the validated index
     * @throws IndexOutOfBoundsException if {@code index} is negative or not less than {@code size}
     */
    protected int elementIndex(int index, int size) {
        if (index < 0 || index >= size)
            throw new IndexOutOfBoundsException("Index out of range: " + index);
        return index;
    }

    /**
     * Validate and return the given position within the specified size.
     *
     * <p>Ensure that the provided {@code index} falls within
     * the valid range {@code [0, size]}. Unlike {@link #elementIndex(int, int)},
     * allow {@code index} to be equal to {@code size}, making
     * it suitable for scenarios where an exclusive upper bound is valid.
     * If the index is out of bounds, throw an {@link IndexOutOfBoundsException}.
     *
     * @param index the position to validate
     * @param size  the upper bound (inclusive) for valid positions
     * @return the validated position
     * @throws IndexOutOfBoundsException if {@code index} is negative or greater than {@code size}
     */
    protected int positionIndex(int index, int size) {
        if (index < 0 || index > size)
            throw new IndexOutOfBoundsException("Index out of range: " + index);
        return index;
    }


    /**
     * A subsequence representation of an array-based sequence template.
     * This class extends {@link AbstractTailedSequence} and provides functionality
     * for efficiently accessing and manipulating a contiguous subset of elements.
     */
    protected abstract class AbstractArraySubSequence extends AbstractTailedSequence<E>
            implements Serializable, RandomGet {

        private static final long serialVersionUID = -8885047280063723368L;

        protected int offset, size;

        /**
         * Construct the object with the specified boundaries.
         *
         * <p>Define a subsequence within an underlying array-based structure.
         * The subsequence is determined by the provided {@code fromIndex} and {@code toIndex},
         * which must satisfy the following constraints:
         * <ul>
         *     <li>{@code fromIndex} must be non-negative.</li>
         *     <li>{@code toIndex} must not exceed {@code size}.</li>
         *     <li>{@code fromIndex} must not be greater than {@code toIndex}.</li>
         * </ul>
         * Define the resulting subsequence relative to the given {@code offset},
         * ensuring that its indices align correctly within the parent structure.
         *
         * @param offset the base offset within the underlying structure
         * @param size the total size of the parent sequence
         * @param fromIndex the starting index of the subsequence (inclusive)
         * @param toIndex the ending index of the subsequence (exclusive)
         * @throws IndexOutOfBoundsException if {@code fromIndex} is negative or {@code toIndex} exceeds {@code size}
         * @throws IllegalArgumentException if {@code fromIndex} is greater than {@code toIndex}
         */
        protected AbstractArraySubSequence(int offset, int size, int fromIndex, int toIndex) {
            if (fromIndex < 0)
                throw new IndexOutOfBoundsException(format("fromIndex = %d", fromIndex));
            if (toIndex > size)
                throw new IndexOutOfBoundsException(format("toIndex = %d", toIndex));
            if (fromIndex > toIndex)
                throw new IllegalArgumentException(format("fromIndex(%d) > toIndex(%d)", fromIndex, toIndex));
            this.offset = offset + fromIndex; this.size = toIndex - fromIndex;
        }

        @Override public <R extends E> R get(int index) {
            return element(offset + elementIndex(index, size)); }
        @Override public int size() {
            return size; }
        @Override public Object[] toArray() {
            return AbstractArraySequence.this.toArray(offset, size); }
        @Override public <T> T[] toArray(T[] a) {
            return AbstractArraySequence.this.toArray(a, offset, size); }
    }
}
