package org.moodminds.elemental;

import java.util.Comparator;
import java.util.NavigableMap;

import static org.moodminds.sneaky.Cast.cast;

/**
 * Wrapping {@link java.util.SortedMap} implementation of the {@link SortedMap} interface.
 *
 * @param <K> the type of keys
 * @param <V> the type of values
 * @param <M> the type of wrapped {@link java.util.SortedMap}
 */
public class WrapSortedMap<K, V, M extends java.util.SortedMap<K, V>> extends WrapMap<K, V, M>
        implements SortedMap<K, V> {

    private static final long serialVersionUID = 1280332418334937482L;

    /**
     * Construct the object with the given {@link M} map.
     *
     * @param wrapped the given {@link M} map to wrap
     */
    protected WrapSortedMap(M wrapped) {
        super(wrapped);
    }

    @Override public Comparator<? super K> comparator() {
        return wrapped.comparator(); }
    @Override public K firstKey() {
        return wrapped.firstKey(); }
    @Override public K lastKey() {
        return wrapped.lastKey(); }
    @Override public SortedMap<K, V> subMap(K fromKey, K toKey) {
        return wrap(wrapped.subMap(fromKey, toKey)); }
    @Override public SortedMap<K, V> headMap(K toKey) {
        return wrap(wrapped.headMap(toKey)); }
    @Override public SortedMap<K, V> tailMap(K fromKey) {
        return wrap(wrapped.tailMap(fromKey)); }


    /**
     * Return wrapping {@link SortedMap} instance of the given {@link java.util.SortedMap} map.
     *
     * @param wrapped the given {@link java.util.SortedMap} map
     * @param <K> the type of keys
     * @param <V> the type of values
     * @return wrapping {@link SortedMap} instance of the given {@link java.util.SortedMap} map
     * @throws NullPointerException if the given {@link java.util.SortedMap} map is {@code null}
     */
    public static <K, V> SortedMap<K, V> wrap(java.util.SortedMap<K, V> wrapped) {
        return wrapped instanceof NavigableMap ? WrapNavigableMap.wrap(cast(wrapped)) : new WrapSortedMap<>(wrapped);
    }
}
