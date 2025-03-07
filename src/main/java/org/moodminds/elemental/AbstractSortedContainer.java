package org.moodminds.elemental;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Comparator;
import java.util.Iterator;
import java.util.Map;
import java.util.Optional;
import java.util.SortedMap;
import java.util.Spliterator;

import static java.util.Optional.ofNullable;
import static org.moodminds.sneaky.Cast.cast;

/**
 * A template implementation of the {@link SortedContainer} interface,
 * which allows duplicates and is powered by an internal {@link SortedMap}.
 *
 * @param <E> the element type
 * @param <M> the type of the internal {@link SortedMap}
 */
public abstract class AbstractSortedContainer<E, M extends SortedMap<E, Object>>
        extends AbstractHeapInitialContainer<E, M> implements SortedContainer<E> {

    private static final long serialVersionUID = 6521271551072774284L;

    /**
     * Container size holder field.
     */
    protected Integer size;

    /**
     * Construct the object with the given {@link M map}.
     *
     * @param map the given {@link M map}
     */
    protected AbstractSortedContainer(M map) {
        super(map);
    }

    /**
     * Construct the object with the given {@link M map} and size.
     *
     * @param map the given {@link M map}
     * @param size the given size
     */
    protected AbstractSortedContainer(M map, Integer size) {
        super(map); this.size = size;
    }

    /**
     * Construct the object with the given {@link M map} and sequential single-threaded {@link Producer} of elements.
     *
     * @param map the given {@link M map}
     * @param elements the given sequential single-threaded {@link Producer} of elements
     */
    protected AbstractSortedContainer(M map, Producer<? extends E> elements) {
        super(map, elements);
    }

    @Override public int size() {
        return getSize().orElseGet(() -> {
            int count = 0; for (Object value : map.values())
                count = count + tryBucket(value, Container::size, unused -> 1);
            return size = count;
        }); }

    @Override public Comparator<? super E> comparator() {
        return map.comparator(); }

    @Override public Iterator<E> firstAll() {
        return contains() ? getAll(map.firstKey()) : iterator(null, false); }
    @Override public Iterator<E> lastAll() {
        return contains() ? getAll(map.lastKey()) : iterator(null, false); }

    @Override public SortedContainer<E> sub(E fromElement, E toElement) {
        return new SortedSubContainer<>(this, map.subMap(fromElement, toElement)); }
    @Override public SortedContainer<E> head(E toElement) {
        return new SortedSubContainer<>(this, map.headMap(toElement)); }
    @Override public SortedContainer<E> tail(E fromElement) {
        return new SortedSubContainer<>(this, map.tailMap(fromElement)); }

    /**
     * {@inheritDoc}
     *
     * @param elements {@inheritDoc}
     */
    @Override protected void init(Producer<? extends E> elements) {
        size = 0; super.init(elements); }

    @Override protected Optional<Integer> getSize() {
        return ofNullable(size); }

    @Override protected void count(int number) {
        size = size + number; }

    /**
     * {@inheritDoc}
     *
     * @param entriesSpliterator {@inheritDoc}
     * @param useSourceSize {@inheritDoc}
     * @param isDistinct {@inheritDoc}
     * @param knownSize {@inheritDoc}
     * @return {@inheritDoc}
     */
    @Override protected Spliterator<E> containerSpliterator(Spliterator<Map.Entry<E, Object>> entriesSpliterator, boolean useSourceSize, boolean isDistinct, Integer knownSize) {
        return super.containerSpliterator(entriesSpliterator, false, isDistinct, knownSize); }

    @Override protected void serialize(ObjectOutputStream output) throws Exception {
        output.writeObject(map); }
    @Override protected void deserialize(ObjectInputStream input) throws Exception {
        map = cast(input.readObject()); }


    /**
     * Sub-container extension of the {@link AbstractSortedContainer}.
     *
     * @param <P> the type of the parent collection
     * @param <E> the element type
     * @param <M> the type of the internal {@link SortedMap}
     */
    protected static class SortedSubContainer<P extends AbstractSortedContainer<E, ? extends M>, E, M extends SortedMap<E, Object>>
            extends AbstractSortedContainer<E, M> {

        private static final long serialVersionUID = -5646956340075624242L;

        protected final P parent;

        protected SortedSubContainer(P parent, M map) { super(map); this.parent = parent; }

        @Override protected Optional<Boolean> isDistinct() {
            return parent.isDistinct(); }
    }
}
