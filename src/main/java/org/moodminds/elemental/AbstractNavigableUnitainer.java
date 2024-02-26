package org.moodminds.elemental;

import java.util.Iterator;
import java.util.Map;
import java.util.NavigableMap;

import static java.util.Optional.ofNullable;

/**
 * Template unique-values implementation of the {@link NavigableContainer}
 * interface, which is powered by an internal {@link NavigableMap}.
 *
 * @param <E> the element type
 * @param <M> the type of the internal {@link NavigableMap}
 */
public abstract class AbstractNavigableUnitainer<E, M extends NavigableMap<E, E>> extends AbstractSortedUnitainer<E, M>
        implements NavigableContainer<E> {

    private static final long serialVersionUID = 2736637274731331383L;

    /**
     * Construct the object with the given {@link M} map.
     *
     * @param map the given {@link M} map
     */
    protected AbstractNavigableUnitainer(M map) {
        super(map);
    }

    @Override public Iterator<E> lowerAll(E e) {
        return ofNullable(map.lowerEntry(e)).map(Map.Entry::getValue)
                .map(SingleIterator::iterator).orElseGet(EmptyIterator::iterator); }
    @Override public Iterator<E> floorAll(E e) {
        return ofNullable(map.floorEntry(e)).map(Map.Entry::getValue)
                .map(SingleIterator::iterator).orElseGet(EmptyIterator::iterator); }
    @Override public Iterator<E> ceilingAll(E e) {
        return ofNullable(map.ceilingEntry(e)).map(Map.Entry::getValue)
                .map(SingleIterator::iterator).orElseGet(EmptyIterator::iterator); }
    @Override public Iterator<E> higherAll(E e) {
        return ofNullable(map.higherEntry(e)).map(Map.Entry::getValue)
                .map(SingleIterator::iterator).orElseGet(EmptyIterator::iterator); }
}
