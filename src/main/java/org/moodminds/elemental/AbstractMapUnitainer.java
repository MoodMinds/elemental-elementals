package org.moodminds.elemental;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Comparator;
import java.util.Iterator;
import java.util.Map;
import java.util.Spliterator;
import java.util.function.Consumer;
import java.util.function.Supplier;

import static java.util.Optional.ofNullable;

/**
 * A template unique-values implementation of the {@link Container}
 * interface, which is powered by an internal {@link Map}.
 * <p>
 * The {@link RandomMatch} efficiently matches elements based on a provided example object,
 * leveraging the typically fast access of the underlying {@link Map} implementation.
 * However, there's no guarantee of consistently fast element search.
 *
 * @param <E> the element type
 * @param <M> the type of the internal {@link Map}
 */
public abstract class AbstractMapUnitainer<E, M extends Map<E, E>>
        extends AbstractContainer<E> implements RandomMatch, Serializable {

    private static final long serialVersionUID = -2001815907225641108L;

    /**
     * Backing {@link M map} holder field.
     */
    protected transient M map;

    /**
     * Construct the object with the given {@link M map}.
     *
     * @param map the given {@link M map}
     */
    protected AbstractMapUnitainer(M map) {
        this.map = map;
    }

    @Override public Iterator<E> getAll(Object o) {
        E value = map.get(o); return iterator(value, isMapped(o, value)); }
    @Override public int getCount(Object o) {
        return map.containsKey(o) ? 1 : 0; }
    @Override public int size() {
        return map.size(); }
    @Override public Iterator<E> iterator() {
        return containerIterator(map.keySet().iterator()); }
    @Override public Spliterator<E> spliterator() {
        return containerSpliterator(map.keySet().spliterator()); }

    /**
     * Return an iterator for the given value, indicating whether it is present.
     * <p>
     * This is a convenience overload of {@link #iterator(Supplier, boolean, Runnable)}
     * that does not define element removal.
     * </p>
     *
     * @param value the value to be iterated
     * @param present a flag indicating whether the value is present
     * @return an {@link Iterator} for the specified value
     */
    protected final Iterator<E> iterator(E value, boolean present) {
        return iterator(value, present, null); }

    /**
     * Return an iterator for the specified value, considering its presence and {@link Runnable} removal action.
     *
     * @param value the value to iterate over
     * @param present {@code true} if the value is present, {@code false} otherwise
     * @param removal a {@link Runnable} to execute when the value is removed during iteration
     * @return an iterator for the specified value
     */
    protected Iterator<E> iterator(E value, boolean present, Runnable removal) {
        return OptionalIterator.iterator(value, present, removal); }

    /**
     * Return an iterator that delegates its operations to the provided {@link Iterator} of keys.
     *
     * @param keysIterator the iterator over keys to be wrapped
     * @return a new {@link Iterator} that delegates to the provided {@code keysIterator}
     */
    protected Iterator<E> containerIterator(Iterator<E> keysIterator) {
        return new Iterator<E>() {
            @Override public boolean hasNext() { return keysIterator.hasNext(); }
            @Override public E next() { return keysIterator.next(); }
            @Override public void forEachRemaining(Consumer<? super E> action) {
                keysIterator.forEachRemaining(action); }
        };
    }

    /**
     * Return a {@link Spliterator} that delegates its operations to the provided {@code keysSpliterator}.
     *
     * @param keysSpliterator the {@link Spliterator} over keys to be wrapped
     * @return a new {@link Spliterator} that delegates to the provided {@code keysSpliterator}
     */
    protected Spliterator<E> containerSpliterator(Spliterator<E> keysSpliterator) {
        return new Spliterator<E>() {
            @Override public boolean tryAdvance(Consumer<? super E> action) {
                return keysSpliterator.tryAdvance(action); }
            @Override public long estimateSize() {
                return keysSpliterator.estimateSize(); }
            @Override public long getExactSizeIfKnown() {
                return keysSpliterator.getExactSizeIfKnown(); }
            @Override public void forEachRemaining(Consumer<? super E> action) {
                keysSpliterator.forEachRemaining(action); }
            @Override public Comparator<? super E> getComparator() {
                return keysSpliterator.getComparator(); }
            @Override public int characteristics() {
                return keysSpliterator.characteristics() | Spliterator.IMMUTABLE; }
            @Override public boolean hasCharacteristics(int characteristics) {
                return (characteristics & Spliterator.IMMUTABLE) != 0
                        || keysSpliterator.hasCharacteristics(characteristics); }

            @Override public Spliterator<E> trySplit() {
                return ofNullable(keysSpliterator.trySplit()).map(split -> containerSpliterator(split))
                        .orElse(null); }
        };
    }

    /**
     * Determine whether a given key-value pair is considered "mapped".
     * A key-value pair is considered mapped if the value is non-null or if the key is null
     * and the map contains an entry with a null key.
     *
     * @param key the key to check
     * @param value the value to check
     * @return {@code true} if the key-value pair is mapped, {@code false} otherwise
     */
    protected boolean isMapped(Object key, Object value) {
        return value != null || key == null && map.containsKey(null);
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
