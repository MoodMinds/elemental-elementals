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

    protected final C wrapped;

    /**
     * Construct the object with the given {@link C} collection.
     *
     * @param wrapped the given {@link C} collection to wrap
     */
    protected WrapCollection(C wrapped) {
        this.wrapped = requireNonNull(wrapped);
    }

    @Override public boolean contains(Object o) {
        return wrapped.contains(o); }
    @Override public boolean containsAll(java.util.Collection<?> c) {
        return wrapped.containsAll(c); }
    @Override public void forEach(Consumer<? super V> action) {
        wrapped.forEach(action); }
    @Override public Iterator<V> iterator() {
        return wrapped.iterator(); }
    @Override public Object[] toArray() {
        return wrapped.toArray(); }
    @Override public <T> T[] toArray(T[] a) {
        return wrapped.toArray(a); }
    @Override public Spliterator<V> spliterator() {
        return wrapped.spliterator(); }
    @Override public Stream<V> stream() {
        return wrapped.stream(); }
    @Override public Stream<V> parallelStream() {
        return wrapped.parallelStream(); }
    @Override public int size() {
        return wrapped.size(); }
    @Override public boolean addAll(java.util.Collection<? extends V> c) {
        return wrapped.addAll(c); }
    @Override public boolean removeAll(java.util.Collection<?> c) {
        return wrapped.removeAll(c); }
    @Override public boolean removeIf(Predicate<? super V> filter) {
        return wrapped.removeIf(filter); }
    @Override public boolean retainAll(java.util.Collection<?> c) {
        return wrapped.retainAll(c); }
    @Override public void clear() {
        wrapped.clear(); }
    @Override public boolean add(V v) {
        return wrapped.add(v); }
    @Override public boolean remove(Object o) {
        return wrapped.remove(o); }
    @Override public boolean isEmpty() {
        return wrapped.isEmpty(); }

    @Override protected String toStringThis() {
        return "(this Collection)"; }

    /**
     * Return wrapping {@link Collection} instance of the given {@link java.util.Collection} collection.
     *
     * @param wrapped the given {@link java.util.Collection} collection
     * @param <V> the type of elements
     * @return wrapping {@link Collection} instance of the given {@link java.util.Collection} collection
     * @throws NullPointerException if the given {@link java.util.Collection} collection is {@code null}
     */
    public static <V> Collection<V> wrap(java.util.Collection<V> wrapped) {
        return wrapped instanceof List ? WrapList.wrap(cast(wrapped))
                : wrapped instanceof Queue ? WrapQueue.wrap(cast(wrapped))
                : wrapped instanceof Set ? WrapSet.wrap(cast(wrapped))
                : new WrapCollection<>(wrapped);
    }
}
