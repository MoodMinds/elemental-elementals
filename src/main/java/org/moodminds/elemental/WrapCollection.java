package org.moodminds.elemental;

import java.io.Serializable;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
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
 * @param <E> the type of elements
 * @param <C> the type of wrapped {@link java.util.Collection}
 */
public class WrapCollection<E, C extends java.util.Collection<E>> extends AbstractContainer<E>
        implements Collection<E>, Serializable {

    private static final long serialVersionUID = -5400019593819691818L;

    /**
     * Wrapping {@link C collection} holder field.
     */
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
    @Override public void forEach(Consumer<? super E> action) {
        wrapped.forEach(action); }
    @Override public Object[] toArray() {
        return wrapped.toArray(); }
    @Override public <T> T[] toArray(T[] a) {
        return wrapped.toArray(a); }
    @Override public Iterator<E> iterator() {
        return wrapped.iterator(); }
    @Override public Spliterator<E> spliterator() {
        return wrapped.spliterator(); }
    @Override public Stream<E> stream() {
        return wrapped.stream(); }
    @Override public Stream<E> parallelStream() {
        return wrapped.parallelStream(); }
    @Override public int size() {
        return wrapped.size(); }
    @Override public boolean addAll(java.util.Collection<? extends E> c) {
        return wrapped.addAll(c); }
    @Override public boolean removeAll(java.util.Collection<?> c) {
        return wrapped.removeAll(c); }
    @Override public boolean removeIf(Predicate<? super E> filter) {
        return wrapped.removeIf(filter); }
    @Override public boolean retainAll(java.util.Collection<?> c) {
        return wrapped.retainAll(c); }
    @Override public void clear() {
        wrapped.clear(); }
    @Override public boolean add(E e) {
        return wrapped.add(e); }
    @Override public boolean remove(Object o) {
        return wrapped.remove(o); }

    @Override protected String toStringThis() {
        return "(this Collection)"; }

    @Override
    protected Iterator<E> iterator(Iterator<E> i, Object o, Runnable removal) {
        return new Iterator<E>() {

            Iterator<?> modCheckIterator = wrapped.iterator();

            boolean moved; final Iterator<E> iterator = WrapCollection.super.iterator(i, o, () -> {
                checkMod();
                if (removal != null) removal.run();
                if (!moved) i.remove();
                else throw new IllegalStateException("The remove() method can only be called immediately after retrieving an element.");
                modCheckIterator = wrapped.iterator();
            });

            @Override public boolean hasNext() {
                boolean hasNext = iterator.hasNext(); moved = true; return hasNext; }
            @Override public E next() {
                checkMod(); E next = iterator.next(); moved = false; return next; }
            @Override public void remove() {
                iterator.remove(); }
            @Override public void forEachRemaining(Consumer<? super E> action) {
                iterator.forEachRemaining(action); moved = true; }

            void checkMod() { try { modCheckIterator.next(); } catch (NoSuchElementException ignored) {} }
        };
    }

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
