package org.moodminds.elemental;

import java.io.Serializable;
import java.util.Comparator;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.NavigableMap;
import java.util.function.BiPredicate;

import static java.util.Optional.ofNullable;

/**
 * Template implementation of the {@link NavigableCollection} interface,
 * which allows duplicates and is powered by an internal {@link NavigableMap}.
 *
 * @param <E> the element type
 * @param <N> the type of the internal {@link Node}
 * @param <B> the type of the internal {@link Bucket} that holds duplicates
 * @param <M> the type of the internal {@link NavigableMap}
 */
public abstract class AbstractNavigableCollection<E,
            N extends AbstractMultiCollection.Node<E, N>,
            B extends AbstractMultiCollection.Bucket<E, N, B>,
            M extends NavigableMap<E, Object>>
        extends AbstractSortedCollection<E, N, B, M> implements NavigableCollection<E> {

    private static final long serialVersionUID = -9092551212464933303L;

    /**
     * Construct the object with the given {@link M} map and size.
     *
     * @param map the given {@link M} map
     * @param size the given size
     */
    protected AbstractNavigableCollection(M map, Integer size) {
        super(map, size);
    }

    /**
     * Construct the object by the given {@link M} map, size and parent Collection.
     *
     * @param map the given {@link M} map
     * @param size the given size
     * @param parent the given parent Collection
     */
    protected AbstractNavigableCollection(M map, Integer size, AbstractNavigableCollection<E, N, B, ?> parent) {
        super(map, size, parent);
    }

    /**
     * Construct the object by the given {@link M} map, parent Collection
     * and {@link BiPredicate} elements range check bound for subs.
     *
     * @param map the given {@link M} map
     * @param parent the given parent Collection
     * @param range the given {@link BiPredicate} elements range check bound
     * @param <P> the type of the {@link Serializable} {@link BiPredicate} intersection
     */
    protected <P extends BiPredicate<Comparator<E>, E> & Serializable> AbstractNavigableCollection(M map, AbstractNavigableCollection<E, N, B, ?> parent, P range) {
        super(map, parent, range);
    }

    @Override public E pollFirst() {
        return poll(iterator()); }
    @Override public E pollLast() {
        return poll(descending().iterator()); }

    @Override public Iterator<E> lowerAll(E e) {
        return ofNullable(map.lowerEntry(e)).map(Entry::getValue)
                .map(value -> isBucket(value) ? iterator(asBucket(value)) : iterator(unmask(value), true))
                .orElseGet(() -> iterator(null, false)); }
    @Override public Iterator<E> floorAll(E e) {
        return ofNullable(map.floorEntry(e)).map(Entry::getValue)
                .map(value -> isBucket(value) ? iterator(asBucket(value)) : iterator(unmask(value), true))
                .orElseGet(() -> iterator(null, false)); }
    @Override public Iterator<E> ceilingAll(E e) {
        return ofNullable(map.ceilingEntry(e)).map(Entry::getValue)
                .map(value -> isBucket(value) ? iterator(asBucket(value)) : iterator(unmask(value), true))
                .orElseGet(() -> iterator(null, false)); }
    @Override public Iterator<E> higherAll(E e) {
        return ofNullable(map.higherEntry(e)).map(Entry::getValue)
                .map(value -> isBucket(value) ? iterator(asBucket(value)) : iterator(unmask(value), true))
                .orElseGet(() -> iterator(null, false)); }


    private E poll(Iterator<E> iterator) {
        if (iterator.hasNext()) {
            E value = iterator.next();
            iterator.remove(); return value;
        } return null;
    }
}
