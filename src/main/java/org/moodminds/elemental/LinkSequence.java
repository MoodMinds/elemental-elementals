package org.moodminds.elemental;

import org.moodminds.elemental.LinkSequence.Link;

import java.io.InvalidObjectException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Iterator;
import java.util.function.Consumer;
import java.util.stream.Stream;

import static org.moodminds.elemental.Producer.producer;
import static org.moodminds.sneaky.Cast.cast;
import static org.moodminds.sneaky.Sneak.sneak;

/**
 * A forward-linked implementation of the {@link Sequence} interface.
 *
 * @param <E> the type of elements
 */
public class LinkSequence<E> extends AbstractLinkSequence<E, Link<E>> {

    private static final long serialVersionUID = -1418808492462226766L;

    /**
     * Construct the object with the specified elements array.
     *
     * @param elements the specified elements array
     */
    @SafeVarargs
    public LinkSequence(E... elements) {
        this(producer(elements));
    }

    /**
     * Construct the object with the specified elements {@link Container}.
     *
     * @param elements the specified elements {@link Container}
     */
    public LinkSequence(Container<? extends E> elements) {
        this(elements.stream());
    }

    /**
     * Construct the object with the specified elements {@link java.util.Collection}.
     *
     * @param elements the specified elements {@link java.util.Collection}
     */
    public LinkSequence(java.util.Collection<? extends E> elements) {
        this(elements.stream());
    }

    /**
     * Construct the object with the specified elements {@link Collection}.
     *
     * @param elements the specified elements {@link Collection}
     */
    public LinkSequence(Collection<? extends E> elements) {
        this((java.util.Collection<? extends E>) elements);
    }

    /**
     * Construct the object with the specified elements {@link Stream}.
     *
     * @param elements the specified elements {@link Stream}
     */
    public LinkSequence(Stream<? extends E> elements) {
        this(elements.sequential()::forEach);
    }

    /**
     * Construct the object with the given sequential single-threaded {@link Producer} of elements.
     *
     * @param elements the given sequential single-threaded {@link Producer} of elements
     */
    protected LinkSequence(Producer<? extends E> elements) {
        init(elements);
    }

    /**
     * Initialize the object with the given sequential single-threaded {@link Producer} of elements.
     *
     * @param elements the given sequential single-threaded {@link Producer} of elements
     */
    protected void init(Producer<? extends E> elements) {
        elements.provide(new Consumer<E>() {
            Link<E> tail;
            @Override public void accept(E e) {
                put(tail, tail = new Link<>(e)); }
        });
    }

    /**
     * Put the specified {@link Link} to this Container as the next to the previous one.
     *
     * @param previous the specified previous {@link Link} link
     * @param next the specified next {@link Link} link
     */
    protected void put(Link<E> previous, Link<E> next) {
        link(previous, next); size++;
    }

    @Override public Sequence<E> sub(int fromIndex, int toIndex) {
        return new LinkSubSequence(size, fromIndex, toIndex); }

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
                while (this.size < size) {
                    consumer.accept(cast(input.readObject())); }
            } catch (Exception e) { sneak(e); }
        });
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

    /**
     * A subsequence view of a linked sequence.
     */
    protected class LinkSubSequence extends AbstractLinkSubSequence {

        private static final long serialVersionUID = -7518921420091550676L;

        protected LinkSubSequence(int size, int fromIndex, int toIndex) {
            super(size, fromIndex, toIndex); }

        protected LinkSubSequence(LinkSubSequence parent, int fromIndex, int toIndex) {
            super(parent, fromIndex, toIndex); }

        @Override public Iterator<E> iterator() {
            return LinkSequence.super.iterator(previous(0), this, 0, null); }
        @Override public SequenceIterator<E> iterator(int index) {
            return LinkSequence.super.iterator(previous(positionIndex(index, size)), this, index, null); }
        @Override public Sequence<E> sub(int fromIndex, int toIndex) {
            return new LinkSubSequence(this, fromIndex, toIndex); }
    }


    /**
     * Return a {@link LinkSequence} of the given elements values.
     *
     * @param elements the given element values
     * @param <E> the type of elements
     * @return a {@link LinkSequence} of the given element values
     */
    @SafeVarargs
    public static <E> LinkSequence<E> sequence(E... elements) {
        return new LinkSequence<>(elements);
    }
}
