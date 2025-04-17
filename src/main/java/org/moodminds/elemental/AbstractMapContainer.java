package org.moodminds.elemental;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Iterator;
import java.util.Map;
import java.util.Spliterator;
import java.util.function.Consumer;
import java.util.function.Function;

import static org.moodminds.sneaky.Cast.cast;

/**
 * A template implementation of the {@link Container} interface,
 * which allows duplicates and is powered by an internal {@link Map}.
 * <p>
 * The {@link RandomMatch} efficiently matches elements based on a provided example object,
 * leveraging the typically fast access of the underlying {@link Map} implementation.
 * However, there's no guarantee of consistently fast element search.
 *
 * @param <E> the element type
 * @param <B> the type of the internal {@link Container} bucket that holds duplicates
 * @param <M> the type of the internal {@link Map}
 */
public abstract class AbstractMapContainer<E, B extends Container<E>, M extends Map<E, Object>>
        extends AbstractContainer<E> implements RandomMatch, Serializable {

    private static final long serialVersionUID = -4343296324379369167L;

    /**
     * Backing {@link M map} holder field.
     */
    protected transient M map;

    /**
     * Construct the object with the given {@link M map}.
     *
     * @param map the given {@link M map}
     */
    protected AbstractMapContainer(M map) {
        this.map = map;
    }

    @Override public Iterator<E> getAll(Object o) {
        return tryBucket(map.get(o), this::iterator, value -> iterator(value, isMapped(o, value))); }
    @Override public int getCount(Object o) {
        return tryBucket(map.get(o), Container::size, value -> isMapped(o, value) ? 1 : 0); }
    @Override public boolean contains(Object o) {
        return map.containsKey(o); }

    @Override public boolean contains() {
        return !map.isEmpty(); }

    /**
     * {@inheritDoc}
     *
     * @return {@inheritDoc}
     */
    @Override
    public abstract Iterator<E> iterator();

    /**
     * {@inheritDoc}
     *
     * @return {@inheritDoc}
     */
    @Override
    public abstract Spliterator<E> spliterator();

    /**
     * Return an iterator over the elements of the specified {@link B} bucket.
     * By default, return the provided {@code bucketIterator} as-is.
     * Subclasses may override this method to modify or wrap the iterator.
     *
     * @param bucket the bucket containing the elements
     * @param bucketIterator the iterator for traversing the elements in the bucket
     * @return an iterator over the elements in the specified bucket
     */
    protected Iterator<E> iterator(B bucket, Iterator<E> bucketIterator) {
        return bucketIterator;
    }

    /**
     * Return an iterator over elements associated with the specified value.
     * Implementations define how the iterator is constructed based on whether
     * the value is considered present.
     *
     * @param value the value for which an iterator is requested
     * @param present {@code true} if the value is present, {@code false} otherwise
     * @return an iterator over the elements corresponding to the given value
     */
    protected abstract Iterator<E> iterator(Object value, boolean present);

    /**
     * Determine whether the specified value represents a {@link B} bucket.
     *
     * @param value the value to check
     * @return {@code true} if the value is recognized as a {@link B} bucket, {@code false} otherwise
     */
    protected abstract boolean isBucket(Object value);

    /**
     * Check if the specified key-value pair is mapped in the {@link M} map.
     *
     * @param key the key to check for mapping
     * @param value the value associated with the key
     * @return {@code true} if the key is mapped to a non-null value or the key is {@code null} and the {@link M} map contains a {@code null} key
     */
    protected boolean isMapped(Object key, Object value) {
        return value != null || key == null && map.containsKey(null);
    }

    /**
     * Attempt to process the specified value as a {@link B} bucket.
     * If the value is a bucket, the {@code consumer} is applied to the bucket.
     * If the value is not a bucket, return {@code false}.
     *
     * @param value the value to process
     * @param consumer the {@link Consumer} to apply if the value is a bucket
     * @return {@code true} if the value is a bucket and the consumer was applied, {@code false} otherwise
     */
    protected boolean tryBucket(Object value, Consumer<B> consumer) {
        return tryBucket(value, bucket -> { consumer.accept(bucket); return true; }, v -> false);
    }

    /**
     * Attempt to apply a function based on whether the provided value is a bucket.
     * If the value is a {@link B} bucket, the {@code bucketFunction} is applied to the value.
     * Otherwise, the {@code objectFunction} is applied to the value.
     *
     * @param <S> the type of the input value
     * @param <V> the type of the result
     * @param value the value to process
     * @param bucketFunction the function to apply if the value is a {@link B} bucket
     * @param objectFunction the function to apply if the value is not a {@link B} bucket
     * @return the result of applying the appropriate function based on the value type
     */
    protected <S, V> V tryBucket(S value, Function<B, V> bucketFunction, Function<S, V> objectFunction) {
        return isBucket(value) ? bucketFunction.apply(cast(value)) : objectFunction.apply(value);
    }

    /**
     * Return an iterator for the elements of the specified {@link B} bucket.
     * Delegate to another {@link #iterator(Container, Iterator)} method, passing the bucket and its iterator.
     *
     * @param bucket the bucket whose elements are to be iterated
     * @return an iterator for the elements of the specified bucket
     */
    protected Iterator<E> iterator(B bucket) {
        return iterator(bucket, bucket.iterator());
    }

    private void writeObject(ObjectOutputStream output) throws Exception {
        output.defaultWriteObject(); serialize(output);
    }

    private void readObject(ObjectInputStream input) throws Exception {
        input.defaultReadObject(); deserialize(input);
    }

    protected abstract void serialize(ObjectOutputStream output) throws Exception;

    protected abstract void deserialize(ObjectInputStream input) throws Exception;
}
