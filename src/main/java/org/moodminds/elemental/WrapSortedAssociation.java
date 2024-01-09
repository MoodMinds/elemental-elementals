package org.moodminds.elemental;

import java.util.Comparator;
import java.util.NavigableMap;
import java.util.SortedMap;

import static org.moodminds.sneaky.Cast.cast;

/**
 * Wrapping {@link java.util.SortedMap} implementation of the {@link SortedAssociation} interface.
 *
 * @param <K> the type of keys
 * @param <V> the type of values
 * @param <M> the type of wrapped {@link java.util.SortedMap}
 */
public class WrapSortedAssociation<K, V, M extends SortedMap<K, V>> extends WrapAssociation<K, V, M>
        implements SortedAssociation<K, V, KeyValue<K, V>> {

    private static final long serialVersionUID = 2701133871934466876L;

    /**
     * Construct the object with the given {@link M} map.
     *
     * @param wrapped the given {@link M} map to wrap
     */
    protected WrapSortedAssociation(M wrapped) {
        super(wrapped);
    }

    @Override public Comparator<? super K> comparator() {
        return wrapped.comparator(); }
    @Override public K firstKey() {
        return wrapped.firstKey(); }
    @Override public K lastKey() {
        return wrapped.lastKey(); }
    @Override public SortedAssociation<K, V, ?> sub(K fromKey, K toKey) {
        return wrap(wrapped.subMap(fromKey, toKey)); }
    @Override public SortedAssociation<K, V, ?> head(K toKey) {
        return wrap(wrapped.headMap(toKey)); }
    @Override public SortedAssociation<K, V, ?> tail(K fromKey) {
        return wrap(wrapped.tailMap(fromKey)); }


    /**
     * Return wrapping {@link SortedAssociation} instance of the given {@link SortedMap} map.
     *
     * @param wrapped the given {@link java.util.SortedMap} map
     * @param <K> the type of keys
     * @param <V> the type of values
     * @return wrapping {@link SortedAssociation} instance of the given {@link SortedMap} map
     * @throws NullPointerException if the given {@link SortedMap} map is {@code null}
     */
    public static <K, V> SortedAssociation<K, V, ?> wrap(SortedMap<K, V> wrapped) {
        return wrapped instanceof NavigableMap ? WrapNavigableAssociation.wrap(cast(wrapped))
                : new WrapSortedAssociation<>(wrapped);
    }
}
