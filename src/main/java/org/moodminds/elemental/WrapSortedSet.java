package org.moodminds.elemental;

import java.util.Comparator;
import java.util.Iterator;
import java.util.NoSuchElementException;

import static org.moodminds.sneaky.Cast.cast;

/**
 * Wrapping {@link java.util.SortedSet} implementation of the {@link SortedSet} interface.
 *
 * @param <E> the type of elements
 * @param <S> the type of wrapped {@link java.util.SortedSet}
 */
public class WrapSortedSet<E, S extends java.util.SortedSet<E>>
        extends WrapSet<E, S> implements SortedSet<E> {

    private static final long serialVersionUID = -3911881680308313564L;

    /**
     * Construct the object with the given {@link S} set.
     *
     * @param set the given {@link S} set to wrap
     */
    protected WrapSortedSet(S set) {
        super(set);
    }

    @Override public E first() {
        return wrapped.first(); }
    @Override public Iterator<E> firstAll() {
        return contains() ? iterator(wrapped.first(), true) : iterator(null, false); }
    @Override public E last() {
        return wrapped.last(); }
    @Override public Iterator<E> lastAll() {
        return contains() ? iterator(wrapped.last(), true) : iterator(null, false); }
    @Override public Comparator<? super E> comparator() {
        return wrapped.comparator(); }
    @Override public SortedSet<E> subSet(E fromElement, E toElement) {
        return wrap(wrapped.subSet(fromElement, toElement)); }
    @Override public SortedSet<E> headSet(E toElement) {
        return wrap(wrapped.headSet(toElement)); }
    @Override public SortedSet<E> tailSet(E fromElement) {
        return wrap(wrapped.tailSet(fromElement)); }

    protected Iterator<E> iterator(E value, boolean present) {
        return new Iterator<E>() {

            Iterator<?> modCheckIterator = WrapSortedSet.this.iterator();

            final Iterator<E> iterator = new OptionalIterator<E>(value, present) {
                @Override protected void removeElement() {
                    checkMod(); wrapped.remove(value);
                    modCheckIterator = WrapSortedSet.this.iterator(); present = false;
                } };

            @Override public boolean hasNext() { return iterator.hasNext(); }
            @Override public E next() { checkMod(); return iterator.next(); }
            @Override public void remove() { iterator.remove(); }

            void checkMod() { try { modCheckIterator.next(); } catch (NoSuchElementException ignored) {} }
        };
    }


    /**
     * Return wrapping {@link SortedSet} instance of the given {@link java.util.SortedSet} set.
     *
     * @param set the given {@link java.util.SortedSet} set
     * @param <V> the type of elements
     * @return wrapping {@link SortedSet} instance of the given {@link java.util.SortedSet} set
     * @throws NullPointerException if the given {@link java.util.SortedSet} set is {@code null}
     */
    public static <V> SortedSet<V> wrap(java.util.SortedSet<V> set) {
        return set instanceof java.util.NavigableSet ? WrapNavigableSet.wrap(cast(set))
                : new WrapSortedSet<>(set);
    }
}
