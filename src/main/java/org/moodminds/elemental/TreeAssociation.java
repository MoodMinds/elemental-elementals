package org.moodminds.elemental;

import java.util.Comparator;
import java.util.NavigableMap;
import java.util.TreeMap;
import java.util.stream.Stream;

import static org.moodminds.elemental.Producer.producer;

/**
 * A {@link TreeMap}-powered implementation of the {@link NavigableAssociation} interface.
 *
 * @param <K> the common type of keys
 * @param <V> the common type of values
 */
public class TreeAssociation<K, V> extends AbstractNavigableAssociation<K, V, NavigableMap<K, V>> {

    private static final long serialVersionUID = -701342206877485114L;

    /**
     * Construct the Association with the given {@link KeyValue} pairs vararg.
     *
     * @param kvs the given {@link KeyValue} pairs vararg
     */
    @SafeVarargs
    public TreeAssociation(KeyValue<? extends K, ? extends V>... kvs) {
        this(producer(kvs));
    }

    /**
     * Construct the Association ordered by the given {@link Comparator} and the given {@link KeyValue} array.
     *
     * @param comparator the given ordering {@link Comparator}
     * @param kvs the given {@link KeyValue} array
     */
    @SafeVarargs
    public TreeAssociation(Comparator<? super K> comparator, KeyValue<? extends K, ? extends V>... kvs) {
        this(comparator, producer(kvs));
    }

    /**
     * Construct the object with the given sequential
     * single-threaded {@link Producer} of {@link KeyValue} pairs.
     *
     * @param kvs the given {@link Producer} of {@link KeyValue} pairs
     * @throws NullPointerException if {@code kvs} is {@code null}
     */
    public TreeAssociation(Producer<? extends KeyValue<? extends K, ? extends V>> kvs) {
        this(new TreeMap<>(), kvs);
    }

    /**
     * Construct the Association ordered by the given {@link Comparator}
     * and the given sequential single-threaded {@link Producer} of {@link KeyValue} pairs.
     *
     * @param comparator the given ordering {@link Comparator}
     * @param kvs the given {@link Producer} of {@link KeyValue} pairs
     * @throws NullPointerException if {@code kvs} is {@code null}
     */
    public TreeAssociation(Comparator<? super K> comparator, Producer<? extends KeyValue<? extends K, ? extends V>> kvs) {
        this(new TreeMap<>(comparator), kvs);
    }

    /**
     * Construct the Association with the given {@link Stream} of {@link KeyValue} pairs.
     *
     * @param kvs the given {@link Stream} of {@link KeyValue} pairs
     */
    public TreeAssociation(Stream<? extends KeyValue<? extends K, ? extends V>> kvs) {
        this(new TreeMap<>(), kvs.sequential()::forEach);
    }

    /**
     * Construct the Association ordered by the given {@link Comparator} and the given {@link Stream} of {@link KeyValue} pairs.
     *
     * @param comparator the given ordering {@link Comparator}
     * @param kvs the given {@link Stream} of {@link KeyValue} pairs
     */
    public TreeAssociation(Comparator<? super K> comparator, Stream<? extends KeyValue<? extends K, ? extends V>> kvs) {
        this(new TreeMap<>(comparator), kvs.sequential()::forEach);
    }

    /**
     * Construct the Association with the given {@link Container} of {@link KeyValue} pairs.
     *
     * @param kvs the given {@link Container} of {@link KeyValue} pairs
     */
    public TreeAssociation(Container<? extends KeyValue<? extends K, ? extends V>> kvs) {
        this(new TreeMap<>(), kvs::forEach);
    }

    /**
     * Construct the Association ordered by the given {@link Comparator} and the given {@link Container} of {@link KeyValue} pairs.
     *
     * @param comparator the given ordering {@link Comparator}
     * @param kvs the given {@link Container} of {@link KeyValue} pairs
     */
    public TreeAssociation(Comparator<? super K> comparator, Container<? extends KeyValue<? extends K, ? extends V>> kvs) {
        this(new TreeMap<>(comparator), kvs::forEach);
    }

    /**
     * Construct the Association with the given {@link java.util.Collection} of {@link KeyValue} pairs.
     *
     * @param kvs the given {@link java.util.Collection} of {@link KeyValue} pairs
     */
    public TreeAssociation(java.util.Collection<? extends KeyValue<? extends K, ? extends V>> kvs) {
        this(new TreeMap<>(), kvs::forEach);
    }

    /**
     * Construct the Association ordered by the given {@link Comparator} and the given {@link java.util.Collection} of {@link KeyValue} pairs.
     *
     * @param comparator the given ordering {@link Comparator}
     * @param kvs the given {@link java.util.Collection} of {@link KeyValue} pairs
     */
    public TreeAssociation(Comparator<? super K> comparator, java.util.Collection<? extends KeyValue<? extends K, ? extends V>> kvs) {
        this(new TreeMap<>(comparator), kvs::forEach);
    }

    /**
     * Construct the Association with the given {@link Collection} of {@link KeyValue} pairs.
     *
     * @param kvs the given {@link Collection} of {@link KeyValue} pairs
     */
    public TreeAssociation(Collection<? extends KeyValue<? extends K, ? extends V>> kvs) {
        this((java.util.Collection<? extends KeyValue<? extends K,? extends V>>) kvs);
    }

    /**
     * Construct the Association ordered by the given {@link Comparator} and the given {@link Collection} of {@link KeyValue} pairs.
     *
     * @param comparator the given ordering {@link Comparator}
     * @param kvs the given {@link Collection} of {@link KeyValue} pairs
     */
    public TreeAssociation(Comparator<? super K> comparator, Collection<? extends KeyValue<? extends K, ? extends V>> kvs) {
        this(comparator, (java.util.Collection<? extends KeyValue<? extends K,? extends V>>) kvs);
    }

    /**
     * Construct the Association with the target {@link TreeMap}
     * and sequential single-threaded {@link Producer} of {@link KeyValue} pairs.
     *
     * @param map the target {@link TreeMap}
     * @param kvs the specified {@link Producer} of {@link KeyValue} pairs
     */
    protected TreeAssociation(TreeMap<K, V> map, Producer<? extends KeyValue<? extends K, ? extends V>> kvs) {
        super(map); kvs.provide(kv -> map.put(kv.getKey(), kv.getValue()));
    }

    /**
     * Construct the Association with the target {@link NavigableMap}.
     *
     * @param map the target {@link NavigableMap}
     */
    private TreeAssociation(NavigableMap<K, V> map) {
        super(map);
    }


    /**
     * Return a {@link TreeAssociation} of the given {@link KeyValue} pairs.
     *
     * @param kvs the given {@link KeyValue} pairs
     * @param <K> the type of keys
     * @param <V> the type of values
     * @return a {@link TreeAssociation} of the given {@link KeyValue} pairs
     */
    @SafeVarargs
    public static <K extends Comparable<K>, V> TreeAssociation<K, V> association(KeyValue<? extends K, ? extends V>... kvs) {
        return new TreeAssociation<>(kvs);
    }

    /**
     * Return a {@link TreeAssociation} of the given key {@link Comparator} and {@link KeyValue} pairs.
     *
     * @param comparator the given key {@link Comparator}
     * @param kvs the given {@link KeyValue} pairs
     * @param <K> the type of keys
     * @param <V> the type of values
     * @return a {@link TreeAssociation} of the given key {@link Comparator} and {@link KeyValue} pairs
     */
    @SafeVarargs
    public static <K, V> TreeAssociation<K, V> association(Comparator<? super K> comparator, KeyValue<? extends K, ? extends V>... kvs) {
        return new TreeAssociation<>(comparator, kvs);
    }
}
