package org.moodminds.elemental;

import java.util.Iterator;
import java.util.Map.Entry;
import java.util.NoSuchElementException;
import java.util.Objects;

import static org.moodminds.sneaky.Cast.cast;

/**
 * Template implementation of the {@link Association} interface.
 *
 * @param <K> the type of keys
 * @param <V> the type of values
 * @param <KV> the type of {@link KeyValue} entries
 */
public abstract class AbstractAssociation<K, V, KV extends KeyValue<K, V>>
        extends AbstractContainer<KV> implements Association<K, V, KV> {

    /**
     * {@inheritDoc}
     *
     * @param o {@inheritDoc}
     * @return {@inheritDoc}
     * @throws NullPointerException {@inheritDoc}
     */
    @Override
    public Iterator<KV> getAll(Object o) {
        if (o instanceof Entry) {
            Object key = ((Entry<?, ?>) o).getKey(); V value;
            try {
                if (((value = get(key)) != null || containsKey(key))
                        && Objects.equals(((Entry<?, ?>) o).getValue(), value))
                    return iterator(cast(key), cast(value), true);
            } catch (NoSuchElementException e) { iterator(null, null, false); }
        } return iterator(null, null, false);
    }

    /**
     * Return the Association {@link #toString()} beginning prefix.
     *
     * @return the Association {@link #toString()} beginning prefix
     */
    @Override
    protected String toStringBegin() {
        return "{";
    }

    /**
     * Return the Association {@link #toString()} ending prefix.
     *
     * @return the Association {@link #toString()} ending prefix
     */
    @Override
    protected String toStringEnd() {
        return "}";
    }

    /**
     * Return the {@link String} representation of this Association's entry.
     *
     * @param e the given Association's entry
     * @return the {@link String} representation of this Association's entry
     */
    @Override
    protected String toStringEntry(KV e) {
        return e.getKey() == this ? toStringThis() : e.getKey() +
                "=" + (e.getValue() == this ? toStringThis() : e.getValue());
    }

    /**
     * Return the {@link String} representation of this Association
     * for {@link #toString()} method if contains itself.
     *
     * @return the {@link String} representation of this Association if contains itself
     */
    @Override
    protected String toStringThis() {
        return "(this Association)";
    }

    /**
     * Retrieve an {@link Iterator} for the specified {@link K key} and {@link V value} entry,
     * along with the supplied flag indicating the presence of the entry for iteration.
     *
     * @param key the specified {@link K key}
     * @param value the specified {@link V value}
     * @param hasEntry the flag indicating the existence of the {@link KV} entry for iteration
     * @return an {@link Iterator} for the specified {@link K key} and {@link V value}
     */
    protected abstract Iterator<KV> iterator(K key, V value, boolean hasEntry);
}
