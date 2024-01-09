package org.moodminds.elemental;

import java.util.Iterator;
import java.util.Map.Entry;
import java.util.NavigableMap;
import java.util.SortedMap;

import static java.util.Optional.ofNullable;

/**
 * Template implementation of the {@link NavigableContainer} interface,
 * which allows duplicates and is powered by an internal {@link NavigableMap}.
 *
 * @param <E> the element type
 * @param <N> the type of the internal {@link Node}
 * @param <B> the type of the internal {@link Bucket} that holds duplicates
 * @param <M> the type of the internal {@link SortedMap}
 */
public abstract class AbstractNavigableContainer<E,
            N extends AbstractMultiContainer.Node<E, N>,
            B extends AbstractMultiContainer.Bucket<E, N, B>,
            M extends NavigableMap<E, Object>>
        extends AbstractSortedContainer<E, N, B, M> implements NavigableContainer<E> {

    private static final long serialVersionUID = -1994968978084270407L;

    /**
     * Construct the object with the given {@link M} map and size.
     *
     * @param map the given {@link M} map
     * @param size the given size
     */
    protected AbstractNavigableContainer(M map, Integer size) {
        super(map, size);
    }

    /**
     * Construct the object with the given {@link M} map and parent Container.
     *
     * @param map the given {@link M} map
     * @param parent the given parent Container
     */
    protected AbstractNavigableContainer(M map, AbstractNavigableContainer<E, N, B, ?> parent) {
        super(map, parent);
    }

    /**
     * Construct the object with the given {@link M} map, size and parent Container if it does exist.
     *
     * @param map the given {@link M} map
     * @param size the given size
     * @param parent the given parent Container
     */
    protected AbstractNavigableContainer(M map, Integer size, AbstractNavigableContainer<E, N, B, ?> parent) {
        super(map, size, parent);
    }

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
}
