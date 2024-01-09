package org.moodminds.elemental;

import java.util.Iterator;
import java.util.Map.Entry;
import java.util.NoSuchElementException;
import java.util.Objects;

import static org.moodminds.sneaky.Cast.cast;

/**
 * Template implementation of the {@link Association} interface that
 * operates with {@link KeyValue} which are also {@link Entry} instances.
 *
 * @param <K> the type of keys
 * @param <V> the type of values
 * @param <KV> the type of {@link KeyValue} entries
 * @param <E>  the type of {@link KeyValue} which are also {@link Entry} instances
 */
public abstract class AbstractAssociation<K, V, KV extends KeyValue<K, V>,
            E extends KeyValue<K, V> & Entry<K, V>>
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
                    return iterator(keyValue(cast(key), value));
            } catch (NoSuchElementException e) { iterator(null); }
        } return iterator(null);
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
     * Create an instance of {@link E} using the provided key and value.
     *
     * @param key   the given key
     * @param value the given value
     * @return an instance of {@link E} created with the provided key and value
     */
    protected abstract E keyValue(K key, V value);

    /**
     * Get an {@link Iterator} for the provided {@link E} instance.
     *
     * @param entry the given {@link E} instance
     * @return an {@link Iterator} for the given {@link E} instance
     */
    protected abstract Iterator<KV> iterator(E entry);


    /**
     * An immutable template implementation of the {@link Association} interface
     * that operates with {@link AbstractKeyValue}.
     *
     * @param <K>  the type of keys
     * @param <V>  the type of values
     */
    public abstract static class AbstractImmutableAssociation<K, V>
            extends AbstractAssociation<K, V, KeyValue<K, V>, AbstractKeyValue<K, V>> {

        /**
         * Create an instance of {@link AbstractKeyValue} using the provided key and value.
         *
         * @param key   the given key
         * @param value the given value
         * @return an instance of {@link AbstractKeyValue} created with the provided key and value
         */
        @Override
        protected AbstractKeyValue<K, V> keyValue(K key, V value) {
            return new AbstractKeyValue<K, V>() {
                @Override public K getKey() { return key; }
                @Override public V getValue() { return value; }
                @Override public V setValue(V value) { throw new UnsupportedOperationException(); }
            };
        }

        /**
         * Get an {@link Iterator} for the provided {@link AbstractKeyValue} instance.
         *
         * @param entry the given {@link AbstractKeyValue} instance
         * @return an {@link Iterator} for the given {@link AbstractKeyValue} instance
         */
        @Override
        protected Iterator<KeyValue<K, V>> iterator(AbstractKeyValue<K, V> entry) {
            return new AbstractIterator<KeyValue<K,V>>() {

                boolean hasNext = entry != null;

                @Override public boolean hasNext() {
                    return hasNext; }
                @Override public KeyValue<K, V> element() {
                    hasNext = false; return entry; }
            };
        }
    }
}
