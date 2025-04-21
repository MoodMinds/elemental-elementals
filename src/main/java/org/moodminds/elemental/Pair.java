package org.moodminds.elemental;

import java.io.Serializable;

/**
 * A simple {@link KeyValue} pair implementation.
 *
 * @param <K> the type of the key
 * @param <V> the type of the value
 */
public class Pair<K, V> extends AbstractKeyValue<K, V> implements Serializable {

    private static final long serialVersionUID = -8519193798498950277L;

    /**
     * Key holding field.
     */
    private final K key;

    /**
     * Value holding field.
     */
    private final V value;

    /**
     * Construct the object with the specified key and value.
     *
     * @param key the specified key
     * @param value the specified value
     */
    public Pair(K key, V value) {
        this.key = key; this.value = value;
    }

    /**
     * Construct the object with the specified {@link KeyValue}.
     *
     * @param kv the specified {@link KeyValue}
     */
    public Pair(KeyValue<? extends K, ? extends V> kv) {
        this(kv.getKey(), kv.getValue());
    }

    /**
     * {@inheritDoc}
     *
     * @return {@inheritDoc}
     */
    @Override
    public K getKey() {
        return key;
    }

    /**
     * {@inheritDoc}
     *
     * @return {@inheritDoc}
     */
    @Override
    public V getValue() {
        return value;
    }


    /**
     * Return a simple {@link Pair} with the specified key and value.
     *
     * @param key the specified key
     * @param value the specified value
     * @param <K> the type of key
     * @param <V> the type of value
     * @return a simple {@link Pair} with the specified key and value
     */
    public static <K, V> Pair<K, V> pair(K key, V value) {
        return new Pair<>(key, value);
    }
}
