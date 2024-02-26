package org.moodminds.elemental;

import java.util.Collections;
import java.util.Iterator;

/**
 * This class defines an immutable empty {@link Iterator}.
 * <p>
 * Unlike the conventional {@link Collections#emptyIterator()}, this iterator
 * does not support the {@link Iterator#remove()} operation by default. Instead of
 * throwing {@link IllegalStateException} as the {@link Collections#emptyIterator()} does,
 * it throws {@link UnsupportedOperationException} on {@link Iterator#remove()}.
 *
 * @param <E> the type of elements
 */
public class EmptyIterator<E> extends AbstractIterator<E> {

    /**
     * {@inheritDoc}
     *
     * @return {@inheritDoc}
     */
    @Override
    public boolean hasNext() {
        return false;
    }

    /**
     * {@inheritDoc}
     *
     * @return {@inheritDoc}
     */
    @Override
    protected E nextElement() {
        return null; // will never happen
    }


    /**
     * Return an empty {@link Iterator}.
     *
     * @return an empty {@link Iterator}
     * @param <V> the type of element value
     */
    public static <V> Iterator<V> iterator() {
        return new EmptyIterator<>();
    }
}
