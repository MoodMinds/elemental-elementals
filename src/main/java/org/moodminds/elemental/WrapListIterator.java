package org.moodminds.elemental;

import java.util.function.Consumer;

import static java.util.Objects.requireNonNull;

/**
 * Wrapping and converting {@link java.util.ListIterator} implementation of the {@link ListIterator}.
 *
 * @param <E> the type of elements
 */
public class WrapListIterator<E> implements ListIterator<E> {

    /**
     * Wrapping {@link java.util.ListIterator iterator} holder field.
     */
    protected final java.util.ListIterator<E> iterator;

    /**
     * Construct the object with the given {@link java.util.ListIterator<E>} iterator.
     *
     * @param iterator the given {@link java.util.ListIterator<E>} iterator to wrap
     */
    protected WrapListIterator(java.util.ListIterator<E> iterator) {
        this.iterator = requireNonNull(iterator);
    }

    @Override public boolean hasNext() {
        return iterator.hasNext(); }
    @Override public E next() {
        return iterator.next(); }
    @Override public void remove() {
        iterator.remove(); }
    @Override public void set(E e) {
        iterator.set(e); }
    @Override public void add(E e) {
        iterator.add(e); }
    @Override public boolean hasPrevious() {
        return iterator.hasPrevious(); }
    @Override public E previous() {
        return iterator.previous(); }
    @Override public int nextIndex() {
        return iterator.nextIndex(); }
    @Override public int previousIndex() {
        return iterator.previousIndex(); }
    @Override public void forEachRemaining(Consumer<? super E> action) {
        iterator.forEachRemaining(action); }

    /**
     * Return wrapping {@link ListIterator} instance of the given source {@link java.util.ListIterator}.
     *
     * @param <V> the type of elements
     * @return wrapping {@link ListIterator} instance of the given source {@link java.util.ListIterator}
     * @throws NullPointerException if the given {@link java.util.ListIterator} list is {@code null}
     */
    public static <V> ListIterator<V> wrap(java.util.ListIterator<V> iterator) {
        return new WrapListIterator<>(iterator);
    }
}
