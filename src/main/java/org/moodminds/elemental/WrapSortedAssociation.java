package org.moodminds.elemental;

import java.util.NavigableMap;
import java.util.SortedMap;

import static java.util.Objects.requireNonNull;
import static org.moodminds.sneaky.Cast.cast;

/**
 * Wrapping {@link SortedMap} implementation of the {@link SortedAssociation} interface.
 *
 * @param <K> the type of keys
 * @param <V> the type of values
 * @param <M> the type of wrapped {@link SortedMap}
 */
public class WrapSortedAssociation<K, V, M extends SortedMap<K, V>> extends AbstractSortedAssociation<K, V, M>
        implements SortedAssociation<K, V, KeyValue<K, V>> {

    private static final long serialVersionUID = 2701133871934466876L;

    /**
     * Construct the object with the given {@link M map} .
     *
     * @param map the given {@link M map}  to wrap
     */
    protected WrapSortedAssociation(M map) {
        super(requireNonNull(map));
    }


    /**
     * Return wrapping {@link SortedAssociation} instance of the given {@link SortedMap} map.
     *
     * @param map the given {@link java.util.SortedMap} map
     * @param <K> the type of keys
     * @param <V> the type of values
     * @return wrapping {@link SortedAssociation} instance of the given {@link SortedMap} map
     * @throws NullPointerException if the given {@link SortedMap} map is {@code null}
     */
    public static <K, V> SortedAssociation<K, V, ?> wrap(SortedMap<K, V> map) {
        return map instanceof NavigableMap ? WrapNavigableAssociation.wrap(cast(map))
                : new WrapSortedAssociation<>(map);
    }
}
