package org.moodminds.elemental;

import java.util.Iterator;
import java.util.concurrent.BlockingDeque;

import static org.moodminds.sneaky.Cast.cast;

/**
 * Wrapping {@link java.util.Deque} implementation of the {@link Deque} interface.
 *
 * @param <E> the type of elements
 * @param <D> the type of wrapped {@link java.util.Deque}
 */
public class WrapDeque<E, D extends java.util.Deque<E>> extends WrapQueue<E, D> implements Deque<E> {

    private static final long serialVersionUID = 4089723529756102348L;

    /**
     * Construct the object with the given {@link D} queue.
     *
     * @param deque the given {@link D} queue to wrap
     */
    protected WrapDeque(D deque) { super(deque); }

    @Override public Iterator<E> getAllDescending(Object o) {
        return iterator(descendingIterator(), o); }
    @Override public void addFirst(E e) {
        wrapped.addFirst(e); }
    @Override public void addLast(E e) {
        wrapped.addLast(e); }
    @Override public boolean offerFirst(E e) {
        return wrapped.offerFirst(e); }
    @Override public boolean offerLast(E e) {
        return wrapped.offerLast(e); }
    @Override public E removeFirst() {
        return wrapped.removeFirst(); }
    @Override public E removeLast() {
        return wrapped.removeLast(); }
    @Override public E pollFirst() {
        return wrapped.pollFirst(); }
    @Override public E pollLast() {
        return wrapped.pollLast(); }
    @Override public E getFirst() {
        return wrapped.getFirst(); }
    @Override public E getLast() {
        return wrapped.getLast(); }
    @Override public E peekFirst() {
        return wrapped.peekFirst(); }
    @Override public E peekLast() {
        return wrapped.peekLast(); }
    @Override public boolean removeFirstOccurrence(Object o) {
        return wrapped.removeFirstOccurrence(o); }
    @Override public boolean removeLastOccurrence(Object o) {
        return wrapped.removeLastOccurrence(o); }
    @Override public void push(E e) {
        wrapped.push(e); }
    @Override public E pop() {
        return wrapped.pop(); }
    @Override public Iterator<E> descendingIterator() {
        return wrapped.descendingIterator(); }


    /**
     * Return wrapping {@link Deque} instance of the given {@link java.util.Deque} queue.
     *
     * @param deque the given {@link java.util.Deque} queue
     * @return wrapping {@link Deque} instance of the given {@link java.util.Deque} queue
     * @param <V> the type of elements
     * @throws NullPointerException if the given {@link java.util.Deque} queue is {@code null}
     */
    public static <V> Deque<V> wrap(java.util.Deque<V> deque) {
        return deque instanceof BlockingDeque ? WrapBlockingDeque.wrap(cast(deque))
                : new WrapDeque<>(deque);
    }
}
