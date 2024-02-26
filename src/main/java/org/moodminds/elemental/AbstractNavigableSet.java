package org.moodminds.elemental;

import java.util.Iterator;
import java.util.Map.Entry;
import java.util.NavigableMap;
import java.util.SortedMap;

import static java.util.Optional.ofNullable;

/**
 * Template implementation of the {@link NavigableSet} interface,
 * which is powered by an internal {@link NavigableMap}.
 *
 * @param <E> the element type
 * @param <M> the type of the internal {@link SortedMap}
 */
public abstract class AbstractNavigableSet<E, M extends NavigableMap<E, Object>>
        extends AbstractSortedSet<E, M> implements NavigableSet<E> {

    private static final long serialVersionUID = 1139419161769574628L;

    /**
     * Construct the object with the given {@link M} map.
     *
     * @param map the given {@link M} map
     */
    protected AbstractNavigableSet(M map) {
        super(map);
    }

    @Override public Iterator<E> lowerAll(E e) {
        return ofNullable(map.lowerEntry(e)).map(Entry::getKey)
                .<Iterator<E>>map(SetSingleIterator::new).orElseGet(SetEmptyIterator::new); }
    @Override public Iterator<E> floorAll(E e) {
        return ofNullable(map.floorEntry(e)).map(Entry::getKey)
                .<Iterator<E>>map(SetSingleIterator::new).orElseGet(SetEmptyIterator::new); }
    @Override public Iterator<E> ceilingAll(E e) {
        return ofNullable(map.ceilingEntry(e)).map(Entry::getKey)
                .<Iterator<E>>map(SetSingleIterator::new).orElseGet(SetEmptyIterator::new); }
    @Override public Iterator<E> higherAll(E e) {
        return ofNullable(map.higherEntry(e)).map(Entry::getKey)
                .<Iterator<E>>map(SetSingleIterator::new).orElseGet(SetEmptyIterator::new); }

    @Override public E pollFirst() {
        return ofNullable(map.pollFirstEntry()).map(Entry::getKey).orElse(null); }
    @Override public E pollLast() {
        return ofNullable(map.pollLastEntry()).map(Entry::getKey).orElse(null); }

    @Override public Iterator<E> descendingIterator() {
        return map.descendingKeySet().iterator(); }
}
