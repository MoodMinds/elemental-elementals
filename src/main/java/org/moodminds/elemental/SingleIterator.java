package org.moodminds.elemental;

import java.util.Iterator;

/**
 * A single element {@link Iterator}.
 *
 * @param <E> the type of element value
 */
public class SingleIterator<E> extends AbstractIterator<E> {

    /**
     * The next element holder field.
     */
    protected E next;

    /**
     * The hasNext flag holder field.
     */
    protected boolean hasNext = true;

    /**
     * Construct the object by the given next value.
     *
     * @param next the given next value
     */
    public SingleIterator(E next) {
        this.next = next;
    }

    /**
     * {@inheritDoc}
     *
     * @return {@inheritDoc}
     */
    @Override
    public boolean hasNext() {
        return hasNext;
    }

    /**
     * {@inheritDoc}
     *
     * @return {@inheritDoc}
     */
    @Override
    protected E nextElement() {
        hasNext = false; return next;
    }


    /**
     * Return a single element {@link Iterator} by the given next value.
     *
     * @param next the given next value
     * @return a single element {@link Iterator} by the given next value
     * @param <V> the type of element value
     */
    public static <V> Iterator<V> iterator(V next) {
        return new SingleIterator<>(next);
    }
}
