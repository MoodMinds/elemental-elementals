package org.moodminds.elemental;

import java.util.Comparator;
import java.util.Iterator;
import java.util.NoSuchElementException;

import static org.moodminds.sneaky.Cast.cast;

/**
 * Wrapping {@link java.util.SortedSet} implementation of the {@link SortedSet} interface.
 *
 * @param <V> the type of elements
 * @param <S> the type of wrapped {@link java.util.SortedSet}
 */
public class WrapSortedSet<V, S extends java.util.SortedSet<V>>
        extends WrapSet<V, S> implements SortedSet<V> {

    private static final long serialVersionUID = -3911881680308313564L;

    /**
     * Construct the object with the given {@link S} set.
     *
     * @param wrapped the given {@link S} set to wrap
     */
    protected WrapSortedSet(S wrapped) {
        super(wrapped);
    }

    @Override public V first() {
        return wrapped.first(); }
    @Override public Iterator<V> firstAll() {
        return contains() ? iterator(wrapped.first(), true) : iterator(null, false); }
    @Override public V last() {
        return wrapped.last(); }
    @Override public Iterator<V> lastAll() {
        return contains() ? iterator(wrapped.last(), true) : iterator(null, false); }
    @Override public Comparator<? super V> comparator() {
        return wrapped.comparator(); }
    @Override public SortedSet<V> subSet(V fromElement, V toElement) {
        return wrap(wrapped.subSet(fromElement, toElement)); }
    @Override public SortedSet<V> headSet(V toElement) {
        return wrap(wrapped.headSet(toElement)); }
    @Override public SortedSet<V> tailSet(V fromElement) {
        return wrap(wrapped.tailSet(fromElement)); }

    protected Iterator<V> iterator(V value, boolean hasValue) {
        return new AbstractRemoveIterator<V>() {

            Iterator<?> modCheckIterator = iterator(); boolean hasNext = hasValue;

            @Override public boolean hasNext() {
                return hasNext; }
            @Override public V next() {
                modCheckIterator.next(); return super.next(); }
            @Override protected V element() {
                hasNext = false; return value; }
            @Override protected void removeElement() {
                try { modCheckIterator.next(); }
                catch (NoSuchElementException ignored) {}
                wrapped.remove(value); modCheckIterator = iterator(); }
        };
    }


    /**
     * Return wrapping {@link SortedSet} instance of the given {@link java.util.SortedSet} set.
     *
     * @param wrapped the given {@link java.util.SortedSet} set
     * @param <V> the type of elements
     * @return wrapping {@link SortedSet} instance of the given {@link java.util.SortedSet} set
     * @throws NullPointerException if the given {@link java.util.SortedSet} set is {@code null}
     */
    public static <V> SortedSet<V> wrap(java.util.SortedSet<V> wrapped) {
        return wrapped instanceof java.util.NavigableSet ? WrapNavigableSet.wrap(cast(wrapped)) : new WrapSortedSet<>(wrapped);
    }
}
