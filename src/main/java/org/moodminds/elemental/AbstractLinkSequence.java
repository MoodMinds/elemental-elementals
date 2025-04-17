package org.moodminds.elemental;

import org.moodminds.elemental.AbstractLinkSequence.Node;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Iterator;

import static java.lang.String.format;
import static org.moodminds.sneaky.Cast.cast;

/**
 * A template linked implementation of the {@link Sequence} interface.
 *
 * @param <E> the type of elements
 * @param <L> the type of {@link Node} link
 */
public abstract class AbstractLinkSequence<E, L extends Node<E, L>>
        extends AbstractSequence<E> implements Serializable {

    private static final long serialVersionUID = 239364802410716153L;

    /**
     * Head {@link L} node holding field.
     */
    protected transient L head;

    /**
     * Sequence size holding field.
     */
    protected transient int size;

    /**
     * {@inheritDoc}
     *
     * @param index {@inheritDoc}
     * @return {@inheritDoc}
     * @throws IndexOutOfBoundsException {@inheritDoc}
     */
    @Override
    public <R extends E> R get(int index) {
        return cast(node(elementIndex(index, size)).item);
    }

    /**
     * {@inheritDoc}
     *
     * @return {@inheritDoc}
     */
    @Override
    public Iterator<E> iterator() {
        return iterator(null, this, 0);
    }

    /**
     * {@inheritDoc}
     *
     * @param index {@inheritDoc}
     * @return {@inheritDoc}
     * @throws IndexOutOfBoundsException {@inheritDoc}
     */
    @Override
    public TailedSequenceIterator<E> iterator(int index) {
        return iterator(positionIndex(index, size) == 0 ? null : node(index - 1), this, index);
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
     * Link the specified {@link L} as the next to the previous one.
     *
     * @param previous the specified previous {@link L} node
     * @param next the specified next {@link L} node
     */
    protected void link(L previous, L next) {
        if (previous != null) {
            next.next = previous.next; previous.next = next;
        } else head = next;
    }

    /**
     * Unlink the next {@link L} node from the given previous one.
     *
     * @param previous the specified previous {@link L} node
     */
    protected void unlink(L previous) {
        if (previous != null) {
            previous.next = previous.next != null ? previous.next.next : null;
        } else head = head.next;
    }

    /**
     * Retrieve the {@link L} node at the specified index, starting from the head.
     *
     * @param index the index of the {@link L} node to retrieve
     * @return the {@link L} node at the given index
     */
    protected L node(int index) {
        return next(head, index);
    }

    /**
     * Traverse the linked sequence to retrieve the {@link L} node at the specified index.
     *
     * @param node the starting {@link L} node from which traversal begins
     * @param index the number of steps to move forward
     * @return the {@link L} node at the given index after traversal
     */
    protected L next(L node, int index) {
        L next = node;
        for (int i = 0; i < index; i++)
            next = next.next;
        return next;
    }

    /**
     * Returns a {@link SequenceIterator} for iterating over the sequence starting from the given link.
     * <p>
     * This is a convenience overload of {@link #iterator(L, Sequence, int, Runnable)}
     * that does not define element removal.
     * </p>
     *
     * @param previous the previous link before the iteration starts, or {@code null} if starting from the head
     * @param sequence the given sequence
     * @param index    the starting index of the iteration
     * @return a {@link SequenceIterator} starting at the given index
     */
    protected final TailedSequenceIterator<E> iterator(L previous, Sequence<E> sequence, int index) {
        return iterator(previous, sequence, index, null);
    }

    /**
     * Return a {@link SequenceIterator} for iterating over the sequence starting from the given link.
     *
     * @param previous the previous link before the iteration starts, or {@code null} if starting from the head
     * @param sequence the given sequence
     * @param index the starting index of the iteration
     * @param removal a {@link Runnable} to execute upon element removal
     * @return a {@link SequenceIterator} starting at the given index
     */
    protected TailedSequenceIterator<E> iterator(L previous, Sequence<E> sequence, int index, Runnable removal) {
        return new AbstractLinkSequenceIterator<E, L>(previous, index, removal) {
            @Override protected boolean hasPreviousLink() { return super.hasPreviousLink() && index > 0; }
            @Override protected L previous(L link) { return AbstractLinkSequence.this.previous(link); }
            @Override protected boolean hasNext(L link) { return index < sequence.size(); }
            @Override protected L next(L link) { return link != null ? link.next : head; }
            @Override protected E item(L link) { return link.item; }
        };
    }

    /**
     * Validate and return the given index within the specified size.
     *
     * <p>Ensure that the provided {@code index} falls within
     * the valid range {@code [0, size - 1]}. If the index is out of bounds,
     * throw an {@link IndexOutOfBoundsException}.
     *
     * @param index the index to validate
     * @param size the upper bound (exclusive) for valid indices
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
     * @param size the upper bound (inclusive) for valid positions
     * @return the validated position
     * @throws IndexOutOfBoundsException if {@code index} is negative or greater than {@code size}
     */
    protected int positionIndex(int index, int size) {
        if (index < 0 || index > size)
            throw new IndexOutOfBoundsException("Index out of range: " + index);
        return index;
    }

    /**
     * Return previous link for the given {@link L} one.
     *
     * @param link the given {@link L} link
     * @return previous link for the given {@link L} one
     */
    protected abstract L previous(L link);

    private void writeObject(ObjectOutputStream output) throws Exception {
        output.defaultWriteObject(); serialize(output);
    }

    private void readObject(ObjectInputStream input) throws Exception {
        input.defaultReadObject(); deserialize(input);
    }

    protected abstract void serialize(ObjectOutputStream output) throws Exception;

    protected abstract void deserialize(ObjectInputStream input) throws Exception;



    /**
     * Represents a node in a linked structure, holding an element and a reference to the next link.
     *
     * @param <E> the type of element
     * @param <L> the type of the {@code Link}
     */
    protected static class Node<E, L extends Node<E, L>> {

        /**
         * Item {@link E} value holding field.
         */
        protected E item;

        /**
         * Next {@link L} link holding field.
         */
        protected L next;

        /**
         * Construct the object with the given item value.
         *
         * @param item the given item value
         */
        protected Node(E item) { this.item = item; }
    }

    /**
     * A subsequence view of a linked sequence template.
     * <p>
     * This class extends {@link AbstractSequence} and provides a serializable
     * representation of a contiguous segment of the original linked sequence.
     * </p>
     */
    protected abstract class AbstractLinkSubSequence extends AbstractSequence<E> implements Serializable {

        private static final long serialVersionUID = -7874437837379449962L;

        protected final AbstractLinkSubSequence parent;

        protected transient L previous;

        protected int offset, size;

        /**
         * Construct the object with the specified size and index range.
         *
         * @param size the total size of the subsequence
         * @param fromIndex the starting index of the subsequence (inclusive)
         * @param toIndex the ending index of the subsequence (exclusive)
         * @throws IndexOutOfBoundsException if {@code fromIndex} is negative or {@code toIndex} exceeds {@code size}
         * @throws IllegalArgumentException if {@code fromIndex} is greater than {@code toIndex}
         */
        protected AbstractLinkSubSequence(int size, int fromIndex, int toIndex) {
            this(null, size, fromIndex, toIndex);
        }

        /**
         * Construct the object with the given parent sequence using the parent's size.
         *
         * @param parent the parent sequence from which this subsequence is derived
         * @param fromIndex the starting index of the subsequence (inclusive)
         * @param toIndex the ending index of the subsequence (exclusive)
         * @throws IndexOutOfBoundsException if {@code fromIndex} is negative or {@code toIndex} exceeds the parent's size
         * @throws IllegalArgumentException if {@code fromIndex} is greater than {@code toIndex}
         */
        protected AbstractLinkSubSequence(AbstractLinkSubSequence parent, int fromIndex, int toIndex) {
            this(parent, parent.size, fromIndex, toIndex);
        }

        /**
         * Construct the object with the given parent sequence.
         *
         * @param parent the parent sequence from which this subsequence is derived
         * @param size the size of the parent sequence
         * @param fromIndex the starting index of the subsequence (inclusive)
         * @param toIndex the ending index of the subsequence (exclusive)
         * @throws IndexOutOfBoundsException if {@code fromIndex} is negative or {@code toIndex} exceeds {@code size}
         * @throws IllegalArgumentException if {@code fromIndex} is greater than {@code toIndex}
         */
        private AbstractLinkSubSequence(AbstractLinkSubSequence parent, int size, int fromIndex, int toIndex) {
            if (fromIndex < 0)
                throw new IndexOutOfBoundsException(format("fromIndex = %d", fromIndex));
            if (toIndex > size)
                throw new IndexOutOfBoundsException(format("toIndex = %d", toIndex));
            if (fromIndex > toIndex)
                throw new IllegalArgumentException(format("fromIndex(%d) > toIndex(%d)", fromIndex, toIndex));
            this.parent = parent; this.offset = fromIndex; this.size = toIndex - fromIndex;
        }

        @Override public <R extends E> R get(int index) {
            return cast(previous(elementIndex(index, size) + 1).item); }
        @Override public int size() {
            return size; }

        /**
         * Return the link element that precedes the specified index within this subsequence.
         * <p>
         * This method lazily resolves and caches the {@code previous} link to optimize subsequent lookups.
         * If the cached {@code previous} link is unavailable, it is derived either from the parent subsequence
         * (if present), or directly from the underlying structure using positional navigation.
         * </p>
         *
         * @param index the target index within the subsequence, relative to its logical range
         * @return the link element that immediately precedes the element at the specified index
         */
        protected L previous(int index) {
            if (previous == null)
                if (offset == 0) {
                    if (parent != null) previous = parent.previous(0);
                } else if (parent != null && parent.previous(0) != null)
                    previous = next(parent.previous(0), offset);
                else previous = node(offset - 1);
            if (index == 0) return previous;
            return previous != null ? next(previous, index) : next(head, index - 1);
        }
    }
}
