package org.moodminds.elemental;

import org.moodminds.sneaky.Cast;

import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.SortedMap;
import java.util.Spliterator;
import java.util.concurrent.ConcurrentMap;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.BiPredicate;
import java.util.function.Function;
import java.util.function.Predicate;

import static java.util.Objects.requireNonNull;
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
     * Construct the object with the given {@link M map} .
     *
     * @param map the given {@link M map}  to wrap
     */
    public WrapMap(M map) {
        super(requireNonNull(map));
    }

    @Override public boolean equatable(Object obj) {
        return obj instanceof java.util.Map; }

    @Override @SuppressWarnings("unchecked") public V get(Object key) {
        return super.get(key); }
    @Override public Iterator<Map.Entry<K, V>> getAll(Object o) {
        return map instanceof Map ? Cast.<Map<K, V>>cast(map).getAll(o) : super.getAll(o); }
    @Override public int getCount(Object o) {
        return map instanceof Map ? Cast.<Map<K, V>>cast(map).getCount(o) : super.getCount(o); }
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
    @Override public boolean removeIf(BiPredicate<? super K, ? super V> filter) {
        return map instanceof Map ? Cast.<Map<K, V>>cast(map).removeIf(filter)
                : Map.super.removeIf(filter); }
    @Override public boolean removeIfValue(Predicate<? super V> filter) {
        return map instanceof Map ? Cast.<Map<K, V>>cast(map).removeIfValue(filter)
                : Map.super.removeIfValue(filter); }
    @Override public boolean retainIf(BiPredicate<? super K, ? super V> filter) {
        return map instanceof Map ? Cast.<Map<K, V>>cast(map).retainIf(filter)
                : Map.super.retainIf(filter); }
    @Override public boolean retainIfValue(Predicate<? super V> filter) {
        return map instanceof Map ? Cast.<Map<K, V>>cast(map).retainIfValue(filter)
                : Map.super.retainIfValue(filter); }
    @Override public V replace(K key) {
        return map instanceof Map ? Cast.<Map<K, V>>cast(map).replace(key)
                : map.computeIfPresent(key, (k, v) -> v); }
    @Override public V replace(K key, V value) {
        return map.replace(key, value); }
    @Override public boolean replace(K key, V oldValue, V newValue) {
        return map.replace(key, oldValue, newValue); }
    @Override public V computeIfAbsent(K key, Function<? super K, ? extends V> mappingFunction) {
        return map.computeIfAbsent(key, mappingFunction); }
    @Override public V computeIfPresent(K key, BiFunction<? super K, ? super V, ? extends V> remappingFunction) {
        return map.computeIfPresent(key, remappingFunction); }
    @Override public V compute(K key, BiFunction<? super K, ? super V, ? extends V> remappingFunction) {
        return map.compute(key, remappingFunction); }
    @Override public V merge(K key, V value, BiFunction<? super V, ? super V, ? extends V> remappingFunction) {
        return map.merge(key, value, remappingFunction); }
    @Override public Set<K> keySet() {
        return new WrapSet<>(map.keySet()); }
    @Override public Collection<V> values() {
        return WrapCollection.wrap(map.values()); }
    @Override public Set<java.util.Map.Entry<K, V>> entrySet() {
        return new WrapEntrySet(map.entrySet()); }
    @Override public Iterator<Map.Entry<K, V>> iterator() {
        return new WrapIterator(map.entrySet().iterator()); }

    @Override public int hashCode() {
        return map.hashCode(); }
    @Override public boolean equals(Object obj) {
        return this == obj || (obj instanceof WrapMap
                && map.equals(((WrapMap<?, ?, ?>) obj).map)) || map.equals(obj); }

    @Override protected String toStringThis() {
        return "(this Map)"; }

    @Override protected Spliterator<Entry<K, V>> spliterator(Spliterator<java.util.Map.Entry<K, V>> spliterator) {
        return new WrapSpliterator(spliterator); }

    @Override protected Iterator<Map.Entry<K, V>> iterator(K k, V v, boolean present) {
        return new Object() {

            K key = k; V value = v; boolean evicted; Iterator<?> modCheckIterator = map.entrySet().iterator();

            V value() { V value; return this.value = !evicted
                    && !(evicted = !isAssociated(key, value = get(key), value)) ? value : this.value; }

            class Entry extends AbstractKeyValue<K, V> implements Map.Entry<K, V> {

                @Override public K getKey() { return key; }
                @Override public V getValue() { return value(); }

                @Override public K setKey(K newKey) {
                    K oldKey = key; key = newKey;
                    if (map.computeIfPresent(newKey, (k, v) -> {
                        checkSameKey(oldKey, newKey); return !evicted ? (value = v) : v;
                    }) == null) {
                        checkSameKey(oldKey, newKey);
                        if (!evicted)
                            if (map.containsKey(newKey))
                                value = null;
                            else evicted = true;
                    } return oldKey;
                }

                @Override public V setValue(V newValue) {
                    Object[] oldValue = new Object[]{value}; value = newValue;

                    if (evicted)
                        map.computeIfPresent(key, (k, v) -> v);
                    else if (value != null)
                        map.compute(key, (k, v) -> {
                            if (v != null || map.containsKey(k)) {
                                oldValue[0] = v; return value; }
                            else {
                                evicted = true; return null; }
                        });
                    else if (map.computeIfPresent(key, (k, v) -> cast(oldValue[0] = v)) == null)
                        if (!map.containsKey(key))
                            evicted = true;
                        else oldValue[0] = null;
                    else map.put(key, null);

                    return cast(oldValue[0]);
                }
            }

            Iterator<Map.Entry<K, V>> iterator() {
                return new Iterator<Map.Entry<K, V>>() {

                    final Iterator<Map.Entry<K, V>> iterator = new OptionalIterator<Map.Entry<K, V>>(Entry::new, present) {
                        @Override protected void removeElement() {
                            value(); present = false;
                            if (!evicted) {
                                map.remove(key, value); evicted = true;
                            } }
                    };

                    @Override public boolean hasNext() {
                        return iterator.hasNext(); }
                    @Override public Map.Entry<K, V> next() {
                        modCheckIterator.next(); return iterator.next(); }
                    @Override public void remove() {
                        try { modCheckIterator.next(); }
                        catch (NoSuchElementException ignored) {} // should never happen
                        iterator.remove(); modCheckIterator = map.entrySet().iterator(); }
                };
            }
        }.iterator(); }

    @Override protected Entry<K, V> entry(java.util.Map.Entry<K, V> entry) {
        return WrapEntry.wrap(entry); }
    @Override protected java.util.Map.Entry<K, V> entry(Entry<K, V> entry) {
        return entry; }

    /**
     * Wrapping {@link java.util.Map.Entry} implementation of the {@link Map.Entry} interface.
     *
     * @param <K> the type of keys
     * @param <V> the type of values
     */
    protected static class WrapEntry<K, V> extends WrapKeyValue<K, V> implements Map.Entry<K, V> {

        private static final long serialVersionUID = -7867649034913474219L;

        /**
         * Construct the object with the given {@link java.util.Map.Entry} map entry.
         *
         * @param entry the given {@link java.util.Map.Entry} map entry to wrap
         */
        protected WrapEntry(java.util.Map.Entry<K, V> entry) {
            super(entry);
        }

        @Override public K setKey(K key) {
            if (entry instanceof Map.Entry)
                return ((Map.Entry<K, V>) entry).setKey(key);
            try { entry.setValue(getValue()); }
            catch (UnsupportedOperationException ignored) {
                throw new UnsupportedOperationException(); }
            catch (IllegalStateException ignored) {
                throw new IllegalStateException(); }
            checkSameKey(entry.getKey(), key);
            // no real key instance replacement
            return entry.getKey(); }
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
     * Wrap Map entries {@link Iterator} implementation.
     */
    protected class WrapIterator extends AssociationIterator {

        protected WrapIterator(Iterator<java.util.Map.Entry<K, V>> iterator) {
            super(iterator); }

        @Override public void remove() {
            iterator.remove(); }
    }

    /**
     * Wrap Map entries {@link Spliterator} implementation.
     */
    protected class WrapSpliterator extends AssociationSpliterator {

        protected WrapSpliterator(Spliterator<java.util.Map.Entry<K, V>> spliterator) {
            super(spliterator); }

        @Override public int characteristics() {
            return spliterator.characteristics(); }
    }

    /**
     * {@link RandomMatch} implementation of the wrapping {@link org.moodminds.elemental.WrapSet}.
     *
     * @param <E> the type of elements
     */
    protected static class WrapSet<E> extends org.moodminds.elemental.WrapSet<E, java.util.Set<E>>
            implements RandomMatch {

        private static final long serialVersionUID = 6606837168358207396L;

        protected WrapSet(java.util.Set<E> set) {
            super(set); }
    }

    /**
     * Wrapping {@link java.util.Map.Entry} set.
     */
    protected class WrapEntrySet extends WrapSet<java.util.Map.Entry<K, V>> {

        private static final long serialVersionUID = 3069710402412052841L;

        protected WrapEntrySet(java.util.Set<java.util.Map.Entry<K, V>> set) {
            super(set); }

        @Override public Iterator<java.util.Map.Entry<K, V>> getAll(Object o) {
            return cast(WrapMap.this.getAll(o)); }
    }


    /**
     * Check key replacement for equivalence before replacement.
     *
     * @param key the key to replace
     * @param newKey the new key to replace with
     */
    private static void checkSameKey(Object key, Object newKey) {
        if (!Objects.equals(key, newKey))
            throw new IllegalArgumentException("Entry key is not equivalent: " + newKey);
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
