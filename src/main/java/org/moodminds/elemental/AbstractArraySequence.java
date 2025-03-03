package org.moodminds.elemental;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Iterator;
import java.util.Spliterator;
import java.util.Spliterators;

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
        return element(inBounds(index, size));
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
        return iterator(0, 0, size);
    }

    /**
     * {@inheritDoc}
     *
     * @return {@inheritDoc}
     */
    @Override
    public TailedSequenceIterator<E> iterator(int index) {
        return iterator(0, inBounds(index, size), size);
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
     * @param offset the offset applied to iteration
     * @param index the starting index
     * @param size the maximum number of elements to iterate over
     * @return a {@link TailedSequenceIterator} starting at the specified index,
     * with the given offset, and limited to at most the specified size
     */
    protected TailedSequenceIterator<E> iterator(int offset, int index, int size) {
        return iterator(offset, index, size, null);
    }

    /**
     * Return a {@link TailedSequenceIterator} for this sequence, starting at the given index
     * with the specified offset and iterating over at most the given number of elements
     * and optionally supporting removal operation.
     *
     *
     * @param offset the offset applied to iteration
     * @param index the starting index
     * @param size the maximum number of elements to iterate over
     * @param removal a {@link Runnable} representing the removal operation (optional)
     * @return a {@link TailedSequenceIterator} starting at the specified index,
     * with the given offset, and limited to at most the specified size
     */
    protected TailedSequenceIterator<E> iterator(int offset, int index, int size, Runnable removal) {
        return new AbstractTailedSequenceIterator<E>(index, removal) {
            @Override protected boolean hasPreviousElement() { return index > 0; }
            @Override protected boolean hasNextElement() { return index < size; }
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
     * Check if the specified index is within bounds for the given size.
     * If the index is out of bounds, an {@link IndexOutOfBoundsException} is thrown.
     *
     * @param index the index to check
     * @param size the size that defines the valid range of indices
     * @return the given index if it is within bounds
     * @throws IndexOutOfBoundsException if the index is less than 0 or greater than or equal to the size
     */
    protected int inBounds(int index, int size) {
        if (index < 0 || index >= size)
            throw new IndexOutOfBoundsException("Index out of range: " + index);
        return index;
    }
}
