package org.moodminds.elemental;

import org.moodminds.sneaky.Cast;

import java.util.Iterator;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Spliterator;

import static org.moodminds.sneaky.Cast.cast;

/**
 * A template implementation of the {@link Association} interface.
 *
 * @param <K> the type of keys
 * @param <V> the type of values
 * @param <KV> the type of {@link KeyValue} entries
 */
public abstract class AbstractAssociation<K, V, KV extends KeyValue<? extends K, ? extends V>>
        extends AbstractContainer<KV> implements Association<K, V, KV> {

    /**
     * {@inheritDoc}
     *
     * @param o {@inheritDoc}
     * @return {@inheritDoc}
     * @throws ClassCastException {@inheritDoc}
     * @throws NullPointerException {@inheritDoc}
     */
    @Override
    public Iterator<KV> getAll(Object o) {
        if (o instanceof Map.Entry) {
            Object k = ((Map.Entry<?, ?>) o).getKey(); V value = get(k);
            try {
                if (isAssociated(k, ((Map.Entry<?, ?>) o).getValue(), value))
                    return iterator(cast(k), value, true);
            } catch (NoSuchElementException e) { /* just suppress */ }
        } return iterator(null, null, false);
    }

    /**
     * {@inheritDoc}
     *
     * @param o {@inheritDoc}
     * @return {@inheritDoc}
     * @throws ClassCastException {@inheritDoc}
     * @throws NullPointerException {@inheritDoc}
     */
    @Override
    public int getCount(Object o) {
        return o instanceof Map.Entry && contains(((Map.Entry<?, ?>) o).getKey(), ((Map.Entry<?, ?>) o).getValue()) ? 1 : 0;
    }

    /**
     * {@inheritDoc}
     *
     * @param key {@inheritDoc}
     * @param value {@inheritDoc}
     * @return {@code true} {@inheritDoc}
     * @throws ClassCastException {@inheritDoc}
     * @throws NullPointerException {@inheritDoc}
     */
    @Override
    public boolean contains(Object key, Object value) {
        try {
            return isAssociated(key, value, get(key));
        } catch (NoSuchElementException e) { return false; }
    }

    /**
     * Return an {@link Iterator} for the specified {@link K key} and {@link V value} entry,
     * along with the supplied flag indicating the presence of the entry for iteration.
     *
     * @param key the specified {@link K key}
     * @param value the specified {@link V value}
     * @param present the flag indicating the existence of the {@link KV} entry for iteration
     * @return an {@link Iterator} for the specified {@link K key} and {@link V value}
     */
    protected abstract Iterator<KV> iterator(K key, V value, boolean present);

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
     * @param kv the given Association's entry
     * @return the {@link String} representation of this Association's entry
     */
    @Override
    protected String toStringEntry(KV kv) {
        return toStringThisOrObject(kv.getKey()) + "=" + toStringThisOrObject(kv.getValue());
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
     * Return the {@link String} representation of this Association's entry.
     *
     * @param o the given Association's entry
     * @return the {@link String} representation of this Association's entry
     */
    private String toStringThisOrObject(Object o) {
        return o == this ? toStringThis() : String.valueOf(o);
    }

    /**
     * Determine whether this Association contains an entry with the specified key and value.
     *
     * @param k the key to check for in this Association
     * @param v the value to verify against the entry associated with the given key
     * @param value the current value associated with the specified key in this Association
     * @return {@code true} if an entry exists with the specified key and value; {@code false} otherwise
     * @throws ClassCastException if the key is of an inappropriate type for this Association
     * @throws NullPointerException if the specified key is {@code null} and this Association does not permit {@code null} keys
     */
    protected boolean isAssociated(Object k, Object v, V value) {
        return Objects.equals(v, value) && (value != null || containsKey(k));
    }

    /**
     * Keys view implementation template.
     */
    protected abstract class AbstractKeysContainer extends AbstractContainer<K> {

        @Override public Iterator<K> getAll(Object o) {
            return OptionalIterator.iterator(Cast.<K>cast(o), containsKey(o)); }
        @Override public int getCount(Object o) {
            return containsKey(o) ? 1 : 0; }
        @Override public int size() {
            return AbstractAssociation.this.size(); }
        @Override public Iterator<K> iterator() {
            return new Iterator<K>() {
                final Iterator<KV> iterator = AbstractAssociation.this.iterator();
                @Override public boolean hasNext() { return iterator.hasNext(); }
                @Override public K next() { return iterator.next().getKey(); }
            }; }

        @Override public abstract Spliterator<K> spliterator();
    }

    /**
     * Values view implementation template.
     */
    protected abstract class AbstractValuesContainer extends AbstractContainer<V> {

        @Override public int size() {
            return AbstractAssociation.this.size(); }
        @Override public Iterator<V> iterator() {
            return new Iterator<V>() {
                final Iterator<KV> iterator = AbstractAssociation.this.iterator();
                @Override public boolean hasNext() { return iterator.hasNext(); }
                @Override public V next() { return iterator.next().getValue(); }
            }; }

        @Override public abstract Spliterator<V> spliterator();
    }
}
