package org.moodminds.elemental;

import java.util.Comparator;
import java.util.Iterator;
import java.util.SortedMap;

/**
 * Template implementation of the {@link SortedContainer} interface,
 * which allows duplicates and is powered by an internal {@link SortedMap}.
 *
 * @param <E> the element type
 * @param <N> the type of the internal {@link Node}
 * @param <B> the type of the internal {@link Bucket} that holds duplicates
 * @param <M> the type of the internal {@link SortedMap}
 */
public abstract class AbstractSortedContainer<E,
            N extends AbstractMultiContainer.Node<E, N>,
            B extends AbstractMultiContainer.Bucket<E, N, B>,
            M extends SortedMap<E, Object>>
        extends AbstractMultiContainer<E, N, B, M> implements SortedContainer<E> {

    private static final long serialVersionUID = -133212908877564940L;

    /**
     * Parent Container holder field.
     */
    protected final AbstractSortedContainer<E, N, B, ?> parent;

    /**
     * Construct the object with the given {@link M} map and size.
     *
     * @param map the given {@link M} map
     * @param size the given size
     */
    protected AbstractSortedContainer(M map, Integer size) {
        this(map, size, null);
    }

    /**
     * Construct the object with the given {@link M} map and parent Container.
     *
     * @param map the given {@link M} map
     * @param parent the given parent Container
     */
    protected AbstractSortedContainer(M map, AbstractSortedContainer<E, N, B, ?> parent) {
        this(map, null, parent);
    }

    /**
     * Construct the object with the given {@link M} map, size and parent Container if it does exist.
     *
     * @param map the given {@link M} map
     * @param size the given size
     * @param parent the given parent Container
     */
    protected AbstractSortedContainer(M map, Integer size, AbstractSortedContainer<E, N, B, ?> parent) {
        super(map, size); this.parent = parent;
    }

    @Override public Comparator<? super E> comparator() {
        return map.comparator(); }

    @Override public Iterator<E> firstAll() {
        return contains() ? getAll(map.firstKey()) : iterator(null, false); }
    @Override public Iterator<E> lastAll() {
        return contains() ? getAll(map.lastKey()) : iterator(null, false); }
}
