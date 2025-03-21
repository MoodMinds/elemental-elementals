package org.moodminds.elemental;

import java.util.NavigableMap;

import static org.moodminds.elemental.WrapKeyValue.wrap;

/**
 * A template implementation of the {@link NavigableAssociation} interface,
 * which is powered by an internal {@link NavigableMap}.
 *
 * @param <K> the type of keys
 * @param <V> the type of values
 * @param <M> the type of wrapped {@link NavigableMap}
 */
public abstract class AbstractNavigableAssociation<K, V, M extends NavigableMap<K, V>>
        extends AbstractSortedAssociation<K, V, M> implements NavigableAssociation<K, V, KeyValue<K, V>> {

    private static final long serialVersionUID = -4725440864433861191L;

    /**
     * Construct the object with the given {@link M map}.
     *
     * @param map the given {@link M map}
     */
    protected AbstractNavigableAssociation(M map) {
        super(map);
    }

    @Override public KeyValue<K, V> lowerEntry(K key) {
        return wrap(map.lowerEntry(key)); }
    @Override public K lowerKey(K key) {
        return map.lowerKey(key); }
    @Override public KeyValue<K, V> floorEntry(K key) {
        return wrap(map.floorEntry(key)); }
    @Override public K floorKey(K key) {
        return map.floorKey(key); }
    @Override public KeyValue<K, V> ceilingEntry(K key) {
        return wrap(map.ceilingEntry(key)); }
    @Override public K ceilingKey(K key) {
        return map.ceilingKey(key); }
    @Override public KeyValue<K, V> higherEntry(K key) {
        return wrap(map.higherEntry(key)); }
    @Override public K higherKey(K key) {
        return map.higherKey(key); }
    @Override public KeyValue<K, V> firstEntry() {
        return wrap(map.firstEntry()); }
    @Override public KeyValue<K, V> lastEntry() {
        return wrap(map.lastEntry()); }

    @Override public NavigableAssociation<K, V, ?> descending() {
        return new NavigableSubAssociation<>(map.descendingMap()); }
    @Override public NavigableAssociation<K, V, ?> sub(K fromKey, boolean fromInclusive, K toKey, boolean toInclusive) {
        return new NavigableSubAssociation<>(map.subMap(fromKey, fromInclusive, toKey, toInclusive)); }
    @Override public NavigableAssociation<K, V, ?> head(K toKey, boolean inclusive) {
        return new NavigableSubAssociation<>(map.headMap(toKey, inclusive));}
    @Override public NavigableAssociation<K, V, ?> tail(K fromKey, boolean inclusive) {
        return new NavigableSubAssociation<>(map.tailMap(fromKey, inclusive)); }


    /**
     * Sub-association extension of the {@link AbstractNavigableAssociation}.
     *
     * @param <K> the type of keys
     * @param <V> the type of values
     */
    protected static class NavigableSubAssociation<K, V, M extends NavigableMap<K, V>>
            extends AbstractNavigableAssociation<K, V, M> {

        private static final long serialVersionUID = 3966799727744616965L;

        protected NavigableSubAssociation(M map) { super(map); }
    }
}
