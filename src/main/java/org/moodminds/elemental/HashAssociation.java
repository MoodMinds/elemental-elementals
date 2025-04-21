package org.moodminds.elemental;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Stream;

import static java.lang.Math.max;
import static org.moodminds.elemental.Producer.producer;
import static org.moodminds.sneaky.Cast.cast;

/**
 * A {@link HashMap}-powered implementation of the {@link Association} interface.
 *
 * @param <K> the common type of keys
 * @param <V> the common type of values
 */
public class HashAssociation<K, V> extends AbstractMapAssociation<K, V, KeyValue<K, V>, Map<K, V>> {

    private static final long serialVersionUID = 8489296969014822996L;

    /**
     * Construct the object with the given {@link KeyValue} pairs vararg.
     *
     * @param kvs the given {@link KeyValue} pairs varargs
     */
    @SafeVarargs
    public HashAssociation(KeyValue<? extends K, ? extends V>... kvs) {
        this(new HashMap<>(capacity(kvs.length)), producer(kvs));
    }

    /**
     * Construct the object with the given sequential
     * single-threaded {@link Producer} of {@link KeyValue} pairs.
     *
     * @param kvs the given {@link Producer} of {@link KeyValue} pairs
     * @throws NullPointerException if {@code kvs} is {@code null}
     */
    public HashAssociation(Producer<? extends KeyValue<? extends K, ? extends V>> kvs) {
        this(new HashMap<>(), kvs);
    }

    /**
     * Construct the object with the given {@link Stream} of {@link KeyValue} pairs.
     *
     * @param kvs the given {@link Stream} of {@link KeyValue} pairs
     */
    public HashAssociation(Stream<? extends KeyValue<? extends K, ? extends V>> kvs) {
        this(new HashMap<>(), kvs.sequential()::forEach);
    }

    /**
     * Construct the object with the given {@link Container} of {@link KeyValue} pairs.
     *
     * @param kvs the given {@link Container} of {@link KeyValue} pairs
     */
    public HashAssociation(Container<? extends KeyValue<? extends K, ? extends V>> kvs) {
        this(new HashMap<>(capacity(kvs.size())), kvs::forEach);
    }

    /**
     * Construct the object with the given {@link java.util.Collection} of {@link KeyValue} pairs.
     *
     * @param kvs the given {@link java.util.Collection} of {@link KeyValue} pairs
     */
    public HashAssociation(java.util.Collection<? extends KeyValue<? extends K, ? extends V>> kvs) {
        this(new HashMap<>(capacity(kvs.size())), kvs::forEach);
    }

    /**
     * Construct the object with the given {@link Collection} of {@link KeyValue} pairs.
     *
     * @param kvs the given {@link Collection} of {@link KeyValue} pairs
     */
    public HashAssociation(Collection<? extends KeyValue<? extends K, ? extends V>> kvs) {
        this((java.util.Collection<? extends KeyValue<? extends K,? extends V>>) kvs);
    }

    /**
     * Construct the object with the target {@link HashMap}
     * and sequential single-threaded {@link Producer} of {@link KeyValue} pairs.
     *
     * @param map the target {@link HashMap}
     * @param kvs the specified {@link Producer} of {@link KeyValue} pairs
     */
    protected HashAssociation(HashMap<K, V> map, Producer<? extends KeyValue<? extends K, ? extends V>> kvs) {
        super(map); kvs.provide(kv -> map.put(kv.getKey(), kv.getValue()));
    }

    @Override protected Iterator<KeyValue<K, V>> iterator(K key, V value, boolean present) {
        return OptionalIterator.iterator(() -> new AbstractKeyValue<K, V>() {
            @Override public K getKey() { return key; }
            @Override public V getValue() { return value; }
        }, present); }
    @Override protected KeyValue<K, V> entry(Entry<K, V> entry) {
        return WrapKeyValue.wrap(entry); }
    @Override protected Entry<K, V> entry(KeyValue<K, V> entry) {
        return cast(entry); }

    /**
     * Calculate the initial {@link HashMap} capacity basing on the given size.
     *
     * @param size the given size
     * @return the initial {@link HashMap} capacity basing on the given size
     */
    protected static int capacity(int size) {
        return max((int) (size/.75f) + 1, 16);
    }

    /**
     * Return a {@link HashAssociation} of the given {@link KeyValue} pairs vararg.
     *
     * @param kvs the given {@link KeyValue} pairs vararg
     * @param <K> the type of keys
     * @param <V> the type of values
     * @return a {@link HashAssociation} of the given {@link KeyValue} pairs vararg
     */
    @SafeVarargs
    public static <K, V> HashAssociation<K, V> association(KeyValue<? extends K, ? extends V>... kvs) {
        return new HashAssociation<>(kvs);
    }
}
