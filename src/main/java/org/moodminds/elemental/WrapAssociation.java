package org.moodminds.elemental;

import org.moodminds.elemental.AbstractAssociation.AbstractImmutableAssociation;

import java.io.Serializable;
import java.util.Iterator;
import java.util.Map;
import java.util.SortedMap;
import java.util.Spliterator;
import java.util.stream.Stream;

import static java.util.Objects.requireNonNull;
import static java.util.Spliterator.IMMUTABLE;
import static org.moodminds.sneaky.Cast.cast;

/**
 * Wrapping {@link java.util.Map} implementation of the {@link Association} interface.
 *
 * @param <K> the type of keys
 * @param <V> the type of values
 * @param <M> the type of wrapped {@link java.util.Map}
 */
public class WrapAssociation<K, V, M extends Map<K, V>>
        extends AbstractImmutableAssociation<K, V> implements Serializable {

    private static final long serialVersionUID = 4349202813676022292L;

    protected final M wrapped;

    /**
     * Construct the object with the given {@link M} map.
     *
     * @param wrapped the given {@link M} map to wrap
     */
    public WrapAssociation(M wrapped) {
        this.wrapped = requireNonNull(wrapped);
    }

    @Override public int size() {
        return wrapped.size(); }
    @Override public <R extends V> R get(Object key) {
        return cast(wrapped.get(key)); }
    @Override public boolean contains(Object o) {
        return wrapped.entrySet().contains(o); }
    @Override public boolean containsKey(Object key) {
        return wrapped.containsKey(key); }
    @Override public boolean containsValue(Object value) {
        return wrapped.containsValue(value); }
    @Override public V getOrDefault(Object key, V defaultValue) {
        return wrapped.getOrDefault(key, defaultValue); }
    @Override public Iterator<KeyValue<K, V>> iterator() {
        return WrapIterator.wrap(wrapped.entrySet().iterator(), this::keyValue); }
    @Override public Spliterator<KeyValue<K, V>> spliterator() {
        return WrapSpliterator.wrap(wrapped.entrySet().spliterator(), this::keyValue, this::keyValue, ch -> ch | IMMUTABLE); }
    @Override public Stream<KeyValue<K, V>> stream() {
        return wrapped.entrySet().stream().map(this::keyValue); }
    @Override public Stream<KeyValue<K, V>> parallelStream() {
        return wrapped.entrySet().parallelStream().map(this::keyValue); }

    protected KeyValue<K, V> keyValue(Map.Entry<K, V> entry) {
        return new AbstractKeyValue<K, V>() {
            @Override public K getKey() {
                return entry.getKey(); }
            @Override public V getValue() {
                return entry.getValue(); }
            @Override public V setValue(V value) {
                throw new UnsupportedOperationException(); }
        };
    }

    protected Map.Entry<K, V> keyValue(KeyValue<K, V> kv) {
        return new AbstractKeyValue<K, V>() {
            @Override public K getKey() {
                return kv.getKey(); }
            @Override public V getValue() {
                return kv.getValue(); }
            @Override public V setValue(V value) {
                throw new UnsupportedOperationException(); }
        };
    }


    /**
     * Return wrapping {@link Association} instance of the given {@link java.util.Map} map.
     *
     * @param wrapped the given {@link java.util.Map} map
     * @param <K> the type of keys
     * @param <V> the type of values
     * @return wrapping {@link Association} instance of the given {@link java.util.Map} map
     * @throws NullPointerException if the given {@link Map} map is {@code null}
     */
    public static <K, V> Association<K, V, ?> wrap(Map<K, V> wrapped) {
        return wrapped instanceof SortedMap ? WrapSortedAssociation.wrap(cast(wrapped))
                : new WrapAssociation<>(wrapped);
    }
}
