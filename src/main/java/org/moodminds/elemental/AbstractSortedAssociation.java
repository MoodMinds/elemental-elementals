package org.moodminds.elemental;

import org.moodminds.sneaky.Cast;

import java.util.Comparator;
import java.util.Iterator;
import java.util.SortedMap;
import java.util.Spliterator;

import static java.util.Spliterator.IMMUTABLE;
import static org.moodminds.elemental.Pair.pair;

/**
 * Template implementation of the {@link SortedAssociation} interface,
 * which is powered by an internal {@link SortedMap}.
 *
 * @param <K> the type of keys
 * @param <V> the type of values
 * @param <M> the type of wrapped {@link SortedMap}
 */
public abstract class AbstractSortedAssociation<K, V, M extends SortedMap<K, V>>
        extends AbstractMapAssociation<K, V, KeyValue<K, V>, M>
        implements SortedAssociation<K, V, KeyValue<K, V>> {

    private static final long serialVersionUID = -7434083876349223128L;

    /**
     * Construct the object with the given {@link M} map.
     *
     * @param map the given {@link M} map to wrap
     */
    protected AbstractSortedAssociation(M map) {
        super(map);
    }

    @Override public Comparator<? super K> comparator() {
        return map.comparator(); }
    @Override public K firstKey() {
        return map.firstKey(); }
    @Override public K lastKey() {
        return map.lastKey(); }

    @Override public Iterator<KeyValue<K, V>> iterator() {
        return WrapIterator.wrap(map.entrySet().iterator(), WrapKeyValue::wrap); }
    @Override public Spliterator<KeyValue<K, V>> spliterator() {
        return WrapSpliterator.wrap(map.entrySet().spliterator(), Cast::cast,
                WrapKeyValue::wrap, ch -> ch | IMMUTABLE); }
    @Override protected Iterator<KeyValue<K, V>> iterator(K key, V value, boolean hasEntry) {
        return hasEntry ? SingleIterator.iterator(pair(key, value)) : EmptyIterator.iterator(); }
}
