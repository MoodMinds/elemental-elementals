package org.moodminds.elemental;

import java.io.InvalidObjectException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Iterator;
import java.util.Spliterator;
import java.util.stream.Stream;

/**
 * An array {@link Sequence} implementation.
 *
 * @param <E> the type of elements
 */
public class ArraySequence<E> extends AbstractArraySequence<E> {

    private static final long serialVersionUID = -667506475198692850L;

    /**
     * Construct the object with the specified elements array.
     *
     * @param elements the specified elements array
     */
    @SafeVarargs
    public ArraySequence(E... elements) {
        super(elements);
    }

    /**
     * Construct the object with the specified elements {@link Container}.
     *
     * @param elements the specified elements {@link Container}
     */
    public ArraySequence(Container<? extends E> elements) {
        super(elements.toArray());
    }

    /**
     * Construct the object with the specified elements {@link java.util.Collection}.
     *
     * @param elements the specified elements {@link java.util.Collection}
     */
    public ArraySequence(java.util.Collection<? extends E> elements) {
        super(elements.toArray());
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
        super(elements.sequential().toArray());
    }

    @Override public TailedSequence<E> sub(int fromIndex, int toIndex) {
        return fromIndex == 0 && toIndex == size ? this
                : new ArraySubSequence(0, size, fromIndex, toIndex); }

    @Override protected void serialize(ObjectOutputStream output) throws Exception {
        output.writeInt(size); for (int i = 0; i < size; i++)
            output.writeObject(array[i]); }
    @Override protected void deserialize(ObjectInputStream input) throws Exception {
        if ((size = input.readInt()) < 0)
            throw new InvalidObjectException("Negative size: " + size);
        array = new Object[size]; for (int i = 0; i < size; i++)
            array[i] = input.readObject(); }


    /**
     * A subsequence representation of an array-based sequence.
     */
    protected class ArraySubSequence extends AbstractArraySubSequence {

        private static final long serialVersionUID = 7806421786630968805L;

        protected ArraySubSequence(int offset, int size, int fromIndex, int toIndex) {
            super(offset, size, fromIndex, toIndex); }

        @Override public Iterator<E> iterator() {
            return ArraySequence.super.iterator(offset, this, 0, null); }
        @Override public TailedSequenceIterator<E> iterator(int index) {
            return ArraySequence.super.iterator(offset, this, positionIndex(index, size), null); }
        @Override public Spliterator<E> spliterator() {
            return ArraySequence.super.spliterator(offset, size); }
        @Override public TailedSequence<E> sub(int fromIndex, int toIndex) {
            return new ArraySubSequence(offset, size, fromIndex, toIndex); }
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
