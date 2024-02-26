package org.moodminds.elemental;

import org.moodminds.sneaky.Cast;

import java.util.Iterator;
import java.util.Map;
import java.util.SortedMap;
import java.util.Spliterator;
import java.util.stream.Stream;

import static java.util.Objects.requireNonNull;
import static java.util.Spliterator.IMMUTABLE;
import static org.moodminds.elemental.Pair.pair;
import static org.moodminds.sneaky.Cast.cast;

/**
 * Wrapping {@link Map} implementation of the {@link Association} interface.
 *
 * @param <K> the type of keys
 * @param <V> the type of values
 * @param <M> the type of wrapped {@link Map}
 */
public class WrapAssociation<K, V, M extends Map<K, V>>
        extends AbstractMapAssociation<K, V, KeyValue<K, V>, Map<K, V>> {

    private static final long serialVersionUID = 4349202813676022292L;

    /**
     * Construct the object with the given {@link M} map.
     *
     * @param map the given {@link M} map to wrap
     */
    protected WrapAssociation(M map) {
        super(requireNonNull(map));
    }

    @Override public Iterator<KeyValue<K, V>> iterator() {
        return WrapIterator.wrap(map.entrySet().iterator(), WrapKeyValue::wrap); }
    @Override public Spliterator<KeyValue<K, V>> spliterator() {
        return WrapSpliterator.wrap(map.entrySet().spliterator(), Cast::cast,
                WrapKeyValue::wrap, ch -> ch | IMMUTABLE); }
    @Override public Stream<KeyValue<K, V>> stream() {
        return map.entrySet().stream().map(WrapKeyValue::wrap); }
    @Override public Stream<KeyValue<K, V>> parallelStream() {
        return map.entrySet().parallelStream().map(WrapKeyValue::wrap); }

    @Override
    protected Iterator<KeyValue<K, V>> iterator(K key, V value, boolean hasEntry) {
        return hasEntry ? SingleIterator.iterator(pair(key, value)) : EmptyIterator.iterator();
    }

    /**
     * Return wrapping {@link Association} instance of the given {@link Map} map.
     *
     * @param wrapped the given {@link Map} map
     * @param <K> the type of keys
     * @param <V> the type of values
     * @return wrapping {@link Association} instance of the given {@link Map} map
     * @throws NullPointerException if the given {@link Map} map is {@code null}
     */
    public static <K, V> Association<K, V, ?> wrap(Map<K, V> wrapped) {
        return wrapped instanceof SortedMap ? WrapSortedAssociation.wrap(cast(wrapped))
                : new WrapAssociation<>(wrapped);
    }
}
