package org.moodminds.elemental;

import java.util.Comparator;
import java.util.Iterator;
import java.util.Map;
import java.util.SortedMap;

import static org.moodminds.sneaky.Cast.cast;

/**
 * A template implementation of the {@link SortedAssociation} interface,
 * which is powered by an internal {@link SortedMap}.
 *
 * @param <K> the type of keys
 * @param <V> the type of values
 * @param <M> the type of wrapped {@link SortedMap}
 */
public abstract class AbstractSortedAssociation<K, V, M extends SortedMap<K, V>>
        extends AbstractMapAssociation<K, V, KeyValue<K, V>, M>
        implements SortedAssociation<K, V, KeyValue<K, V>> {

    private static final long serialVersionUID = -7434083876349223128L;

    /**
     * Construct the object with the given {@link M map}.
     *
     * @param map the given {@link M map}
     */
    protected AbstractSortedAssociation(M map) {
        super(map);
    }

    @Override public Comparator<? super K> comparator() { return map.comparator(); }
    @Override public K firstKey() { return map.firstKey(); }
    @Override public K lastKey() { return map.lastKey(); }

    @Override public SortedAssociation<K, V, ?> sub(K fromKey, K toKey) {
        return new SortedSubAssociation<>(map.subMap(fromKey, toKey)); }
    @Override public SortedAssociation<K, V, ?> head(K toKey) {
        return new SortedSubAssociation<>(map.headMap(toKey)); }
    @Override public SortedAssociation<K, V, ?> tail(K fromKey) {
        return new SortedSubAssociation<>(map.tailMap(fromKey)); }

    @Override protected Iterator<KeyValue<K, V>> iterator(K key, V value, boolean present) {
        return OptionalIterator.iterator(() -> new AbstractKeyValue<K, V>() {
            @Override public K getKey() { return key; }
            @Override public V getValue() { return value; }
        }, present); }
    @Override protected KeyValue<K, V> entry(Map.Entry<K, V> entry) {
        return WrapKeyValue.wrap(entry); }
    @Override protected Map.Entry<K, V> entry(KeyValue<K, V> entry) {
        return cast(entry); }


    /**
     * Sub-association extension of the {@link AbstractSortedAssociation}.
     *
     * @param <K> the type of keys
     * @param <V> the type of values
     * @param <M> the type of wrapped {@link SortedMap}
     */
    protected static class SortedSubAssociation<K, V, M extends SortedMap<K, V>>
            extends AbstractSortedAssociation<K, V, M> {

        private static final long serialVersionUID = 3966799727744616965L;

        protected SortedSubAssociation(M map) { super(map); }
    }
}
