package org.moodminds.elemental;

import java.util.NavigableMap;

/**
 * Wrapping {@link java.util.NavigableMap} implementation of the {@link NavigableAssociation} interface.
 *
 * @param <K> the type of keys
 * @param <V> the type of values
 * @param <M> the type of wrapped {@link java.util.NavigableMap}
 */
public class WrapNavigableAssociation<K, V, M extends NavigableMap<K, V>> extends WrapSortedAssociation<K, V, M>
        implements NavigableAssociation<K, V, KeyValue<K, V>> {

    private static final long serialVersionUID = 8723477727145185198L;

    /**
     * Construct the object with the given {@link M} map.
     *
     * @param wrapped the given {@link M} map to wrap
     */
    protected WrapNavigableAssociation(M wrapped) {
        super(wrapped);
    }

    @Override public KeyValue<K, V> lowerEntry(K key) {
        return keyValue(wrapped.lowerEntry(key)); }
    @Override public K lowerKey(K key) {
        return wrapped.lowerKey(key); }
    @Override public KeyValue<K, V> floorEntry(K key) {
        return keyValue(wrapped.floorEntry(key)); }
    @Override public K floorKey(K key) {
        return wrapped.floorKey(key); }
    @Override public KeyValue<K, V> ceilingEntry(K key) {
        return keyValue(wrapped.ceilingEntry(key)); }
    @Override public K ceilingKey(K key) {
        return wrapped.ceilingKey(key); }
    @Override public KeyValue<K, V> higherEntry(K key) {
        return keyValue(wrapped.higherEntry(key)); }
    @Override public K higherKey(K key) {
        return wrapped.higherKey(key); }
    @Override public KeyValue<K, V> firstEntry() {
        return keyValue(wrapped.firstEntry()); }
    @Override public KeyValue<K, V> lastEntry() {
        return keyValue(wrapped.lastEntry()); }

    @Override public NavigableAssociation<K, V, ?> descending() {
        return wrap(wrapped.descendingMap()); }
    @Override public NavigableAssociation<K, V, ?> subAssociation(K fromKey, boolean fromInclusive, K toKey,   boolean toInclusive) {
        return wrap(wrapped.subMap(fromKey, fromInclusive, toKey, toInclusive)); }
    @Override public NavigableAssociation<K, V, ?> headAssociation(K toKey, boolean inclusive) {
        return wrap(wrapped.headMap(toKey, inclusive));}
    @Override public NavigableAssociation<K, V, ?> tailAssociation(K fromKey, boolean inclusive) {
        return wrap(wrapped.tailMap(fromKey, inclusive)); }


    /**
     * Return wrapping {@link NavigableAssociation} instance of the given {@link java.util.NavigableMap} map.
     *
     * @param wrapped the given {@link java.util.NavigableMap} map
     * @param <K> the type of keys
     * @param <V> the type of values
     * @return wrapping {@link NavigableAssociation} instance of the given {@link java.util.NavigableMap} map
     * @throws NullPointerException if the given {@link java.util.NavigableMap} map is {@code null}
     */
    public static <K, V> NavigableAssociation<K, V, ?> wrap(NavigableMap<K, V> wrapped) {
        return new WrapNavigableAssociation<>(wrapped);
    }
}
