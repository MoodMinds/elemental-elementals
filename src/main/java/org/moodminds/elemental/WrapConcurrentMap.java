package org.moodminds.elemental;

import java.util.concurrent.ConcurrentNavigableMap;

import static org.moodminds.sneaky.Cast.cast;

/**
 * Wrapping {@link java.util.concurrent.ConcurrentMap} implementation of the {@link ConcurrentMap} interface.
 *
 * @param <K> the type of keys
 * @param <V> the type of values
 * @param <M> the type of wrapped {@link java.util.concurrent.ConcurrentMap}
 */
public class WrapConcurrentMap<K, V, M extends java.util.concurrent.ConcurrentMap<K, V>> extends WrapMap<K, V, M>
        implements ConcurrentMap<K, V> {

    private static final long serialVersionUID = 1153508266742482691L;

    /**
     * Construct the object with the given {@link M} map.
     *
     * @param wrapped the given {@link M} map to wrap
     */
    protected WrapConcurrentMap(M wrapped) {
        super(wrapped);
    }


    /**
     * Return wrapping {@link ConcurrentMap} instance of the given {@link java.util.concurrent.ConcurrentMap} map.
     *
     * @param wrapped the given {@link java.util.concurrent.ConcurrentMap} map
     * @param <K> the type of keys
     * @param <V> the type of values
     * @return wrapping {@link ConcurrentMap} instance of the given {@link java.util.concurrent.ConcurrentMap} map
     * @throws NullPointerException if the given {@link java.util.concurrent.ConcurrentMap} map is {@code null}
     */
    public static <K, V> ConcurrentMap<K, V> wrap(java.util.concurrent.ConcurrentMap<K, V> wrapped) {
        return wrapped instanceof ConcurrentNavigableMap ? WrapConcurrentNavigableMap.wrap(cast(wrapped))
                : new WrapConcurrentMap<>(wrapped);
    }
}
