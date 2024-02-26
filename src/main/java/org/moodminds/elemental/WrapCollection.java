package org.moodminds.elemental;

import java.io.Serializable;
import java.util.Iterator;
import java.util.List;
import java.util.Queue;
import java.util.Set;
import java.util.Spliterator;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.Stream;

import static java.util.Objects.requireNonNull;
import static org.moodminds.sneaky.Cast.cast;

/**
 * Wrapping {@link java.util.Collection} implementation of the {@link Collection} interface.
 *
 * @param <V> the type of elements
 * @param <C> the type of wrapped {@link java.util.Collection}
 */
public class WrapCollection<V, C extends java.util.Collection<V>> extends AbstractContainer<V>
        implements Collection<V>, Serializable {

    private static final long serialVersionUID = -5400019593819691818L;

    /**
     * Wrapping {@link C collection} holder field.
     */
    protected final C collection;

    /**
     * Construct the object with the given {@link C} collection.
     *
     * @param collection the given {@link C} collection to wrap
     */
    protected WrapCollection(C collection) {
        this.collection = requireNonNull(collection);
    }

    @Override public boolean contains(Object o) {
        return collection.contains(o); }
    @Override public boolean containsAll(java.util.Collection<?> c) {
        return collection.containsAll(c); }
    @Override public void forEach(Consumer<? super V> action) {
        collection.forEach(action); }
    @Override public Iterator<V> iterator() {
        return collection.iterator(); }
    @Override public Object[] toArray() {
        return collection.toArray(); }
    @Override public <T> T[] toArray(T[] a) {
        return collection.toArray(a); }
    @Override public Spliterator<V> spliterator() {
        return collection.spliterator(); }
    @Override public Stream<V> stream() {
        return collection.stream(); }
    @Override public Stream<V> parallelStream() {
        return collection.parallelStream(); }
    @Override public int size() {
        return collection.size(); }
    @Override public boolean addAll(java.util.Collection<? extends V> c) {
        return collection.addAll(c); }
    @Override public boolean removeAll(java.util.Collection<?> c) {
        return collection.removeAll(c); }
    @Override public boolean removeIf(Predicate<? super V> filter) {
        return collection.removeIf(filter); }
    @Override public boolean retainAll(java.util.Collection<?> c) {
        return collection.retainAll(c); }
    @Override public void clear() {
        collection.clear(); }
    @Override public boolean add(V v) {
        return collection.add(v); }
    @Override public boolean remove(Object o) {
        return collection.remove(o); }
    @Override public boolean isEmpty() {
        return collection.isEmpty(); }

    @Override protected String toStringThis() {
        return "(this Collection)"; }


    /**
     * Return wrapping {@link Collection} instance of the given {@link java.util.Collection} collection.
     *
     * @param collection the given {@link java.util.Collection} collection
     * @param <V> the type of elements
     * @return wrapping {@link Collection} instance of the given {@link java.util.Collection} collection
     * @throws NullPointerException if the given {@link java.util.Collection} collection is {@code null}
     */
    public static <V> Collection<V> wrap(java.util.Collection<V> collection) {
        return collection instanceof List ? WrapList.wrap(cast(collection))
                : collection instanceof Queue ? WrapQueue.wrap(cast(collection))
                : collection instanceof Set ? WrapSet.wrap(cast(collection))
                : new WrapCollection<>(collection);
    }
}
