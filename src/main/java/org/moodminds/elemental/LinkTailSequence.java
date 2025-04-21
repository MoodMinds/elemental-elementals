package org.moodminds.elemental;

import org.moodminds.elemental.LinkTailSequence.Node;

import java.io.InvalidObjectException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Iterator;
import java.util.stream.Stream;

import static org.moodminds.elemental.Producer.producer;
import static org.moodminds.sneaky.Cast.cast;
import static org.moodminds.sneaky.Sneak.sneak;

/**
 * A linked implementation of the {@link TailedSequence} interface.
 *
 * @param <E> the type of elements
 */
public class LinkTailSequence<E> extends AbstractTailedLinkSequence<E, Node<E>> {

    private static final long serialVersionUID = -1418808492462226766L;

    /**
     * Construct the object with the specified elements array.
     *
     * @param elements the specified elements array
     */
    @SafeVarargs
    public LinkTailSequence(E... elements) {
        this(producer(elements));
    }

    /**
     * Construct the object with the specified elements {@link Container}.
     *
     * @param elements the specified elements {@link Container}
     */
    public LinkTailSequence(Container<? extends E> elements) {
        this(elements.stream());
    }

    /**
     * Construct the object with the specified elements {@link java.util.Collection}.
     *
     * @param elements the specified elements {@link java.util.Collection}
     */
    public LinkTailSequence(java.util.Collection<? extends E> elements) {
        this(elements.stream());
    }

    /**
     * Construct the object with the specified elements {@link Collection}.
     *
     * @param elements the specified elements {@link Collection}
     */
    public LinkTailSequence(Collection<? extends E> elements) {
        this((java.util.Collection<? extends E>) elements);
    }

    /**
     * Construct the object with the specified elements {@link Stream}.
     *
     * @param elements the specified elements {@link Stream}
     */
    public LinkTailSequence(Stream<? extends E> elements) {
        this(elements.sequential()::forEach);
    }

    /**
     * Construct the object with the given sequential single-threaded {@link Producer} of elements.
     *
     * @param elements the given sequential single-threaded {@link Producer} of elements
     */
    protected LinkTailSequence(Producer<? extends E> elements) {
        init(elements);
    }

    /**
     * Initialize the object with the given sequential single-threaded {@link Producer} of elements.
     *
     * @param elements the given sequential single-threaded {@link Producer} of elements
     */
    protected void init(Producer<? extends E> elements) {
        elements.provide(this::put);
    }

    /**
     * Append the specified element to the end of the linked structure.
     * <p>
     * If the provided {@code tail} node is {@code null}, treat the new node as the first
     * element in the structure. Otherwise, the new node is linked after the given {@code tail}.
     * In both cases, increment the structure's {@code size}, and return the new node.
     *
     * @param element the given element to be added
     * @return the newly created node containing the given element
     */
    protected Node<E> put(E element) {
        Node<E> next; link(tail, tail = next = new Node<>(element)); size++; return next;
    }

    @Override public TailedSequence<E> sub(int fromIndex, int toIndex) {
        return new TailedLinkSubSequence(size, fromIndex, toIndex); }

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
     * Implementation of the {@link AbstractTailedLinkSequence.Node}.
     *
     * @param <E> the type of elements
     */
    protected static class Node<E> extends AbstractTailedLinkSequence.Node<E, Node<E>> {

        /**
         * Construct the object with the specified item.
         *
         * @param item the item to store in this node
         */
        protected Node(E item) { super(item); }
    }

    /**
     * A subsequence view of a linked sequence.
     */
    protected class TailedLinkSubSequence extends AbstractTailedLinkSubSequence {

        private static final long serialVersionUID = -7518921420091550676L;

        protected TailedLinkSubSequence(int size, int fromIndex, int toIndex) {
            super(size, fromIndex, toIndex); }

        protected TailedLinkSubSequence(TailedLinkSubSequence parent, int fromIndex, int toIndex) {
            super(parent, fromIndex, toIndex); }

        @Override public Iterator<E> iterator() {
            return LinkTailSequence.super.iterator(previous(0), this, 0, null); }
        @Override public TailedSequenceIterator<E> iterator(int index) {
            return LinkTailSequence.super.iterator(previous(positionIndex(index, size)), this, index, null); }
        @Override public TailedSequence<E> sub(int fromIndex, int toIndex) {
            return new TailedLinkSubSequence(this, fromIndex, toIndex); }
    }


    /**
     * Return a {@link LinkTailSequence} of the given elements values.
     *
     * @param elements the given element values
     * @param <E> the type of elements
     * @return a {@link LinkTailSequence} of the given element values
     */
    @SafeVarargs
    public static <E> LinkTailSequence<E> sequence(E... elements) {
        return new LinkTailSequence<>(elements);
    }
}
