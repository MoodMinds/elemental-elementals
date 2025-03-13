package org.moodminds.elemental;

import java.io.Serializable;
import java.util.Comparator;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Spliterator;
import java.util.function.Consumer;
import java.util.stream.Stream;

import static java.util.Optional.ofNullable;
import static org.moodminds.sneaky.Cast.cast;

/**
 * A template implementation of the {@link Association} interface, which is powered by an internal {@link Map}.
 * <p>
 * The {@link RandomMatch} efficiently matches elements based on a provided example object,
 * leveraging the typically fast access of the underlying {@link Map} implementation.
 * However, there's no guarantee of consistently fast element search.
 *
 * @param <K> the type of keys
 * @param <V> the type of values
 * @param <KV> the type of {@link KeyValue} entries
 * @param <M> the type of wrapped {@link Map}
 */
public abstract class AbstractMapAssociation<K, V, KV extends KeyValue<? extends K, ? extends V>, M extends Map<K, V>>
        extends AbstractAssociation<K, V, KV> implements RandomMatch, Serializable {

    private static final long serialVersionUID = -4533400046102874586L;

    /**
     * Backing {@link M map} holder field.
     */
    protected final M map;

    /**
     * Construct the object with the given {@link M map}.
     *
     * @param map the given {@link M map}
     */
    protected AbstractMapAssociation(M map) {
        this.map = map;
    }

    @Override public int size() {
        return map.size(); }
    @Override public <R extends V> R get(Object key) {
        return cast(map.get(key)); }
    @Override public boolean contains(Object o) {
        return map.entrySet().contains(o); }
    @Override public boolean containsKey(Object key) {
        return map.containsKey(key); }
    @Override public boolean containsValue(Object value) {
        return map.containsValue(value); }
    @Override public V getOrDefault(Object key, V defaultValue) {
        return map.getOrDefault(key, defaultValue); }
    @Override public Iterator<KV> iterator() {
        return iterator(map.entrySet().iterator()); }
    @Override public Spliterator<KV> spliterator() {
        return spliterator(map.entrySet().spliterator()); }
    @Override public Stream<KV> stream() {
        return map.entrySet().stream().map(this::entry); }
    @Override public Stream<KV> parallelStream() {
        return map.entrySet().parallelStream().map(this::entry); }
    @Override public Container<K> keys() {
        return new KeysContainer(); }
    @Override public Container<V> values() {
        return new ValuesContainer(); }

    /**
     * Return {@link Iterator} of {@link KV key-values} by the given {@link Iterator} of {@link Entry entries}.
     *
     * @param entriesIterator the given {@link Iterator} of {@link Entry entries}.
     * @return {@link Iterator} of {@link KV key-values} by the given {@link Iterator} of {@link Entry entries}
     */
    protected Iterator<KV> iterator(Iterator<Entry<K, V>> entriesIterator) {
        return new Iterator<KV>() {
            @Override public boolean hasNext() { return entriesIterator.hasNext(); }
            @Override public KV next() { return entry(entriesIterator.next()); }
            @Override public void forEachRemaining(Consumer<? super KV> action) {
                entriesIterator.forEachRemaining(entry -> action.accept(entry(entry))); }
        };
    }

    /**
     * Return {@link Spliterator} of {@link KV key-values} by the given {@link Spliterator} of {@link Entry entries}.
     *
     * @param entriesSpliterator the given {@link Spliterator} of {@link Entry entries}.
     * @return {@link Spliterator} of {@link KV key-values} by the given {@link Spliterator} of {@link Entry entries}
     */
    protected Spliterator<KV> spliterator(Spliterator<Entry<K, V>> entriesSpliterator) {
        return new Spliterator<KV>() {

            @Override public boolean tryAdvance(Consumer<? super KV> action) { return entriesSpliterator.tryAdvance(consumer(action)); }
            @Override public void forEachRemaining(Consumer<? super KV> action) { entriesSpliterator.forEachRemaining(consumer(action)); }
            @Override public long estimateSize() { return entriesSpliterator.estimateSize(); }
            @Override public long getExactSizeIfKnown() { return entriesSpliterator.getExactSizeIfKnown(); }
            @Override public int characteristics() { return entriesSpliterator.characteristics() | IMMUTABLE; }
            @Override public Spliterator<KV> trySplit() { return ofNullable(entriesSpliterator.trySplit())
                    .map(AbstractMapAssociation.this::spliterator).orElse(null); }
            @Override public Comparator<? super KV> getComparator() { return ofNullable(entriesSpliterator.getComparator())
                    .<Comparator<KV>>map(comp -> (kv1, kv2) -> comp.compare(entry(kv1), entry(kv2))).orElse(null); }

            private Consumer<Entry<K, V>> consumer(Consumer<? super KV> action) {
                return entry -> action.accept(entry(entry)); }
        };
    }

    /**
     * Return {@link KV} entry instance by the given {@link Entry}.
     *
     * @param entry the given {@link Entry}
     * @return {@link KV} entry instance by the given {@link Entry}
     */
    protected abstract KV entry(Entry<K, V> entry);

    /**
     * Return {@link Entry} entry instance by the given {@link KV}.
     *
     * @param entry the given {@link KV}
     * @return {@link Entry} entry instance by the given {@link KV}
     */
    protected abstract Entry<K, V> entry(KV entry);


    /**
     * Map Association keys view implementation.
     */
    protected class KeysContainer extends AbstractKeysContainer implements RandomMatch {

        @Override public Spliterator<K> spliterator() {
            return map.keySet().spliterator(); }
    }

    /**
     * Map Association values view implementation.
     */
    protected class ValuesContainer extends AbstractValuesContainer {

        @Override public Spliterator<V> spliterator() {
            return map.values().spliterator(); }
    }
}
