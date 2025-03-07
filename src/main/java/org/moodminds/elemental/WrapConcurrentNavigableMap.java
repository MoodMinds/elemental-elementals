package org.moodminds.elemental;

/**
 * Wrapping {@link java.util.concurrent.ConcurrentNavigableMap} implementation of the {@link ConcurrentNavigableMap} interface.
 *
 * @param <K> the type of keys
 * @param <V> the type of values
 * @param <M> the type of wrapped {@link java.util.concurrent.ConcurrentNavigableMap}
 */
public class WrapConcurrentNavigableMap<K, V, M extends java.util.concurrent.ConcurrentNavigableMap<K, V>>
        extends WrapNavigableMap<K, V, M> implements ConcurrentNavigableMap<K, V> {

    private static final long serialVersionUID = 1371944929873507091L;

    /**
     * Construct the object with the given {@link M map} .
     *
     * @param map the given {@link M map}  to wrap
     */
    protected WrapConcurrentNavigableMap(M map) {
        super(map);
    }

    @Override public NavigableSet<K> keySet() {
        return new WrapNavigableSet<>(map.keySet()); }
    @Override public ConcurrentNavigableMap<K,V> subMap(K fromKey, boolean fromInclusive, K toKey, boolean toInclusive) {
        return wrap(map.subMap(fromKey, fromInclusive, toKey, toInclusive)); }
    @Override public ConcurrentNavigableMap<K,V> headMap(K toKey, boolean inclusive) {
        return wrap(map.headMap(toKey, inclusive)); }
    @Override public ConcurrentNavigableMap<K,V> tailMap(K fromKey, boolean inclusive) {
        return wrap(map.tailMap(fromKey, inclusive)); }
    @Override public ConcurrentNavigableMap<K,V> subMap(K fromKey, K toKey) {
        return wrap(map.subMap(fromKey, toKey)); }
    @Override public ConcurrentNavigableMap<K,V> headMap(K toKey) {
        return wrap(map.headMap(toKey)); }
    @Override public ConcurrentNavigableMap<K,V> tailMap(K fromKey) {
        return wrap(map.tailMap(fromKey)); }
    @Override public ConcurrentNavigableMap<K,V> descendingMap() {
        return wrap(map.descendingMap()); }

    /**
     * {@link RandomMatch} implementation of the wrapping {@link org.moodminds.elemental.WrapNavigableSet}.
     *
     * @param <E> the type of elements
     */
    protected static class WrapNavigableSet<E> extends org.moodminds.elemental.WrapNavigableSet<E, java.util.NavigableSet<E>>
            implements RandomMatch {

        private static final long serialVersionUID = -2111506641857883749L;

        protected WrapNavigableSet(java.util.NavigableSet<E> set) {
            super(set); }
    }


    /**
     * Return wrapping {@link ConcurrentNavigableMap} instance of the given {@link java.util.concurrent.ConcurrentNavigableMap} map.
     *
     * @param map the given {@link java.util.concurrent.ConcurrentNavigableMap} map
     * @param <K> the type of keys
     * @param <V> the type of values
     * @return wrapping {@link ConcurrentNavigableMap} instance of the given {@link java.util.concurrent.ConcurrentNavigableMap} map
     * @throws NullPointerException if the given {@link java.util.concurrent.ConcurrentNavigableMap} map is {@code null}
     */
    public static <K, V> ConcurrentNavigableMap<K, V> wrap(java.util.concurrent.ConcurrentNavigableMap<K, V> map) {
        return new WrapConcurrentNavigableMap<>(map);
    }
}
