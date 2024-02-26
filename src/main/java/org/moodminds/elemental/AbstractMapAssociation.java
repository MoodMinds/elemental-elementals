package org.moodminds.elemental;

import java.io.Serializable;
import java.util.Map;

import static org.moodminds.sneaky.Cast.cast;

/**
 * Template implementation of the {@link Association} interface,
 * which is powered by an internal {@link Map}.
 *
 * @param <K> the type of keys
 * @param <V> the type of values
 * @param <KV> the type of {@link KeyValue} entries
 * @param <M> the type of wrapped {@link Map}
 */
public abstract class AbstractMapAssociation<K, V, KV extends KeyValue<K, V>, M extends Map<K, V>>
        extends AbstractAssociation<K, V, KV> implements Serializable {

    private static final long serialVersionUID = -4533400046102874586L;

    /**
     * Backing {@link M map} holder field.
     */
    protected final M map;

    /**
     * Construct the object with the given {@link M} map.
     *
     * @param map the given {@link M} map to wrap
     */
    protected AbstractMapAssociation(M map) {
        this.map = map;
    }

    @Override public int size() {
        return map.size(); }
    @Override public <R extends V> R get(Object key) {
        return cast(map.get(key)); }
    @Override public boolean contains(Object o) {
        return map.entrySet().contains(o); }
    @Override public boolean containsKey(Object key) {
        return map.containsKey(key); }
    @Override public boolean containsValue(Object value) {
        return map.containsValue(value); }
    @Override public V getOrDefault(Object key, V defaultValue) {
        return map.getOrDefault(key, defaultValue); }
}
