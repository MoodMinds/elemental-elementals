package org.moodminds.elemental;

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
        extends AbstractMapAssociation<K, V, Map.Entry<K, V>, M> implements Map<K, V> {

    private static final long serialVersionUID = -2257611878460710620L;

    /**
     * Construct the object with the given {@link M} map.
     *
     * @param map the given {@link M} map to wrap
     */
    public WrapMap(M map) {
        super(requireNonNull(map));
    }

    @Override public boolean equatable(Object obj) {
        return obj instanceof java.util.Map; }

    @Override @SuppressWarnings("unchecked") public V get(Object key) {
        return super.get(key); }
    @Override public void putAll(java.util.Map<? extends K, ? extends V> m) {
        map.putAll(m); }
    @Override public boolean isEmpty() {
        return map.isEmpty(); }
    @Override public V put(K key, V value) {
        return map.put(key, value); }
    @Override public V remove(Object key) {
        return map.remove(key); }
    @Override public void clear() {
        map.clear(); }
    @Override public void forEach(BiConsumer<? super K, ? super V> action) {
        map.forEach(action); }
    @Override public void replaceAll(BiFunction<? super K, ? super V, ? extends V> function) {
        map.replaceAll(function); }
    @Override public V putIfAbsent(K key, V value) {
        return map.putIfAbsent(key, value); }
    @Override public boolean remove(Object key, Object value) {
        return map.remove(key, value); }
    @Override public boolean replace(K key, V oldValue, V newValue) {
        return map.replace(key, oldValue, newValue); }
    @Override public V replace(K key, V value) {
        return map.replace(key, value); }
    @Override public V computeIfAbsent(K key, Function<? super K, ? extends V> mappingFunction) {
        return map.computeIfAbsent(key, mappingFunction); }
    @Override public V computeIfPresent(K key, BiFunction<? super K, ? super V, ? extends V> remappingFunction) {
        return map.computeIfPresent(key, remappingFunction); }
    @Override public V compute(K key, BiFunction<? super K, ? super V, ? extends V> remappingFunction) {
        return map.compute(key, remappingFunction); }
    @Override public V merge(K key, V value, BiFunction<? super V, ? super V, ? extends V> remappingFunction) {
        return map.merge(key, value, remappingFunction); }
    @Override public Set<java.util.Map.Entry<K, V>> entrySet() {
        return WrapSet.wrap(map.entrySet()); }
    @Override public Set<K> keySet() {
        return WrapSet.wrap(map.keySet()); }
    @Override public Iterator<Map.Entry<K, V>> iterator() {
        return WrapRemoveIterator.wrap(map.entrySet().iterator(), WrapEntry::wrap); }
    @Override public Spliterator<Map.Entry<K, V>> spliterator() {
        return WrapSpliterator.wrap(map.entrySet().spliterator(), identity(), WrapEntry::wrap); }
    @Override public Stream<Map.Entry<K, V>> stream() {
        return map.entrySet().stream().map(WrapEntry::wrap); }
    @Override public Stream<Map.Entry<K, V>> parallelStream() {
        return map.entrySet().parallelStream().map(WrapEntry::wrap); }
    @Override public Collection<V> values() {
        return WrapCollection.wrap(map.values()); }
    @Override public int hashCode() {
        return map.hashCode(); }
    @Override public boolean equals(Object obj) {
        return this == obj || (obj instanceof WrapMap && map.equals(((WrapMap<?, ?, ?>) obj).map))
                || map.equals(obj); }

    @Override protected String toStringThis() {
        return "(this Map)"; }

    @Override protected Iterator<Map.Entry<K, V>> iterator(K k, V v, boolean hasNext) {
        return new Object() {

            V value = v; boolean evicted; Iterator<?> modCheckIterator = map.entrySet().iterator();

            V value() { V value; return this.value = !evicted && ((value = get(k)) != null
                        || !(evicted = !containsKey(k))) ? value : this.value; }

            Iterator<Map.Entry<K, V>> iterator() {
                return hasNext ? new SingleIterator<Map.Entry<K,V>>(new Entry<K, V>() {

                    @Override public K getKey() { return k; }
                    @Override public V getValue() { return value(); }
                    @Override public V setValue(V newValue) {
                        V oldValue; oldValue = !evicted && ((oldValue = map.put(k, newValue)) != null
                                || !(evicted = !containsKey(k))) ? oldValue : value;
                        value = newValue; return oldValue; }
                }) {
                    @Override public Map.Entry<K, V> next() {
                        modCheckIterator.next(); return super.next(); }
                    @Override protected void removeElement() {
                        try { modCheckIterator.next(); }
                        catch (NoSuchElementException ignored) {}

                        V value = value();
                        if (!evicted)
                            map.remove(k, value);
                        evicted = true;

                        modCheckIterator = map.entrySet().iterator(); }
                } : new EmptyIterator<Map.Entry<K,V>>() {
                    @Override public Map.Entry<K, V> next() {
                        modCheckIterator.next(); return super.next(); }
                    @Override protected void removeElement() { /* will never happen */ }
                };
            }
        }.iterator(); }


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
     */
    public static class WrapEntry<K, V> extends Entry<K, V> {

        protected final java.util.Map.Entry<K, V> entry;

        /**
         * Construct the object with the given {@link E} map entry.
         *
         * @param entry the given {@link E} map entry to wrap
         */
        public WrapEntry(java.util.Map.Entry<K, V> entry) {
            this.entry = requireNonNull(entry);
        }

        @Override public K getKey() {
            return entry.getKey(); }
        @Override public V getValue() {
            return entry.getValue(); }
        @Override public V setValue(V value) {
            return entry.setValue(value); }


        /**
         * Return wrapping {@link Map.Entry} instance of the given {@link java.util.Map.Entry} map entry.
         *
         * @param entry the given {@link java.util.Map.Entry} map entry
         * @param <K> the type of keys
         * @param <V> the type of values
         * @return wrapping {@link Map.Entry} instance of the given {@link java.util.Map.Entry} map entry
         */
        public static <K, V> Map.Entry<K, V> wrap(java.util.Map.Entry<K, V> entry) {
            return new WrapEntry<>(entry);
        }
    }


    /**
     * Return wrapping {@link Map} instance of the given {@link java.util.Map} map.
     *
     * @param map the given {@link java.util.Map} map
     * @param <K> the type of keys
     * @param <V> the type of values
     * @return wrapping {@link Map} instance of the given {@link java.util.Map} map
     * @throws NullPointerException if the given {@link java.util.Map} map is {@code null}
     */
    public static <K, V> Map<K, V> wrap(java.util.Map<K, V> map) {
        return map instanceof ConcurrentMap ? WrapConcurrentMap.wrap(cast(map))
                : map instanceof SortedMap ? WrapSortedMap.wrap(cast(map))
                : new WrapMap<>(map);
    }
}
