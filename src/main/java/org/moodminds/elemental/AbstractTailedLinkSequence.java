package org.moodminds.elemental;

import org.moodminds.elemental.AbstractTailedLinkSequence.Node;

import java.util.Iterator;

/**
 * A template linked implementation of the {@link TailedSequence} interface.
 *
 * @param <E> the type of elements
 * @param <L> the type of {@link Node} link
 */
public abstract class AbstractTailedLinkSequence<E, L extends Node<E, L>> extends AbstractLinkSequence<E, L>
        implements TailedSequence<E> {

    private static final long serialVersionUID = -3932808683619632752L;

    /**
     * Tail {@link L} node holding field.
     */
    protected transient L tail;

    /**
     * {@inheritDoc}
     *
     * @param o {@inheritDoc}
     * @return an {@inheritDoc}
     * @throws ClassCastException {@inheritDoc}
     * @throws NullPointerException {@inheritDoc}
     */
    @Override
    public Iterator<E> getAllDescending(Object o) {
        return iterator(descendingIterator(), o);
    }

    /**
     * Link the specified {@link L} as the next to the previous one.
     *
     * @param previous the specified previous {@link L} node
     * @param next the specified next {@link L} node
     */
    @Override
    protected void link(L previous, L next) {
        super.link(previous, next);
        if (next.next == null) tail = next;
        else next.next.previous = next;
        next.previous = previous;
    }

    /**
     * Unlink the next {@link L} node from the given previous one.
     *
     * @param previous the specified previous {@link L} node
     */
    @Override
    protected void unlink(L previous) {
        super.unlink(previous);
        if (previous == null)
            if (head == null) tail = null;
            else head.previous = null;
        else if (previous.next == null) tail = previous;
        else previous.next.previous = previous;
    }

    /**
     * Retrieve the {@link L} node at the specified index, traversing from the head if the index is closer to the head,
     * or from the tail otherwise.
     *
     * @param index the index of the {@link L} node to retrieve
     * @return the {@link L} node at the given index
     */
    @Override
    protected L node(int index) {
        return index < (size >> 1) ? super.node(index) : previous(tail, size - index - 1);
    }

    /**
     * {@inheritDoc}
     *
     * @param link {@inheritDoc}
     * @return {@inheritDoc}
     */
    @Override
    protected L previous(L link) {
        return link.previous;
    }

    /**
     * Traverse the linked sequence to retrieve the {@link L} node at the specified index.
     *
     * @param node the starting {@link L} node from which traversal begins
     * @param index the number of steps to move backward
     * @return the {@link L} node at the given index after traversal
     */
    protected L previous(L node, int index) {
        L previous = node;
        for (int i = 0; i < index; i++)
            previous = previous.previous;
        return previous;
    }


    /**
     * Represents a node in a linked structure, holding an element and references to the next and previous links.
     *
     * @param <E> the type of element
     * @param <L> the type of the {@code Link}
     */
    protected static class Node<E, L extends Node<E, L>> extends AbstractLinkSequence.Node<E, L> {

        /**
         * Previous {@link L} link holding field.
         */
        protected L previous;

        /**
         * Construct the object with the given item value.
         *
         * @param item the given item value
         */
        protected Node(E item) {
            super(item);
        }
    }

    /**
     * A subsequence view of a linked sequence template.
     */
    protected abstract class AbstractTailedLinkSubSequence extends AbstractLinkSubSequence
            implements TailedSequence<E> {

        private static final long serialVersionUID = -6403120751829933670L;

        /**
         * Construct the object with the specified size and index range.
         *
         * @param size the total size of the subsequence
         * @param fromIndex the starting index of the subsequence (inclusive)
         * @param toIndex the ending index of the subsequence (exclusive)
         * @throws IndexOutOfBoundsException if {@code fromIndex} is negative or {@code toIndex} exceeds {@code size}
         * @throws IllegalArgumentException if {@code fromIndex} is greater than {@code toIndex}
         */
        protected AbstractTailedLinkSubSequence(int size, int fromIndex, int toIndex) {
            super(size, fromIndex, toIndex);
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
        protected AbstractTailedLinkSubSequence(AbstractLinkSequence<E, L>.AbstractLinkSubSequence parent, int fromIndex, int toIndex) {
            super(parent, fromIndex, toIndex);
        }

        @Override public Iterator<E> getAllDescending(Object o) {
            return iterator(descendingIterator(), o); }
    }
}
