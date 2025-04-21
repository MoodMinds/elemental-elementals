package org.moodminds.elemental;

import java.util.NavigableMap;

import static java.util.Objects.requireNonNull;

/**
 * Wrapping {@link NavigableMap} implementation of the {@link NavigableAssociation} interface.
 *
 * @param <K> the type of keys
 * @param <V> the type of values
 * @param <M> the type of wrapped {@link NavigableMap}
 */
public class WrapNavigableAssociation<K, V, M extends NavigableMap<K, V>> extends AbstractNavigableAssociation<K, V, M>
        implements NavigableAssociation<K, V, KeyValue<K, V>> {

    private static final long serialVersionUID = 8723477727145185198L;

    /**
     * Construct the object with the given {@link M map} .
     *
     * @param map the given {@link M map}  to wrap
     */
    protected WrapNavigableAssociation(M map) {
        super(requireNonNull(map));
    }


    /**
     * Return wrapping {@link NavigableAssociation} instance of the given {@link java.util.NavigableMap} map.
     *
     * @param map the given {@link java.util.NavigableMap} map
     * @param <K> the type of keys
     * @param <V> the type of values
     * @return wrapping {@link NavigableAssociation} instance of the given {@link java.util.NavigableMap} map
     * @throws NullPointerException if the given {@link java.util.NavigableMap} map is {@code null}
     */
    public static <K, V> NavigableAssociation<K, V, ?> wrap(NavigableMap<K, V> map) {
        return new WrapNavigableAssociation<>(map);
    }
}
