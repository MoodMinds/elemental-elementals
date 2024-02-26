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
     * @param set the given {@link S} set to wrap
     */
    protected WrapSortedSet(S set) {
        super(set);
    }

    @Override public V first() {
        return collection.first(); }
    @Override public Iterator<V> firstAll() {
        return contains() ? new SortedSetSingleIterator(collection.first())
                : new SortedSetEmptyIterator(); }
    @Override public V last() {
        return collection.last(); }
    @Override public Iterator<V> lastAll() {
        return contains() ? new SortedSetSingleIterator(collection.last())
                : new SortedSetEmptyIterator(); }
    @Override public Comparator<? super V> comparator() {
        return collection.comparator(); }
    @Override public SortedSet<V> subSet(V fromElement, V toElement) {
        return wrap(collection.subSet(fromElement, toElement)); }
    @Override public SortedSet<V> headSet(V toElement) {
        return wrap(collection.headSet(toElement)); }
    @Override public SortedSet<V> tailSet(V fromElement) {
        return wrap(collection.tailSet(fromElement)); }

    /**
     * Empty {@link Iterator} in this Set.
     */
    protected class SortedSetEmptyIterator extends EmptyIterator<V> {

        Iterator<?> modCheckIterator = WrapSortedSet.this.iterator();

        @Override public V next() { modCheckIterator.next(); return super.next(); }
        @Override protected void removeElement() { /* will never happen */ }
    }

    /**
     * {@link Iterator} over a single value in this Set.
     */
    protected class SortedSetSingleIterator extends SingleIterator<V> {

        Iterator<?> modCheckIterator = WrapSortedSet.this.iterator();

        protected SortedSetSingleIterator(V item) {
            super(item); }

        @Override public V next() {
            modCheckIterator.next(); return super.next(); }
        @Override protected void removeElement() {
            try { modCheckIterator.next(); }
            catch (NoSuchElementException ignored) {}
            collection.remove(next); modCheckIterator = WrapSortedSet.this.iterator(); }
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
