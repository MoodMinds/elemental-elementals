package org.moodminds.elemental;

import java.io.Serializable;
import java.util.Iterator;
import java.util.Map;
import java.util.Spliterator;

import static java.util.Spliterator.IMMUTABLE;

/**
 * Template unique-values implementation of the {@link Container} interface.
 *
 * @param <E> the type of elements
 */
public abstract class AbstractUnitainer<E, M extends Map<E, E>> extends AbstractContainer<E>
        implements Serializable {

    private static final long serialVersionUID = -2001815907225641108L;

    /**
     * Backing {@link M map} holder field.
     */
    protected final M map;

    /**
     * Construct the object with the given {@link M} map.
     *
     * @param map the given {@link M} map
     */
    protected AbstractUnitainer(M map) {
        this.map = map;
    }

    @Override public Iterator<E> getAll(Object o) {
        E value = map.get(o); return iterator(value, value != null || map.containsKey(o)); }
    @Override public int getCount(Object o) {
        return contains(o) ? 1 : 0; }
    @Override public boolean contains() {
        return !map.isEmpty(); }
    @Override public boolean contains(Object o) {
        return map.containsKey(o); }
    @Override public int size() {
        return map.size(); }
    @Override public Iterator<E> iterator() {
        return WrapIterator.wrap(map.keySet().iterator()); }
    @Override public Spliterator<E> spliterator() {
        return WrapSpliterator.wrap(map.keySet().spliterator(), ch -> ch | IMMUTABLE); }

    protected Iterator<E> iterator(E value, boolean hasValue) {
        return new AbstractIterator<E>() {

            boolean hasNext = hasValue;

            @Override public boolean hasNext() {
                return hasNext; }
            @Override public E element() {
                hasNext = false; return value; }
        };
    }
}
