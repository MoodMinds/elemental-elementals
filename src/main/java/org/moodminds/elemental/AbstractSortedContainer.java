package org.moodminds.elemental;

import java.util.Comparator;
import java.util.Iterator;
import java.util.Optional;
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
            N extends AbstractSortedContainer.Node<E, N>,
            B extends AbstractSortedContainer.Bucket<E, N, B>,
            M extends SortedMap<E, Object>>
        extends AbstractMultiContainer<E, N, B, M> implements SortedContainer<E> {

    private static final long serialVersionUID = 6521271551072774284L;

    /**
     * Parent Container holder field.
     */
    protected final AbstractSortedContainer<E, N, B, ?> parent;

    /**
     * Container size holder field.
     */
    protected Integer size;

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
        super(map); this.size = size; this.parent = parent;
    }

    @Override public int size() {
        if (size != null) return size;
        int size = 0; for (Object value : map.values())
            size = size + (isBucket(value) ? asBucket(value).size : 1);
        return this.size = size; }
    @Override public Comparator<? super E> comparator() {
        return map.comparator(); }

    @Override public Iterator<E> firstAll() {
        return contains() ? getAll(map.firstKey()) : iterator(null, false); }
    @Override public Iterator<E> lastAll() {
        return contains() ? getAll(map.lastKey()) : iterator(null, false); }

    @Override public Optional<Integer> getSize() {
        return Optional.of(size); }


    /**
     * Sub-container extension of the {@link AbstractSortedContainer}.
     *
     * @param <E> the element type
     */
    protected static abstract class SortedSubContainer<E,
                N extends AbstractSortedContainer.Node<E, N>,
                B extends AbstractSortedContainer.Bucket<E, N, B>>
            extends AbstractSortedContainer<E, N, B, SortedMap<E, Object>> {

        private static final long serialVersionUID = -301892284588225083L;

        protected SortedSubContainer(SortedMap<E, Object> map, AbstractSortedContainer<E, N, B, ?> parent) {
            super(map, parent); }

        @Override protected boolean isBucket(Object value) {
            return parent.isBucket(value); }
        @Override protected boolean isMulti() {
            return parent.isMulti(); }
    }
}
