package org.moodminds.elemental;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;

import static java.util.function.Predicate.isEqual;

/**
 * A template implementation of the {@link Container} interface.
 *
 * @param <V> the type of elements
 */
public abstract class AbstractContainer<V> extends AbstractEquatable implements Container<V> {

    /**
     * A reusable factory for producing a function that counts occurrences of elements in a Container.
     * <p>
     * This constant is a function that, given a {@link Container}, returns a function mapping each distinct element
     * to the number of times it appears in that Container.
     */
    private static final Function<Container<?>, Function<Object, Integer>> COUNTER = container ->
            container instanceof RandomMatch ? container::getCount : ((Function<Object, int[]>) container.stream().collect(
                    HashMap<Object, int[]>::new, (counts, value) -> counts.merge(value, new int[]{1}, (count, unused) -> {
                        count[0]++; return count;
                    }), Map::putAll
            )::get).andThen(count -> count != null ? count[0] : 0);

    /**
     * {@inheritDoc}
     *
     * @param o {@inheritDoc}
     * @return {@inheritDoc}
     * @throws ClassCastException {@inheritDoc}
     * @throws NullPointerException {@inheritDoc}
     */
    @Override
    public Iterator<V> getAll(Object o) {
        return iterator(iterator(), o);
    }

    /**
     * {@inheritDoc}
     *
     * @param o {@inheritDoc}
     * @return {@inheritDoc}
     * @throws ClassCastException {@inheritDoc}
     * @throws NullPointerException {@inheritDoc}
     */
    @Override
    public int getCount(Object o) {
        return (int) stream().filter(isEqual(o)).count();
    }

    /**
     * {@inheritDoc}
     *
     * @return {@inheritDoc}
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
     * @return {@inheritDoc}
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

        Function<Object, Integer> count1 = COUNTER.apply(this), count2 = COUNTER.apply(c);

        try {
            for (V value : this)
                if (!count1.apply(value).equals(count2.apply(value)))
                    return false;
            return true;
        } catch (ClassCastException | NullPointerException ignored) {
            return false;
        }
    }

    /**
     * Returns a string representation of this Container.
     *
     * @return a string representation of this Container
     */
    @Override
    public String toString() {
        StringBuilder string = new StringBuilder(toStringBegin());

        Iterator<V> it = iterator();

        if (it.hasNext()) {
            while (true) {
                V v = it.next();
                string.append(v == this ? toStringThis() : toStringEntry(v));
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
     * Return the {@link String} representation of this Container's value.
     *
     * @param v the given Container's entry
     * @return the {@link String} representation of this Container's value
     */
    protected String toStringEntry(V v) {
        return String.valueOf(v);
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

    /**
     * Return an {@link Iterator} that filters elements based on a reference object.
     *
     * @param i the source {@link Iterator} providing elements to filter
     * @param o the reference object used to determine which elements should be included
     * @return an {@link Iterator} that yields only elements matching the reference object
     */
    protected final Iterator<V> iterator(Iterator<V> i, Object o) {
        return iterator(i, o, null);
    }

    /**
     * Return an {@link Iterator} that filters elements based on a reference object and optionally support element removal.
     *
     * @param i the source {@link Iterator} providing elements to filter
     * @param o the reference object used to determine which elements should be included
     * @param removal a {@link Runnable} representing the removal operation (optional)
     * @return an {@link Iterator} that yields only elements matching the reference object and allows optional removal
     */
    protected Iterator<V> iterator(Iterator<V> i, Object o, Runnable removal) {
        return new AbstractIterator<V>(removal) {

            V next;

            @Override protected boolean hasNextElement() {
                boolean hasNext = false; V next;
                while (i.hasNext())
                    if (Objects.equals(o, next = i.next())) {
                        hasNext = true; this.next = next; break; }
                return hasNext; }
            @Override protected V nextElement() {
                return next; }
        };
    }
}
