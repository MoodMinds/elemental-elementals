package org.moodminds.elemental;

import java.util.Iterator;
import java.util.concurrent.BlockingDeque;

import static org.moodminds.sneaky.Cast.cast;

/**
 * Wrapping {@link java.util.Deque} implementation of the {@link Deque} interface.
 *
 * @param <V> the type of elements
 * @param <D> the type of wrapped {@link java.util.Deque}
 */
public class WrapDeque<V, D extends java.util.Deque<V>> extends WrapQueue<V, D> implements Deque<V> {

    private static final long serialVersionUID = 4089723529756102348L;

    /**
     * Construct the object with the given {@link D} queue.
     *
     * @param wrapped the given {@link D} queue to wrap
     */
    protected WrapDeque(D wrapped) { super(wrapped); }

    @Override public void addFirst(V v) {
        wrapped.addFirst(v); }
    @Override public void addLast(V v) {
        wrapped.addFirst(v); }
    @Override public boolean offerFirst(V v) {
        return wrapped.offerFirst(v); }
    @Override public boolean offerLast(V v) {
        return wrapped.offerLast(v); }
    @Override public V removeFirst() {
        return wrapped.removeFirst(); }
    @Override public V removeLast() {
        return wrapped.removeLast(); }
    @Override public V pollFirst() {
        return wrapped.pollFirst(); }
    @Override public V pollLast() {
        return wrapped.pollLast(); }
    @Override public V getFirst() {
        return wrapped.getFirst(); }
    @Override public V getLast() {
        return wrapped.getLast(); }
    @Override public V peekFirst() {
        return wrapped.peekFirst(); }
    @Override public V peekLast() {
        return wrapped.peekFirst(); }
    @Override public boolean removeFirstOccurrence(Object o) {
        return wrapped.removeFirstOccurrence(o); }
    @Override public boolean removeLastOccurrence(Object o) {
        return wrapped.removeLastOccurrence(o); }
    @Override public void push(V v) {
        wrapped.push(v); }
    @Override public V pop() {
        return wrapped.pop(); }
    @Override public Iterator<V> descendingIterator() {
        return wrapped.descendingIterator(); }


    /**
     * Return wrapping {@link Deque} instance of the given {@link java.util.Deque} queue.
     *
     * @param wrapped the given {@link java.util.Deque} queue
     * @return wrapping {@link Deque} instance of the given {@link java.util.Deque} queue
     * @param <V> the type of elements
     * @throws NullPointerException if the given {@link java.util.Deque} queue is {@code null}
     */
    public static <V> Deque<V> wrap(java.util.Deque<V> wrapped) {
        return wrapped instanceof BlockingDeque ? WrapBlockingDeque.wrap(cast(wrapped))
                : new WrapDeque<>(wrapped);
    }
}
