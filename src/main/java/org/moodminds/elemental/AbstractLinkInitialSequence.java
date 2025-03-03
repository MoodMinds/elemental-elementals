package org.moodminds.elemental;

import org.moodminds.elemental.AbstractLinkInitialSequence.Node;

import java.io.InvalidObjectException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Iterator;
import java.util.function.Consumer;

import static java.lang.String.format;
import static org.moodminds.sneaky.Cast.cast;
import static org.moodminds.sneaky.Sneak.sneak;

/**
 * A template immutable linked implementation of the {@link Sequence} interface.
 *
 * @param <E> the type of elements
 */
public abstract class AbstractLinkInitialSequence<E> extends AbstractLinkSequence<E, Node<E>> {

    private static final long serialVersionUID = -7296050890079803320L;

    /**
     * Construct the object.
     */
    protected AbstractLinkInitialSequence() {}

    /**
     * Construct the object with the given sequential single-threaded {@link Producer} of elements.
     *
     * @param elements the given sequential single-threaded {@link Producer} of elements
     */
    protected AbstractLinkInitialSequence(Producer<? extends E> elements) {
        init(elements);
    }

    /**
     * Initialize the inner structure of this Sequence with the specified
     * sequential single-threaded {@link Producer} of elements.
     *
     * @param elements the specified sequential single-threaded {@link Producer} of elements
     */
    protected void init(Producer<? extends E> elements) {
        elements.provide(new Consumer<E>() {

            Node<E> tail;

            @Override public void accept(E element) {
                linkNext(tail, tail = new Node<>(element)); size++; }
        });
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
        return new LinkSubSequence(size, fromIndex, toIndex);
    }

    /**
     * Link the given {@code next} node to the specified {@code tail} node.
     * <p>
     * If the {@code tail} node is non-null, delegate the linking to {@link Node#linkNext(Node)}.
     * Otherwise, the {@code next} node becomes the new head of the sequence.
     * </p>
     *
     * @param tail the current tail node, or {@code null} if the sequence is empty
     * @param next the node to be linked as the next element
     */
    protected void linkNext(Node<E> tail, Node<E> next) {
        if (tail != null) tail.linkNext(next); else head = next;
    }

    @Override
    protected void serialize(ObjectOutputStream output) throws Exception {
        output.writeInt(size); for (E element : this)
            output.writeObject(element);
    }

    @Override
    protected void deserialize(ObjectInputStream input) throws Exception {
        int size; if ((size = input.readInt()) < 0)
            throw new InvalidObjectException("Negative size: " + size);
        init(consumer -> {
            try {
                while (this.size < size)
                    consumer.accept(cast(input.readObject()));
            } catch (Exception e) { sneak(e); }
        });
    }


    /**
     * A subsequence view of a linked sequence.
     * <p>
     * This class extends {@link AbstractSequence} and provides a serializable
     * representation of a contiguous segment of the original linked sequence.
     * </p>
     *
     * @param <E> the type of elements in this subsequence
     */
    protected class LinkSubSequence extends AbstractSequence<E> implements Serializable {

        private static final long serialVersionUID = -7874437837379449962L;

        protected final LinkSubSequence parent;

        protected transient Node<E> previous;

        protected final int offset, size;

        /**
         * Constructs a subsequence with the specified size and index range.
         *
         * @param size the total size of the subsequence
         * @param fromIndex the starting index of the subsequence (inclusive)
         * @param toIndex the ending index of the subsequence (exclusive)
         * @throws IndexOutOfBoundsException if {@code fromIndex} is negative or {@code toIndex} exceeds {@code size}
         * @throws IllegalArgumentException  if {@code fromIndex} is greater than {@code toIndex}
         */
        protected LinkSubSequence(int size, int fromIndex, int toIndex) {
            this(null, size, fromIndex, toIndex);
        }

        /**
         * Construct a subsequence view of the given parent sequence using the parent's size.
         *
         * @param parent the parent sequence from which this subsequence is derived
         * @param fromIndex the starting index of the subsequence (inclusive)
         * @param toIndex the ending index of the subsequence (exclusive)
         * @throws IndexOutOfBoundsException if {@code fromIndex} is negative or {@code toIndex} exceeds the parent's size
         * @throws IllegalArgumentException  if {@code fromIndex} is greater than {@code toIndex}
         */
        protected LinkSubSequence(LinkSubSequence parent, int fromIndex, int toIndex) {
            this(parent, parent.size, fromIndex, toIndex);
        }

        /**
         * Construct a subsequence view of the given parent sequence.
         *
         * @param parent the parent sequence from which this subsequence is derived
         * @param size the size of the parent sequence
         * @param fromIndex the starting index of the subsequence (inclusive)
         * @param toIndex the ending index of the subsequence (exclusive)
         * @throws IndexOutOfBoundsException if {@code fromIndex} is negative or {@code toIndex} exceeds {@code size}
         * @throws IllegalArgumentException  if {@code fromIndex} is greater than {@code toIndex}
         */
        private LinkSubSequence(LinkSubSequence parent, int size, int fromIndex, int toIndex) {
            if (fromIndex < 0)
                throw new IndexOutOfBoundsException(format("fromIndex = %d", fromIndex));
            if (toIndex > size)
                throw new IndexOutOfBoundsException(format("toIndex = %d", toIndex));
            if (fromIndex > toIndex)
                throw new IllegalArgumentException(format("fromIndex(%d) > toIndex(%d)", fromIndex, toIndex));
            this.parent = parent; this.offset = fromIndex; this.size = toIndex - fromIndex;
        }

        @Override public <R extends E> R get(int index) {
            return cast((previous() == null ? link(inBounds(index, size))
                    : nextLink(previous(), inBounds(index, size) + 1)).item); }
        @Override public int size() {
            return size; }
        @Override public Iterator<E> iterator() {
            return AbstractLinkInitialSequence.this.iterator(previous(), 0, size); }
        @Override public SequenceIterator<E> iterator(int index) {
            return AbstractLinkInitialSequence.this.iterator(previous(inBounds(index, size)), index, size); }
        @Override public Sequence<E> sub(int fromIndex, int toIndex) {
            return new LinkSubSequence(this, fromIndex, toIndex); }

        private Node<E> previous() {
            if (previous == null)
                if (offset == 0) {
                    if (parent != null) previous = parent.previous();
                } else if (parent != null && parent.previous() != null)
                    previous = nextLink(parent.previous(), offset);
                else previous = link(offset - 1);
            return previous;
        }

        private Node<E> previous(int index) {
            return index == 0 || previous() == null ? previous() : nextLink(previous(), index);
        }
    }


    /**
     * Implementation of the {@link Link}.
     *
     * @param <E> the type of elements
     */
    protected static class Node<E> extends Link<E, Node<E>> {

        /**
         * Construct the object with the specified item.
         *
         * @param item the item to store in this node
         */
        protected Node(E item) { super(item); }
    }
}
