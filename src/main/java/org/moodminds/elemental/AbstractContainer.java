package org.moodminds.elemental;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.function.BiFunction;

import static org.moodminds.elemental.HashCollection.collection;

/**
 * Template implementation of the {@link Container} interface.
 *
 * @param <E> the type of elements
 */
public abstract class AbstractContainer<E> extends AbstractEquatable implements Container<E> {

    /**
     * {@inheritDoc}
     *
     * @return {@code true} {@inheritDoc}
     */
    @Override
    public int hashCode() {
        int hashCode = 0;
        for (Object element : this)
            if (element != null)
                hashCode += element.hashCode();
        return hashCode;
    }

    /**
     * {@inheritDoc}
     *
     * @param obj {@inheritDoc}
     * @return {@code true} {@inheritDoc}
     */
    @Override
    public boolean equatable(Object obj) {
        return obj instanceof Container;
    }

    /**
     * {@inheritDoc}
     *
     * @param equatable {@inheritDoc}
     * @return {@inheritDoc}
     */
    @Override
    protected boolean equals(Equatable equatable) {
        return equals((Container<?>) equatable);
    }

    /**
     * Return {@code true} if this {@link Container} is equal to the given {@link Container},
     * or {@code false} otherwise.
     *
     * @param c the given {@link Container} to check this for equality to
     * @return {@code true} if this {@link Container} is equal to the given {@link Container}
     */
    protected boolean equals(Container<?> c) {

        if (size() != c.size())
            return false;

        return new Object() {

            <V> Container<V> random(Container<V> container) {
                return container instanceof RandomMatch ? container : new AbstractContainer<V>() {

                    Container<V> random;

                    @Override public int getCount(Object o) {
                        return random != null ? random.getCount(o) : container.getCount(o); }
                    @Override public int size() {
                        return random != null ? random.size() : container.size(); }
                    @Override public Iterator<V> iterator() {
                        return random != null ? random.iterator() : new WrapIterator<V, V, Iterator<V>>(container.iterator()) {

                            final Collection<V> hashed = collection();

                            @Override public boolean hasNext() {
                                if (super.hasNext())
                                    return true;
                                random = hashed; return false; }
                            @Override public V next() { V next; hashed.add(next = super.next()); return next; }
                            @Override public V getTarget(V source) { return source; }
                        };
                    }
                };
            }

            boolean equals(BiFunction<Container<E>, Container<?>, Boolean> equality) {
                return equality.apply(random(AbstractContainer.this), random(c));
            }

        }.equals((c1, c2) -> {
            try {
                Set<E> seen = new HashSet<>();
                for (E element : c1) {
                    if (!seen.add(element))
                        continue;
                    if (c1.getCount(element) != c2.getCount(element))
                        return false;
                } return true;
            } catch (ClassCastException | NullPointerException ignored) {
                return false;
            }
        });
    }

    /**
     * Returns a string representation of this Container.
     *
     * @return a string representation of this Container
     */
    @Override
    public String toString() {

        StringBuilder string = new StringBuilder(toStringBegin());

        Iterator<E> it = iterator();

        if (it.hasNext()) {
            while (true) {
                E e = it.next();
                string.append(e == this ? toStringThis() : toStringEntry(e));
                if (it.hasNext()) string.append(", "); else break;
            }
        } return string.append(toStringEnd()).toString();
    }

    /**
     * Return the Container {@link #toString()} beginning prefix.
     *
     * @return the Container {@link #toString()} beginning prefix
     */
    protected String toStringBegin() {
        return "[";
    }

    /**
     * Return the Container {@link #toString()} ending prefix.
     *
     * @return the Container {@link #toString()} ending prefix
     */
    protected String toStringEnd() {
        return "]";
    }

    /**
     * Return the {@link String} representation of this Container's entry.
     *
     * @param e the given Container's entry
     * @return the {@link String} representation of this Container's entry
     */
    protected String toStringEntry(E e) {
        return String.valueOf(e);
    }

    /**
     * Return the {@link String} representation of this Container
     * for {@link #toString()} method if contains itself.
     *
     * @return the {@link String} representation of this Container if contains itself
     */
    protected String toStringThis() {
        return "(this Container)";
    }
}
