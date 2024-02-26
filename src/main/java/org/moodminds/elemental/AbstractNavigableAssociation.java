package org.moodminds.elemental;

import java.util.NavigableMap;

import static org.moodminds.elemental.WrapKeyValue.wrap;

/**
 * Template implementation of the {@link NavigableAssociation} interface,
 * which is powered by an internal {@link NavigableMap}.
 *
 * @param <K> the type of keys
 * @param <V> the type of values
 * @param <M> the type of wrapped {@link NavigableMap}
 */
public abstract class AbstractNavigableAssociation<K, V, M extends NavigableMap<K, V>>
        extends AbstractSortedAssociation<K, V, M>
        implements NavigableAssociation<K, V, KeyValue<K, V>> {

    private static final long serialVersionUID = -4725440864433861191L;

    /**
     * Construct the object with the given {@link M} map.
     *
     * @param map the given {@link M} map to wrap
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
}
