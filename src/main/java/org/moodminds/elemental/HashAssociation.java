package org.moodminds.elemental;

import org.moodminds.sneaky.Cast;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Spliterator;
import java.util.stream.Stream;

import static java.lang.Math.max;
import static java.util.Spliterator.IMMUTABLE;
import static org.moodminds.elemental.Pair.pair;

/**
 * A {@link HashMap}-powered implementation of the {@link Association} interface.
 *
 * @param <K> the common type of keys
 * @param <V> the common type of values
 */
public class HashAssociation<K, V> extends AbstractMapAssociation<K, V, KeyValue<K, V>, Map<K, V>>
        implements RandomMatch {

    private static final long serialVersionUID = 8489296969014822996L;

    /**
     * Construct the object with the given {@link KeyValue} pairs vararg.
     *
     * @param kvs the given {@link KeyValue} pairs varargs
     */
    @SafeVarargs
    public HashAssociation(KeyValue<? extends K, ? extends V>... kvs) {
        this(new HashMap<>(capacity(kvs.length)), Stream.of(kvs));
    }

    /**
     * Construct the object with the given {@link Stream} of {@link KeyValue} pairs.
     *
     * @param kvs the given {@link Stream} of {@link KeyValue} pairs
     */
    public HashAssociation(Stream<? extends KeyValue<? extends K, ? extends V>> kvs) {
        this(new HashMap<>(), kvs);
    }

    /**
     * Construct the object with the given {@link Container} of {@link KeyValue} pairs.
     *
     * @param kvs the given {@link Container} of {@link KeyValue} pairs
     */
    public HashAssociation(Container<? extends KeyValue<? extends K, ? extends V>> kvs) {
        this(new HashMap<>(capacity(kvs.size())), kvs.stream());
    }

    /**
     * Construct the object with the given {@link java.util.Collection} of {@link KeyValue} pairs.
     *
     * @param kvs the given {@link java.util.Collection} of {@link KeyValue} pairs
     */
    public HashAssociation(java.util.Collection<? extends KeyValue<? extends K, ? extends V>> kvs) {
        this(new HashMap<>(capacity(kvs.size())), kvs.stream());
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
     * Construct the object with the target {@link HashMap} and
     * specified {@link Stream} of {@link KeyValue} pairs.
     *
     * @param map the target {@link HashMap}
     * @param kvs the specified {@link Stream} of {@link KeyValue} pairs
     */
    protected HashAssociation(HashMap<K, V> map, Stream<? extends KeyValue<? extends K, ? extends V>> kvs) {
        super(map); kvs.sequential().forEach(kv -> map.put(kv.getKey(), kv.getValue()));
    }

    @Override public Iterator<KeyValue<K, V>> iterator() {
        return WrapIterator.wrap(map.entrySet().iterator(), WrapKeyValue::wrap); }
    @Override public Spliterator<KeyValue<K, V>> spliterator() {
        return WrapSpliterator.wrap(map.entrySet().spliterator(), Cast::cast,
                WrapKeyValue::wrap, ch -> ch | IMMUTABLE); }
    @Override protected Iterator<KeyValue<K, V>> iterator(K key, V value, boolean hasEntry) {
        return hasEntry ? SingleIterator.iterator(pair(key, value)) : EmptyIterator.iterator(); }


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
