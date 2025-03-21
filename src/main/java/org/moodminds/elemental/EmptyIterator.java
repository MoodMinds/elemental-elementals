package org.moodminds.elemental;

import java.util.Collections;
import java.util.Iterator;

/**
 * This class defines an immutable empty {@link Iterator}.
 * <p>
 * Unlike the conventional {@link Collections#emptyIterator()}, this iterator
 * does support throwing {@link UnsupportedOperationException} on {@link Iterator#remove()}.
 *
 * @param <E> the type of elements
 */
public class EmptyIterator<E> extends AbstractIterator<E> {

    /**
     * Construct the object.
     */
    protected EmptyIterator() {}

    /**
     * Construct the object with the given flag indicating either removal operation is supported.
     *
     * @param supportRemoval the given flag indicating either removal operation is supported.
     */
    protected EmptyIterator(boolean supportRemoval) {
        super(supportRemoval ? () -> {} : null);
    }

    /**
     * {@inheritDoc}
     *
     * @return {@inheritDoc}
     */
    @Override
    public boolean hasNextElement() {
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

    /**
     * Return an empty {@link Iterator} by the given flag indicating either removal operation is supported.
     *
     * @param supportRemoval the given flag indicating either removal operation is supported.
     * @return an empty {@link Iterator}
     * @param <V> the type of element value
     */
    public static <V> Iterator<V> iterator(boolean supportRemoval) {
        return new EmptyIterator<>(supportRemoval);
    }
}
