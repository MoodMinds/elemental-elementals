package org.moodminds.elemental;

import java.util.Map.Entry;
import java.util.Objects;

/**
 * A template implementation of the {@link KeyValue} interface, additionally extending the {@link Entry} interface.
 * <p>
 * This extension is necessary to ensure equality symmetry with the {@link Entry}.
 *
 * @param <K> the type of the key
 * @param <V> the type of the value
 */
public abstract class AbstractKeyValue<K, V> implements KeyValue<K, V>, Entry<K, V>, Equatable {

    /**
     * {@inheritDoc}
     *
     * @return {@inheritDoc}
     */
    @Override
    @SuppressWarnings("unchecked")
    public abstract K getKey();

    /**
     * {@inheritDoc}
     *
     * @return {@inheritDoc}
     */
    @Override
    @SuppressWarnings("unchecked")
    public abstract V getValue();

    /**
     * {@inheritDoc}
     *
     * @return {@inheritDoc}
     */
    @Override
    public int hashCode() {
        return Objects.hashCode(getKey()) ^ Objects.hashCode(getValue());
    }

    /**
     * {@inheritDoc}
     *
     * @param obj {@inheritDoc}
     * @return {@inheritDoc}
     */
    @Override
    public boolean equatable(Object obj) {
        return obj instanceof Entry;
    }

    /**
     * {@inheritDoc}
     *
     * @param obj {@inheritDoc}
     * @return {@inheritDoc}
     */
    @Override
    public boolean equals(Object obj) {
        return obj instanceof Entry && Objects.equals(getKey(), ((Entry<?, ?>) obj).getKey())
                && Objects.equals(getValue(), ((Entry<?, ?>) obj).getValue());
    }

    /**
     * Returns a string representation of this {@link KeyValue}.
     *
     * @return a string representation of this {@link KeyValue}
     */
    @Override
    public String toString() {
        return getKey() + "=" + getValue();
    }
}
