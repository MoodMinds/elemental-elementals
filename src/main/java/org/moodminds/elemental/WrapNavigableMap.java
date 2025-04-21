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
     * Construct the object with the given {@link M map} .
     *
     * @param map the given {@link M map}  to wrap
     */
    protected WrapNavigableMap(M map) { super(map); }

    @Override public K lowerKey(K key) {
        return map.lowerKey(key); }
    @Override public K floorKey(K key) {
        return map.floorKey(key); }
    @Override public K ceilingKey(K key) {
        return map.ceilingKey(key); }
    @Override public K higherKey(K key) {
        return map.higherKey(key); }
    @Override public Map.Entry<K, V> lowerEntry(K key) {
        return WrapEntry.wrap(map.lowerEntry(key)); }
    @Override public Map.Entry<K, V> floorEntry(K key) {
        return WrapEntry.wrap(map.floorEntry(key)); }
    @Override public Map.Entry<K, V> ceilingEntry(K key) {
        return WrapEntry.wrap(map.ceilingEntry(key)); }
    @Override public Map.Entry<K, V> higherEntry(K key) {
        return WrapEntry.wrap(map.higherEntry(key)); }
    @Override public Map.Entry<K, V> firstEntry() {
        return WrapEntry.wrap(map.firstEntry()); }
    @Override public Map.Entry<K, V> lastEntry() {
        return WrapEntry.wrap(map.lastEntry()); }
    @Override public Map.Entry<K, V> pollFirstEntry() {
        return WrapEntry.wrap(map.pollFirstEntry()); }
    @Override public Map.Entry<K, V> pollLastEntry() {
        return WrapEntry.wrap(map.pollLastEntry()); }
    @Override public NavigableMap<K, V> descendingMap() {
        return wrap(map.descendingMap()); }
    @Override public NavigableSet<K> navigableKeySet() {
        return WrapNavigableSet.wrap(map.navigableKeySet()); }
    @Override public NavigableSet<K> descendingKeySet() {
        return WrapNavigableSet.wrap(map.descendingKeySet()); }
    @Override public NavigableMap<K, V> subMap(K fromKey, boolean fromInclusive, K toKey, boolean toInclusive) {
        return wrap(map.subMap(fromKey, fromInclusive, toKey, toInclusive)); }
    @Override public NavigableMap<K, V> headMap(K toKey, boolean inclusive) {
        return wrap(map.headMap(toKey, inclusive)); }
    @Override public NavigableMap<K, V> tailMap(K fromKey, boolean inclusive) {
        return wrap(map.tailMap(fromKey, inclusive)); }


    /**
     * Return wrapping {@link NavigableMap} instance of the given {@link java.util.NavigableMap} map.
     *
     * @param map the given {@link java.util.NavigableMap} map
     * @param <K> the type of keys
     * @param <V> the type of values
     * @return wrapping {@link NavigableMap} instance of the given {@link java.util.NavigableMap} map
     * @throws NullPointerException if the given {@link java.util.NavigableMap} map is {@code null}
     */
    public static <K, V> NavigableMap<K, V> wrap(java.util.NavigableMap<K, V> map) {
        return map instanceof ConcurrentNavigableMap ? WrapConcurrentNavigableMap.wrap(cast(map))
                : new WrapNavigableMap<>(map);
    }
}
