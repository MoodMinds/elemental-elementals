package org.moodminds.elemental;

/**
 * Wrapping and converting {@link java.util.ListIterator} implementation of the {@link ListIterator}.
 *
 * @param <E> the type of elements
 * @param <I> the type of source wrapped {@link java.util.ListIterator}
 */
public class WrapListIterator<E, I extends java.util.ListIterator<E>>
        extends WrapRemoveIterator<E, E, I> implements ListIterator<E> {

    /**
     * Construct the object with the given {@link I} iterator.
     *
     * @param iterator the given {@link I} iterator to wrap
     */
    protected WrapListIterator(I iterator) {
        super(iterator);
    }

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

    @Override protected E getTarget(E source) {
        return source; }


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
