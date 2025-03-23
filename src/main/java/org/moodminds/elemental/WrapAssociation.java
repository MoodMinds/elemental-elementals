package org.moodminds.elemental;

import java.util.Iterator;
import java.util.Map;
import java.util.SortedMap;

import static java.util.Objects.requireNonNull;
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
     * Construct the object with the given {@link M map} .
     *
     * @param map the given {@link M map}  to wrap
     */
    protected WrapAssociation(M map) {
        super(requireNonNull(map));
    }

    @Override protected Iterator<KeyValue<K, V>> iterator(K key, V value, boolean present) {
        return OptionalIterator.iterator(() -> new AbstractKeyValue<K, V>() {
            @Override public K getKey() { return key; }
            @Override public V getValue() { return value; }
        }, present); }
    @Override protected KeyValue<K, V> entry(Map.Entry<K, V> entry) {
        return WrapKeyValue.wrap(entry); }
    @Override protected Map.Entry<K, V> entry(KeyValue<K, V> entry) {
        return cast(entry); }

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
