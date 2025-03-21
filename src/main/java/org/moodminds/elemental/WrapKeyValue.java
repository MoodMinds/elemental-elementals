package org.moodminds.elemental;

import java.io.Serializable;
import java.util.Map.Entry;

import static java.util.Objects.requireNonNull;

/**
 * Wrapping {@link Entry} implementation of the {@link KeyValue} interface.
 *
 * @param <K> the type of key
 * @param <V> the type of value
 */
public class WrapKeyValue<K, V> extends AbstractKeyValue<K, V> implements Serializable {

    private static final long serialVersionUID = 1803146821604741974L;

    /**
     * Wrapping {@link Entry} entry holder field.
     */
    protected final Entry<K, V> entry;

    /**
     * Construct the object with the given {@link Entry} entry.
     *
     * @param entry the given {@link Entry} entry to wrap
     */
    public WrapKeyValue(Entry<K, V> entry) {
        this.entry = requireNonNull(entry);
    }

    @Override public K getKey() { return entry.getKey(); }
    @Override public V getValue() { return entry.getValue(); }


    /**
     * Return the KeyValue of the given {@link Entry} entry.
     *
     * @param entry the given {@link Entry} entry to wrap
     */
    public static <K, V> KeyValue<K, V> wrap(Entry<K, V> entry) {
        return new WrapKeyValue<>(entry);
    }
}
