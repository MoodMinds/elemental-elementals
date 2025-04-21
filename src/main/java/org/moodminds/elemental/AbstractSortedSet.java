package org.moodminds.elemental;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Comparator;
import java.util.Iterator;
import java.util.SortedMap;

import static org.moodminds.sneaky.Cast.cast;

/**
 * A template implementation of the {@link SortedSet} interface,
 * which is powered by an internal {@link java.util.SortedMap}.
 *
 * @param <E> the element type
 * @param <M> the type of the internal {@link SortedMap}
 */
public abstract class AbstractSortedSet<E, M extends SortedMap<E, E>>
        extends AbstractMapSet<E, M> implements SortedSet<E> {

    private static final long serialVersionUID = 114471945549088766L;

    /**
     * Construct the object with the given {@link M map} .
     *
     * @param map the given {@link M map} 
     */
    protected AbstractSortedSet(M map) {
        super(map);
    }

    /**
     * Construct the object with the given {@link M} and sequential single-threaded {@link Producer} of elements.
     *
     * @param map the given {@link M}
     * @param elements the given sequential single-threaded {@link Producer} of elements
     */
    protected AbstractSortedSet(M map, Producer<? extends E> elements) {
        super(map, elements);
    }

    @Override public Comparator<? super E> comparator() {
        return map.comparator(); }

    @Override public Iterator<E> firstAll() {
        return contains() ? iterator(map.firstKey(), true) : iterator(null, false); }
    @Override public Iterator<E> lastAll() {
        return contains() ? iterator(map.lastKey(), true) : iterator(null, false); }

    @Override public SortedSet<E> subSet(E fromElement, E toElement) {
        return new SortedSubSet<>(map.subMap(fromElement, toElement)); }
    @Override public SortedSet<E> headSet(E toElement) {
        return new SortedSubSet<>(map.headMap(toElement)); }
    @Override public SortedSet<E> tailSet(E fromElement) {
        return new SortedSubSet<>(map.tailMap(fromElement)); }

    @Override protected void serialize(ObjectOutputStream output) throws Exception {
        output.writeObject(map); }
    @Override protected void deserialize(ObjectInputStream input) throws Exception {
        map = cast(input.readObject()); }


    /**
     * Sub-set extension of the {@link AbstractSortedSet}.
     *
     * @param <E> the element type
     * @param <M> the type of the internal {@link SortedMap}
     */
    protected static class SortedSubSet<E, M extends SortedMap<E, E>> extends AbstractSortedSet<E, M> {

        private static final long serialVersionUID = -6882257609001927320L;

        protected SortedSubSet(M map) { super(map); }
    }
}
