package org.moodminds.elemental;

import java.util.LinkedHashMap;
import java.util.stream.Stream;

import static java.util.Arrays.asList;

/**
 * A {@link LinkedHashMap}-powered implementation of the {@link Association} interface.
 *
 * @param <K> the common type of keys
 * @param <V> the common type of values
 */
public class LinkHashAssociation<K, V> extends HashAssociation<K, V> {

    private static final long serialVersionUID = 3576825581498980611L;

    /**
     * Construct the object with the given {@link KeyValue} pairs vararg.
     *
     * @param kvs the given {@link KeyValue} pairs varargs
     */
    @SafeVarargs
    public LinkHashAssociation(KeyValue<? extends K, ? extends V>... kvs) {
        this(asList(kvs));
    }

    /**
     * Construct the object with the given {@link Stream} of {@link KeyValue} pairs.
     *
     * @param kvs the given {@link Stream} of {@link KeyValue} pairs
     */
    public LinkHashAssociation(Stream<? extends KeyValue<? extends K, ? extends V>> kvs) {
        this(new LinkedHashMap<>(), kvs);
    }

    /**
     * Construct the object with the given {@link Container} of {@link KeyValue} pairs.
     *
     * @param kvs the given {@link Container} of {@link KeyValue} pairs
     */
    public LinkHashAssociation(Container<? extends KeyValue<? extends K, ? extends V>> kvs) {
        this(new LinkedHashMap<>(kvs.size()), kvs.stream());
    }

    /**
     * Construct the object with the given {@link java.util.Collection} of {@link KeyValue} pairs.
     *
     * @param kvs the given {@link java.util.Collection} of {@link KeyValue} pairs
     */
    public LinkHashAssociation(java.util.Collection<? extends KeyValue<? extends K, ? extends V>> kvs) {
        this(new LinkedHashMap<>(kvs.size()), kvs.stream());
    }

    /**
     * Construct the object with the given {@link Collection} of {@link KeyValue} pairs.
     *
     * @param kvs the given {@link Collection} of {@link KeyValue} pairs
     */
    public LinkHashAssociation(Collection<? extends KeyValue<? extends K, ? extends V>> kvs) {
        this((java.util.Collection<? extends KeyValue<? extends K,? extends V>>) kvs);
    }

    /**
     * Construct the object with the target {@link LinkedHashMap} and
     * specified {@link Stream} of {@link KeyValue} pairs.
     *
     * @param map the target {@link LinkedHashMap}
     * @param kvs the specified {@link Stream} of {@link KeyValue} pairs
     */
    protected LinkHashAssociation(LinkedHashMap<K, V> map, Stream<? extends KeyValue<? extends K, ? extends V>> kvs) {
        super(map, kvs);
    }

    /**
     * Return a {@link LinkHashAssociation} of the given {@link KeyValue} pairs vararg.
     *
     * @param kvs the given {@link KeyValue} pairs vararg
     * @param <K> the type of keys
     * @param <V> the type of values
     * @return a {@link LinkHashAssociation} of the given {@link KeyValue} pairs vararg
     */
    @SafeVarargs
    public static <K, V> LinkHashAssociation<K, V> association(KeyValue<? extends K, ? extends V>... kvs) {
        return new LinkHashAssociation<>(kvs);
    }
}
