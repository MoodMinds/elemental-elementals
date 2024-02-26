package org.moodminds.elemental;

import java.lang.reflect.Method;
import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * This class provides a template implementation of the {@link Iterator} interface.
 * <p>
 * By default, instances of this {@link Iterator} are immutable, and calling
 * {@link Iterator#remove()} will result in an {@link UnsupportedOperationException}.
 * To enable removal operation, the {@link #removeElement()} method should be overridden.
 *
 * @param <E> the type of elements
 */
public abstract class AbstractIterator<E> implements Iterator<E> {

    /**
     * Marker for the 'on an element' iteration position.
     */
    private boolean curr;

    /**
     * {@inheritDoc}
     *
     * @return {@inheritDoc}
     * @throws NoSuchElementException {@inheritDoc}
     */
    @Override
    public E next() {
        if (!hasNext())
            throw new NoSuchElementException();
        E next = nextElement(); curr = true; return next;
    }

    /**
     * {@inheritDoc}
     *
     * @throws IllegalStateException {@inheritDoc}
     * @throws UnsupportedOperationException {@inheritDoc}
     */
    @Override
    public final void remove() {
        if (!curr && removeIsOverridden())
            throw new IllegalStateException();
        removeElement(); curr = false;
    }

    /**
     * Return the next element immediately following a successful invocation of {@link #hasNext()}.
     *
     * @return the next element if {@link #hasNext()} returned {@code true}
     */
    protected abstract E nextElement();

    /**
     * Remove from the underlying source the last element returned by this iterator (optional operation).
     *
     * @implSpec
     * The default implementation throws an instance of
     * {@link UnsupportedOperationException} and performs no other action.
     *
     * @throws UnsupportedOperationException if the {@code remove} operation is not supported by this iterator
     */
    protected void removeElement() {
        throw new UnsupportedOperationException("remove");
    }

    /**
     * Determine whether the {@link #removeElement()} method is overridden in subclasses.
     *
     * @return {@code true} if the {@link #removeElement()} method is overridden in any subclass, {@code false} otherwise.
     */
    private boolean removeIsOverridden() {
        for (Class<?> type = getClass(); type != AbstractIterator.class; type = type.getSuperclass())
            for (Method method : type.getDeclaredMethods())
                if ("removeElement".equals(method.getName()))
                    return true;
        return false;
    }
}
