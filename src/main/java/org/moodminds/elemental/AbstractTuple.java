package org.moodminds.elemental;

import java.util.Objects;

/**
 * A template implementation of the {@link Tuple} as a distinct structure.
 */
public abstract class AbstractTuple extends AbstractEquatable implements Tuple {

    /**
     * {@inheritDoc}
     *
     * @return {@inheritDoc}
     */
    @Override
    public int hashCode() {
        int hashCode = 1;
        for (int i = 0; i < width(); i++)
            hashCode = 31 * hashCode + Objects.hashCode(get(i));
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
        return obj instanceof Tuple;
    }

    /**
     * {@inheritDoc}
     *
     * @param {@inheritDoc}
     * @return {@inheritDoc}
     */
    @Override
    protected boolean equals(Equatable equatable) {
        return equals((Tuple) equatable);
    }

    /**
     * Return {@code true} if the specified Tuple consists of the same elements
     * in the same order, or {@code false} otherwise.
     *
     * @param t the specified sequence
     * @return {@code true} if the specified Tuple consists of the same elements in the same order
     */
    protected boolean equals(Tuple t) {
        if (width() != t.width())
            return false;
        for (int i = 0; i < width(); i++)
            if (!(Objects.equals(get(i), t.get(i))))
                return false;
        return true;
    }

    /**
     * Returns a string representation of this Tuple.
     *
     * @return a string representation of this Tuple
     */
    @Override
    public String toString() {

        int width = width();

        StringBuilder string = new StringBuilder("|");

        if (width > 0) {
            string.append(this.<Object>get(0));
            for (int i = 1; i < width; i++)
                string.append(", ").append(this.<Object>get(i));

        } return string.append("|").toString();
    }
}
