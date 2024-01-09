package org.moodminds.elemental;

import java.util.concurrent.ConcurrentNavigableMap;

import static org.moodminds.sneaky.Cast.cast;

/**
 * Wrapping {@link java.util.NavigableMap} implementation of the {@link NavigableMap} interface.
 *
 * @param <K> the type of keys
 * @param <V> the type of values
 * @param <M> the type of wrapped {@link java.util.NavigableMap}
 */
public class WrapNavigableMap<K, V, M extends java.util.NavigableMap<K, V>>
        extends WrapSortedMap<K, V, M> implements NavigableMap<K, V> {

    private static final long serialVersionUID = 6525736188033263416L;

    /**
     * Construct the object with the given {@link M} map.
     *
     * @param wrapped the given {@link M} map to wrap
     */
    protected WrapNavigableMap(M wrapped) { super(wrapped); }

    @Override public K lowerKey(K key) {
        return wrapped.lowerKey(key); }
    @Override public K floorKey(K key) {
        return wrapped.floorKey(key); }
    @Override public K ceilingKey(K key) {
        return wrapped.ceilingKey(key); }
    @Override public K higherKey(K key) {
        return wrapped.higherKey(key); }
    @Override public Map.Entry<K, V> lowerEntry(K key) {
        return keyValue(wrapped.lowerEntry(key)); }
    @Override public Map.Entry<K, V> floorEntry(K key) {
        return keyValue(wrapped.floorEntry(key)); }
    @Override public Map.Entry<K, V> ceilingEntry(K key) {
        return keyValue(wrapped.ceilingEntry(key)); }
    @Override public Map.Entry<K, V> higherEntry(K key) {
        return keyValue(wrapped.higherEntry(key)); }
    @Override public Map.Entry<K, V> firstEntry() {
        return keyValue(wrapped.firstEntry()); }
    @Override public Map.Entry<K, V> lastEntry() {
        return keyValue(wrapped.lastEntry()); }
    @Override public Map.Entry<K, V> pollFirstEntry() {
        return keyValue(wrapped.pollFirstEntry()); }
    @Override public Map.Entry<K, V> pollLastEntry() {
        return keyValue(wrapped.pollLastEntry()); }
    @Override public NavigableMap<K, V> descendingMap() {
        return wrap(wrapped.descendingMap()); }
    @Override public NavigableSet<K> navigableKeySet() {
        return WrapNavigableSet.wrap(wrapped.navigableKeySet()); }
    @Override public NavigableSet<K> descendingKeySet() {
        return WrapNavigableSet.wrap(wrapped.descendingKeySet()); }
    @Override public NavigableMap<K, V> subMap(K fromKey, boolean fromInclusive, K toKey, boolean toInclusive) {
        return wrap(wrapped.subMap(fromKey, fromInclusive, toKey, toInclusive)); }
    @Override public NavigableMap<K, V> headMap(K toKey, boolean inclusive) {
        return wrap(wrapped.headMap(toKey, inclusive)); }
    @Override public NavigableMap<K, V> tailMap(K fromKey, boolean inclusive) {
        return wrap(wrapped.tailMap(fromKey, inclusive)); }


    /**
     * Return wrapping {@link NavigableMap} instance of the given {@link java.util.NavigableMap} map.
     *
     * @param wrapped the given {@link java.util.NavigableMap} map
     * @param <K> the type of keys
     * @param <V> the type of values
     * @return wrapping {@link NavigableMap} instance of the given {@link java.util.NavigableMap} map
     * @throws NullPointerException if the given {@link java.util.NavigableMap} map is {@code null}
     */
    public static <K, V> NavigableMap<K, V> wrap(java.util.NavigableMap<K, V> wrapped) {
        return wrapped instanceof ConcurrentNavigableMap ? WrapConcurrentNavigableMap.wrap(cast(wrapped))
                : new WrapNavigableMap<>(wrapped);
    }
}
