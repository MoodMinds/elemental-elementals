package org.moodminds.elemental;

import java.util.Iterator;
import java.util.Objects;

/**
 * A template implementation of the {@link Sequence} interface.
 *
 * @param <E> the type of elements
 */
public abstract class AbstractSequence<E> extends AbstractContainer<E> implements Sequence<E> {

    /**
     * {@inheritDoc}
     *
     * @return {@code true} {@inheritDoc}
     */
    @Override
    public int hashCode() {
        int hashCode = 1;
        for (Object e : this)
            hashCode = 31 * hashCode + Objects.hashCode(e);
        return hashCode;
    }

    /**
     * {@inheritDoc}
     *
     * @param obj {@inheritDoc}
     * @return {@inheritDoc}
     */
    @Override
    public boolean equatable(Object obj) {
        return obj instanceof Sequence;
    }

    /**
     * {@inheritDoc}
     *
     * @param c {@inheritDoc}
     * @return {@inheritDoc}
     */
    @Override
    protected boolean equals(Container<?> c) {
        return equals((Sequence<?>) c);
    }

    /**
     * Return {@code true} if this {@link Sequence} is equal to the given {@link Sequence},
     * or {@code false} otherwise.
     *
     * @param s the given {@link Sequence} to check this for equality to
     * @return {@code true} if this {@link Sequence} is equal to the given {@link Sequence}
     */
    protected boolean equals(Sequence<?> s) {
        if (size() != s.size())
            return false;
        Iterator<?> it1 = iterator(), it2 = s.iterator();
        while (it1.hasNext() && it2.hasNext())
            if (!(Objects.equals(it1.next(), it2.next())))
                return false;
        return true;
    }

    /**
     * {@inheritDoc}
     *
     * @return {@inheritDoc}
     */
    @Override
    protected String toStringThis() {
        return "(this Sequence)";
    }
}
