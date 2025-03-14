package org.moodminds.elemental;

import org.moodminds.elemental.AbstractLinkSequence.Link;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Iterator;

import static org.moodminds.sneaky.Cast.cast;

/**
 * A template linked implementation of the {@link Sequence} interface.
 *
 * @param <E> the type of elements
 * @param <L> the type of {@link Link} link
 */
public abstract class AbstractLinkSequence<E, L extends Link<E, L>>
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
        return cast(link(inBounds(index, size)).item);
    }

    /**
     * {@inheritDoc}
     *
     * @return {@inheritDoc}
     */
    @Override
    public Iterator<E> iterator() {
        return iterator(null, 0, size);
    }

    /**
     * {@inheritDoc}
     *
     * @param index {@inheritDoc}
     * @return {@inheritDoc}
     * @throws IndexOutOfBoundsException {@inheritDoc}
     */
    @Override
    public SequenceIterator<E> iterator(int index) {
        return iterator(inBounds(index, size) == 0 ? null : link(index - 1), index, size);
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
     * Retrieve the {@link L} link at the specified index, starting from the head.
     *
     * @param index the index of the {@link L} link to retrieve
     * @return the {@link L} link at the given index
     */
    protected L link(int index) {
        return nextLink(head, index);
    }

    /**
     * Traverse the linked sequence to retrieve the {@link L} link at the specified index.
     *
     * @param link the starting {@link L} link from which traversal begins
     * @param index the number of steps to move forward
     * @return the {@link L} link at the given index after traversal
     */
    protected L nextLink(L link, int index) {
        L next = link;
        for (int i = 0; i < index; i++)
            next = next.next;
        return next;
    }

    /**
     * Link the given {@code next} node to the specified {@code tail} node.
     * <p>
     * If the {@code tail} node is non-null, delegate the linking to {@link Link#linkNext(Link)}.
     * Otherwise, the {@code next} node becomes the new head of the sequence.
     * </p>
     *
     * @param tail the current tail node, or {@code null} if the sequence is empty
     * @param next the node to be linked as the next element
     */
    protected void linkNext(L tail, L next) {
        if (tail != null) tail.linkNext(next); else head = next;
    }

    /**
     * Unlink the node following the specified {@code previous} node.
     * <p>
     * If {@code previous} is {@code null}, remove the head node by advancing the head reference.
     * Otherwise, delegate the unlinking operation to {@link Link#unlinkNext()}.
     * </p>
     *
     * @param previous the node preceding the one to be unlinked, or {@code null} if unlinking the head
     */
    protected void unlinkNext(L previous) {
        if (previous == null) head = head.next; else previous.unlinkNext();
    }

    /**
     * Returns a {@link SequenceIterator} for iterating over the sequence starting from the given link.
     * <p>
     * This is a convenience overload of {@link #iterator(L, int, int, Runnable)}
     * that does not define element removal.
     * </p>
     *
     * @param previous the previous link before the iteration starts, or {@code null} if starting from the head
     * @param index the starting index of the iteration
     * @param size the total number of elements in the sequence
     * @return a {@link SequenceIterator} starting at the given index
     */
    protected final SequenceIterator<E> iterator(L previous, int index, int size) {
        return iterator(previous, index, size, null);
    }

    /**
     * Return a {@link SequenceIterator} for iterating over the sequence starting from the given link.
     *
     * @param previous the previous link before the iteration starts, or {@code null} if starting from the head
     * @param index the starting index of the iteration
     * @param size the total number of elements in the sequence
     * @param removal a {@link Runnable} to execute upon element removal
     * @return a {@link SequenceIterator} starting at the given index
     */
    protected SequenceIterator<E> iterator(L previous, int index, int size, Runnable removal) {
        return new AbstractLinkSequenceIterator<E, L>(previous, index, removal) {
            @Override protected boolean hasPreviousLink() { return super.hasPreviousLink() && index > 0; }
            @Override protected boolean hasNext(L link) { return index < size; }
            @Override protected L next(L link) { return link != null ? link.next : head; }
            @Override protected E item(L link) { return link.item; }
        };
    }

    /**
     * Validate that the given index is within the valid bounds of a sequence.
     *
     * @param index the index to check
     * @param size the total size of the sequence
     * @return the validated index if within bounds
     * @throws IndexOutOfBoundsException if the index is negative or greater than or equal to {@code size}
     */
    protected int inBounds(int index, int size) {
        if (index < 0 || index >= size)
            throw new IndexOutOfBoundsException("Index out of range: " + index);
        return index;
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
     * Represents a node in a linked structure, holding an element and a reference to the next link.
     *
     * @param <E> the type of element
     * @param <L> the type of the {@code Link}
     */
    protected static class Link<E, L extends Link<E, L>> {

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
        protected Link(E item) { this.item = item; }

        /**
         * Link the specified link as the next link to this one.
         *
         * @param next the link to be inserted as the next
         */
        protected void linkNext(L next) {
            next.next = this.next; this.next = next;
        }

        /**
         * Unlink the next node from this one.
         */
        protected void unlinkNext() {
            next = next != null ? next.next : null;
        }
    }
}
