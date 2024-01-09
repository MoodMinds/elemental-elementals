package org.moodminds.elemental;

import java.io.Serializable;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.SortedMap;
import java.util.Spliterator;
import java.util.concurrent.ConcurrentMap;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Stream;

import static java.util.Objects.requireNonNull;
import static java.util.function.Function.identity;
import static org.moodminds.sneaky.Cast.cast;

/**
 * Wrapping {@link java.util.Map} implementation of the {@link Map} interface.
 *
 * @param <K> the type of keys
 * @param <V> the type of values
 * @param <M> the type of wrapped {@link java.util.Map}
 */
public class WrapMap<K, V, M extends java.util.Map<K, V>>
        extends AbstractAssociation<K, V, Map.Entry<K, V>, WrapMap.RemoveEntry<K, V>>
        implements Map<K, V>, Serializable {

    private static final long serialVersionUID = -2257611878460710620L;

    protected final M wrapped;

    /**
     * Construct the object with the given {@link M} map.
     *
     * @param wrapped the given {@link M} map to wrap
     */
    public WrapMap(M wrapped) {
        this.wrapped = requireNonNull(wrapped);
    }

    @Override public boolean equatable(Object obj) {
        return obj instanceof java.util.Map; }
    @Override public int size() {
        return wrapped.size(); }
    @Override @SuppressWarnings("unchecked") public V get(Object key) {
        return wrapped.get(key); }
    @Override public boolean containsKey(Object key) {
        return wrapped.containsKey(key); }
    @Override public boolean containsValue(Object value) {
        return wrapped.containsValue(value); }
    @Override public V getOrDefault(Object key, V defaultValue) {
        return wrapped.getOrDefault(key, defaultValue); }
    @Override public void putAll(java.util.Map<? extends K, ? extends V> m) {
        wrapped.putAll(m); }
    @Override public boolean isEmpty() {
        return wrapped.isEmpty(); }
    @Override public V put(K key, V value) {
        return wrapped.put(key, value); }
    @Override public V remove(Object key) {
        return wrapped.remove(key); }
    @Override public void clear() {
        wrapped.clear(); }
    @Override public void forEach(BiConsumer<? super K, ? super V> action) {
        wrapped.forEach(action); }
    @Override public void replaceAll(BiFunction<? super K, ? super V, ? extends V> function) {
        wrapped.replaceAll(function); }
    @Override public V putIfAbsent(K key, V value) {
        return wrapped.putIfAbsent(key, value); }
    @Override public boolean remove(Object key, Object value) {
        return wrapped.remove(key, value); }
    @Override public boolean replace(K key, V oldValue, V newValue) {
        return wrapped.replace(key, oldValue, newValue); }
    @Override public V replace(K key, V value) {
        return wrapped.replace(key, value); }
    @Override public V computeIfAbsent(K key, Function<? super K, ? extends V> mappingFunction) {
        return wrapped.computeIfAbsent(key, mappingFunction); }
    @Override public V computeIfPresent(K key, BiFunction<? super K, ? super V, ? extends V> remappingFunction) {
        return wrapped.computeIfPresent(key, remappingFunction); }
    @Override public V compute(K key, BiFunction<? super K, ? super V, ? extends V> remappingFunction) {
        return wrapped.compute(key, remappingFunction); }
    @Override public V merge(K key, V value, BiFunction<? super V, ? super V, ? extends V> remappingFunction) {
        return wrapped.merge(key, value, remappingFunction); }
    @Override public Set<java.util.Map.Entry<K, V>> entrySet() {
        return WrapSet.wrap(wrapped.entrySet()); }
    @Override public Set<K> keySet() {
        return WrapSet.wrap(wrapped.keySet()); }
    @Override public Iterator<Map.Entry<K, V>> iterator() {
        return WrapRemoveIterator.wrap(wrapped.entrySet().iterator(), this::keyValue); }
    @Override public Spliterator<Map.Entry<K, V>> spliterator() {
        return WrapSpliterator.wrap(wrapped.entrySet().spliterator(), identity(), this::keyValue); }
    @Override public Stream<Map.Entry<K, V>> stream() {
        return wrapped.entrySet().stream().map(this::keyValue); }
    @Override public Stream<Map.Entry<K, V>> parallelStream() {
        return wrapped.entrySet().parallelStream().map(this::keyValue); }
    @Override public Collection<V> values() {
        return WrapCollection.wrap(wrapped.values()); }
    @Override public int hashCode() {
        return wrapped.hashCode(); }
    @Override public boolean equals(Object obj) {
        return this == obj || (obj instanceof WrapMap && wrapped.equals(((WrapMap<?, ?, ?>) obj).wrapped))
                || wrapped.equals(obj); }

    @Override protected String toStringThis() {
        return "(this Map)"; }

    protected Map.Entry<K, V> keyValue(java.util.Map.Entry<K, V> entry) {
        return WrapEntry.wrap(entry);
    }

    @Override
    protected RemoveEntry<K, V> keyValue(K k, V v) {
        return new RemoveEntry<K, V>() {

            V value = v; boolean evicted;

            @Override public K getKey() { return k; }
            @Override public V getValue() {
                V value; return this.value = !evicted && ((value = get(k)) != null || !(evicted = !containsKey(k)))
                        ? value : this.value; }
            @Override public V setValue(V newValue) {
                V oldValue; oldValue = !evicted && ((oldValue = replace(k, newValue)) != null || !(evicted = !containsKey(k)))
                        ? oldValue : value;
                value = newValue; return oldValue; }
            @Override void remove() {
                V value = getValue();
                if (!evicted)
                    WrapMap.this.remove(k, value);
                evicted = true;
            }
        };
    }

    @Override
    protected Iterator<Map.Entry<K, V>> iterator(RemoveEntry<K, V> entry) {
        return new AbstractRemoveIterator<Map.Entry<K, V>>() {

            Iterator<?> modCheckIterator = iterator(); boolean hasNext = entry != null;

            @Override public boolean hasNext() {
                return hasNext; }
            @Override public Map.Entry<K, V> next() {
                modCheckIterator.next(); return super.next(); }
            @Override protected Map.Entry<K, V> element() {
                hasNext = false; return entry; }
            @Override protected void removeElement() {
                try { modCheckIterator.next(); }
                catch (NoSuchElementException ignored) {}
                entry.remove(); modCheckIterator = iterator(); }
        };
    }


    /**
     * An {@link AbstractKeyValue} implementation of the {@link Map.Entry} interface
     *
     * @param <K> the type of the key
     * @param <V> the type of the value
     */
    protected abstract static class Entry<K, V> extends AbstractKeyValue<K, V> implements Map.Entry<K, V> {

        /**
         * {@inheritDoc}
         *
         * @return {@inheritDoc}
         * @throws IllegalStateException implementations may throw this exception if the entry has been
         * removed from the backing map
         */
        @Override
        public abstract K getKey();

        /**
         * {@inheritDoc}
         *
         * @return {@inheritDoc}
         * @throws IllegalStateException implementations may throw this exception if the entry has been
         * removed from the backing map
         */
        @Override
        public abstract V getValue();
    }


    /**
     * Wrapping {@link java.util.Map.Entry} implementation of the {@link Map.Entry} interface.
     *
     * @param <K> the type of keys
     * @param <V> the type of values
     * @param <E> the type of wrapped {@link java.util.Map.Entry}
     */
    public static class WrapEntry<K, V, E extends java.util.Map.Entry<K, V>> extends Entry<K, V> {

        protected final E wrapped;

        /**
         * Construct the object with the given {@link E} map entry.
         *
         * @param wrapped the given {@link E} map entry to wrap
         */
        public WrapEntry(E wrapped) {
            this.wrapped = requireNonNull(wrapped);
        }

        @Override public K getKey() {
            return wrapped.getKey(); }
        @Override public V getValue() {
            return wrapped.getValue(); }
        @Override public V setValue(V value) {
            return wrapped.setValue(value); }


        /**
         * Return wrapping {@link Map.Entry} instance of the given {@link java.util.Map.Entry} map entry.
         *
         * @param wrapped the given {@link java.util.Map.Entry} map entry
         * @param <K> the type of keys
         * @param <V> the type of values
         * @return wrapping {@link Map.Entry} instance of the given {@link java.util.Map.Entry} map entry
         */
        public static <K, V> Map.Entry<K, V> wrap(java.util.Map.Entry<K, V> wrapped) {
            return new WrapEntry<>(wrapped);
        }
    }


    /**
     * Removable implementation of the {@link Map.Entry} interface.
     *
     * @param <K> the type of keys
     * @param <V> the type of values
     */
    protected abstract static class RemoveEntry<K, V> extends Entry<K, V> {

        /**
         * Remove this key-value pair from the map.
         */
        abstract void remove();
    }


    /**
     * Return wrapping {@link Map} instance of the given {@link java.util.Map} map.
     *
     * @param wrapped the given {@link java.util.Map} map
     * @param <K> the type of keys
     * @param <V> the type of values
     * @return wrapping {@link Map} instance of the given {@link java.util.Map} map
     * @throws NullPointerException if the given {@link java.util.Map} map is {@code null}
     */
    public static <K, V> Map<K, V> wrap(java.util.Map<K, V> wrapped) {
        return wrapped instanceof ConcurrentMap ? WrapConcurrentMap.wrap(cast(wrapped))
                : wrapped instanceof SortedMap ? WrapSortedMap.wrap(cast(wrapped))
                : new WrapMap<>(wrapped);
    }
}
