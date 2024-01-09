package org.moodminds.elemental;

/**
 * Wrapping and converting {@link java.util.ListIterator} implementation of the {@link ListIterator}.
 *
 * @param <E> the type of elements
 * @param <I> the type of source wrapped {@link java.util.ListIterator}
 */
class WrapListIterator<E, I extends java.util.ListIterator<E>>
        extends WrapRemoveIterator<E, E, I> implements ListIterator<E> {

    /**
     * Construct the object with the given {@link I} iterator.
     *
     * @param wrapped the given {@link I} iterator to wrap
     */
    protected WrapListIterator(I wrapped) {
        super(wrapped);
    }

    @Override public void set(E e) {
        wrapped.set(e); }
    @Override public void add(E e) {
        wrapped.add(e); }
    @Override public boolean hasPrevious() {
        return wrapped.hasPrevious(); }
    @Override public E previous() {
        return wrapped.previous(); }
    @Override public int nextIndex() {
        return wrapped.nextIndex(); }
    @Override public int previousIndex() {
        return wrapped.previousIndex(); }

    @Override protected E getTarget(E source) {
        return source; }


    /**
     * Return wrapping {@link ListIterator} instance of the given source {@link java.util.ListIterator}.
     *
     * @param <V> the type of elements
     * @return wrapping {@link ListIterator} instance of the given source {@link java.util.ListIterator}
     * @throws NullPointerException if the given {@link java.util.ListIterator} list is {@code null}
     */
    static <V> ListIterator<V> wrap(java.util.ListIterator<V> iterator) {
        return new WrapListIterator<>(iterator);
    }
}
