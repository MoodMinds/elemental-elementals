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
     * Construct the object with the given {@link M map} .
     *
     * @param map the given {@link M map}  to wrap
     */
    protected WrapSortedMap(M map) {
        super(map);
    }

    @Override public Comparator<? super K> comparator() {
        return map.comparator(); }
    @Override public K firstKey() {
        return map.firstKey(); }
    @Override public K lastKey() {
        return map.lastKey(); }
    @Override public SortedMap<K, V> subMap(K fromKey, K toKey) {
        return wrap(map.subMap(fromKey, toKey)); }
    @Override public SortedMap<K, V> headMap(K toKey) {
        return wrap(map.headMap(toKey)); }
    @Override public SortedMap<K, V> tailMap(K fromKey) {
        return wrap(map.tailMap(fromKey)); }


    /**
     * Return wrapping {@link SortedMap} instance of the given {@link java.util.SortedMap} map.
     *
     * @param map the given {@link java.util.SortedMap} map
     * @param <K> the type of keys
     * @param <V> the type of values
     * @return wrapping {@link SortedMap} instance of the given {@link java.util.SortedMap} map
     * @throws NullPointerException if the given {@link java.util.SortedMap} map is {@code null}
     */
    public static <K, V> SortedMap<K, V> wrap(java.util.SortedMap<K, V> map) {
        return map instanceof NavigableMap ? WrapNavigableMap.wrap(cast(map)) : new WrapSortedMap<>(map);
    }
}
