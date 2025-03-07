package org.moodminds.elemental;

import org.moodminds.elemental.AbstractLinkInitialSequence.Link;

import java.io.InvalidObjectException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Iterator;
import java.util.function.Function;

import static java.lang.String.format;
import static org.moodminds.sneaky.Cast.cast;
import static org.moodminds.sneaky.Sneak.sneak;

/**
 * A template immutable linked implementation of the {@link Sequence} interface.
 *
 * @param <E> the type of elements
 */
public abstract class AbstractLinkInitialSequence<E> extends AbstractLinkSequence<E, Link<E>> {

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
     * Construct the object with the given sequential single-threaded {@link Producer} of elements
     * and a {@link Function} that determines how new links are attached.
     *
     * @param tails a {@link Function} that maps a newly created {@link Link} to its preceding link
     * @param elements the given sequential single-threaded {@link Producer} of elements
     */
    protected AbstractLinkInitialSequence(Function<Link<E>, Link<E>> tails, Producer<? extends E> elements) {
        init(tails, elements);
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
     * Initialize the internal structure of this Sequence using the specified
     * sequential, single-threaded {@link Producer} of elements.
     * <p>
     * Delegate to {@link #init(Function, Producer)} with a default function
     * that maintains a reference to the last inserted link.
     *
     * @param elements the sequential, single-threaded {@link Producer} supplying elements
     */
    protected void init(Producer<? extends E> elements) {
        init(new Function<Link<E>, Link<E>>() {

            Link<E> tail;

            @Override public Link<E> apply(Link<E> link) {
                Link<E> tail = this.tail; this.tail = link; return tail; }
        }, elements);
    }

    /**
     * Initialize the internal structure of this Sequence using the specified
     * {@link Producer} of elements and a function that determines how new links are attached.
     * <p>
     * The provided function defines how each new {@link Link} should be appended to the
     * sequence, allowing for customized link behavior.
     *
     * @param tails a function mapping a newly created {@link Link} to its preceding link
     * @param elements the {@link Producer} supplying elements for initialization
     */
    protected void init(Function<Link<E>, Link<E>> tails, Producer<? extends E> elements) {
        elements.provide(element -> { Link<E> next = new Link<>(element); put(tails.apply(next), next); });
    }

    /**
     * Insert the specified {@link Link} into the Sequence by attaching it to the given tail.
     * <p>
     * Establish a connection between {@code tail} and {@code link} using
     * {@link #linkNext(Link, Link)} and increment the sequence size.
     *
     * @param tail the current tail of the sequence, to which {@code link} will be appended
     * @param link the new {@link Link} to be inserted
     */
    protected void put(Link<E> tail, Link<E> link) {
        linkNext(tail, link); size++;
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

        protected transient Link<E> previous;

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
            return AbstractLinkInitialSequence.super.iterator(previous(), 0, size, null); }
        @Override public SequenceIterator<E> iterator(int index) {
            return AbstractLinkInitialSequence.super.iterator(previous(inBounds(index, size)), index, size, null); }
        @Override public Sequence<E> sub(int fromIndex, int toIndex) {
            return new LinkSubSequence(this, fromIndex, toIndex); }

        private Link<E> previous() {
            if (previous == null)
                if (offset == 0) {
                    if (parent != null) previous = parent.previous();
                } else if (parent != null && parent.previous() != null)
                    previous = nextLink(parent.previous(), offset);
                else previous = link(offset - 1);
            return previous;
        }

        private Link<E> previous(int index) {
            return index == 0 || previous() == null ? previous() : nextLink(previous(), index);
        }
    }


    /**
     * Implementation of the {@link AbstractLinkSequence.Link}.
     *
     * @param <E> the type of elements
     */
    protected static class Link<E> extends AbstractLinkSequence.Link<E, Link<E>> {

        /**
         * Construct the object with the specified item.
         *
         * @param item the item to store in this node
         */
        protected Link(E item) { super(item); }
    }
}
