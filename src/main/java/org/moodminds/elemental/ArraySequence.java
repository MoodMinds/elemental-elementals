package org.moodminds.elemental;

import java.io.InvalidObjectException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Iterator;
import java.util.Spliterator;
import java.util.function.Consumer;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import static java.lang.String.format;

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

    /**
     * Construct the object with the given sequential
     * single-threaded {@link Producer} of elements.
     *
     * @param elements the given sequential single-threaded {@link Producer} of elements
     * @throws NullPointerException if {@code elements} is {@code null}
     */
    public ArraySequence(Producer<? extends E> elements) {
        super(StreamSupport.stream(new AbstractSpliterator<E>() {
            @Override public void forEachRemaining(Consumer<? super E> action) {
                elements.provide(action); }
            @Override public boolean tryAdvance(Consumer<? super E> action) {
                throw new Error("Unexpected 'tryAdvance' call."); } // should never happen
        }, false).toArray());
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
     * This class extends {@link AbstractTailedSequence} and provides functionality
     * for efficiently accessing and manipulating a contiguous subset of elements.
     */
    protected class ArraySubSequence extends AbstractTailedSequence<E>
            implements Serializable, RandomGet {

        private static final long serialVersionUID = -8885047280063723368L;

        protected final int offset, size;

        protected ArraySubSequence(int offset, int size, int fromIndex, int toIndex) {
            if (fromIndex < 0)
                throw new IndexOutOfBoundsException(format("fromIndex = %d", fromIndex));
            if (toIndex > size)
                throw new IndexOutOfBoundsException(format("toIndex = %d", toIndex));
            if (fromIndex > toIndex)
                throw new IllegalArgumentException(format("fromIndex(%d) > toIndex(%d)", fromIndex, toIndex));
            this.offset = offset + fromIndex; this.size = toIndex - fromIndex;
        }

        @Override public <R extends E> R get(int index) {
            return element(offset + inBounds(index, size)); }
        @Override public int size() {
            return size; }
        @Override public Object[] toArray() {
            return ArraySequence.this.toArray(offset, size); }
        @Override public <T> T[] toArray(T[] a) {
            return ArraySequence.this.toArray(a, offset, size); }
        @Override public Iterator<E> iterator() {
            return iterator(0); }
        @Override public TailedSequenceIterator<E> iterator(int index) {
            return ArraySequence.super.iterator(offset, inBounds(index, size), size, null); }
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
