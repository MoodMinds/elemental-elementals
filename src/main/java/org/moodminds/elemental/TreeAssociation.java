package org.moodminds.elemental;

import java.util.Comparator;
import java.util.NavigableMap;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.stream.Stream;

/**
 * A {@link TreeMap}-powered implementation of the {@link NavigableAssociation} interface.
 *
 * @param <K> the common type of keys
 * @param <V> the common type of values
 */
public class TreeAssociation<K, V> extends AbstractNavigableAssociation<K, V, NavigableMap<K, V>>
        implements RandomMatch {

    private static final long serialVersionUID = -701342206877485114L;

    /**
     * Construct the Association with the given {@link KeyValue} pairs vararg.
     *
     * @param kvs the given {@link KeyValue} pairs vararg
     */
    @SafeVarargs
    public TreeAssociation(KeyValue<? extends K, ? extends V>... kvs) {
        this(new TreeMap<>(), Stream.of(kvs));
    }

    /**
     * Construct the Association ordered by the given {@link Comparator} and the given {@link KeyValue} array.
     *
     * @param comparator the given ordering {@link Comparator}
     * @param kvs the given {@link KeyValue} array
     */
    @SafeVarargs
    public TreeAssociation(Comparator<? super K> comparator, KeyValue<? extends K, ? extends V>... kvs) {
        this(new TreeMap<>(comparator), Stream.of(kvs));
    }

    /**
     * Construct the Association with the given {@link Stream} of {@link KeyValue} pairs.
     *
     * @param kvs the given {@link Stream} of {@link KeyValue} pairs
     */
    public TreeAssociation(Stream<? extends KeyValue<? extends K, ? extends V>> kvs) {
        this(new TreeMap<>(), kvs);
    }

    /**
     * Construct the Association ordered by the given {@link Comparator} and the given {@link Stream} of {@link KeyValue} pairs.
     *
     * @param comparator the given ordering {@link Comparator}
     * @param kvs the given {@link Stream} of {@link KeyValue} pairs
     */
    public TreeAssociation(Comparator<? super K> comparator, Stream<? extends KeyValue<? extends K, ? extends V>> kvs) {
        this(new TreeMap<>(comparator), kvs);
    }

    /**
     * Construct the Association with the given {@link Container} of {@link KeyValue} pairs.
     *
     * @param kvs the given {@link Container} of {@link KeyValue} pairs
     */
    public TreeAssociation(Container<? extends KeyValue<? extends K, ? extends V>> kvs) {
        this(new TreeMap<>(), kvs.stream());
    }

    /**
     * Construct the Association ordered by the given {@link Comparator} and the given {@link Container} of {@link KeyValue} pairs.
     *
     * @param comparator the given ordering {@link Comparator}
     * @param kvs the given {@link Container} of {@link KeyValue} pairs
     */
    public TreeAssociation(Comparator<? super K> comparator, Container<? extends KeyValue<? extends K, ? extends V>> kvs) {
        this(new TreeMap<>(comparator), kvs.stream());
    }

    /**
     * Construct the Association with the given {@link java.util.Collection} of {@link KeyValue} pairs.
     *
     * @param kvs the given {@link java.util.Collection} of {@link KeyValue} pairs
     */
    public TreeAssociation(java.util.Collection<? extends KeyValue<? extends K, ? extends V>> kvs) {
        this(new TreeMap<>(), kvs.stream());
    }

    /**
     * Construct the Association ordered by the given {@link Comparator} and the given {@link java.util.Collection} of {@link KeyValue} pairs.
     *
     * @param comparator the given ordering {@link Comparator}
     * @param kvs the given {@link java.util.Collection} of {@link KeyValue} pairs
     */
    public TreeAssociation(Comparator<? super K> comparator, java.util.Collection<? extends KeyValue<? extends K, ? extends V>> kvs) {
        this(new TreeMap<>(comparator), kvs.stream());
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
     * and specified {@link Stream} of {@link KeyValue} pairs.
     *
     * @param map the target {@link TreeMap}
     * @param kvs the specified {@link Stream} of {@link KeyValue} pairs
     */
    protected TreeAssociation(TreeMap<K, V> map, Stream<? extends KeyValue<? extends K, ? extends V>> kvs) {
        super(map); kvs.sequential().forEach(kv -> map.put(kv.getKey(), kv.getValue()));
    }

    /**
     * Construct the Association with the target {@link NavigableMap}.
     *
     * @param map the target {@link NavigableMap}
     */
    private TreeAssociation(NavigableMap<K, V> map) {
        super(map);
    }

    @Override public SortedAssociation<K, V, ?> sub(K fromKey, K toKey) {
        return new SortedSubAssociation<>(map.subMap(fromKey, toKey)); }
    @Override public SortedAssociation<K, V, ?> head(K toKey) {
        return new SortedSubAssociation<>(map.headMap(toKey)); }
    @Override public SortedAssociation<K, V, ?> tail(K fromKey) {
        return new SortedSubAssociation<>(map.tailMap(fromKey)); }

    @Override public NavigableAssociation<K, V, ?> descending() {
        return new TreeAssociation<>(map.descendingMap()); }
    @Override public NavigableAssociation<K, V, ?> sub(K fromKey, boolean fromInclusive, K toKey, boolean toInclusive) {
        return new TreeAssociation<>(map.subMap(fromKey, fromInclusive, toKey, toInclusive)); }
    @Override public NavigableAssociation<K, V, ?> head(K toKey, boolean inclusive) {
        return new TreeAssociation<>(map.headMap(toKey, inclusive));}
    @Override public NavigableAssociation<K, V, ?> tail(K fromKey, boolean inclusive) {
        return new TreeAssociation<>(map.tailMap(fromKey, inclusive)); }


    /**
     * Sub-association extension of the {@link AbstractSortedAssociation}.
     *
     * @param <K> the type of keys
     * @param <V> the type of values
     */
    protected static class SortedSubAssociation<K, V>
            extends AbstractSortedAssociation<K, V, java.util.SortedMap<K, V>> implements RandomMatch {

        private static final long serialVersionUID = 3966799727744616965L;

        protected SortedSubAssociation(SortedMap<K, V> map) {
            super(map);
        }

        @Override public SortedAssociation<K, V, ?> sub(K fromKey, K toKey) {
            return new SortedSubAssociation<>(map.subMap(fromKey, toKey)); }
        @Override public SortedAssociation<K, V, ?> head(K toKey) {
            return new SortedSubAssociation<>(map.headMap(toKey)); }
        @Override public SortedAssociation<K, V, ?> tail(K fromKey) {
            return new SortedSubAssociation<>(map.tailMap(fromKey)); }
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
