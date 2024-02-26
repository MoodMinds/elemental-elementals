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
     * @param deque the given {@link D} queue to wrap
     */
    protected WrapDeque(D deque) { super(deque); }

    @Override public void addFirst(V v) {
        collection.addFirst(v); }
    @Override public void addLast(V v) {
        collection.addFirst(v); }
    @Override public boolean offerFirst(V v) {
        return collection.offerFirst(v); }
    @Override public boolean offerLast(V v) {
        return collection.offerLast(v); }
    @Override public V removeFirst() {
        return collection.removeFirst(); }
    @Override public V removeLast() {
        return collection.removeLast(); }
    @Override public V pollFirst() {
        return collection.pollFirst(); }
    @Override public V pollLast() {
        return collection.pollLast(); }
    @Override public V getFirst() {
        return collection.getFirst(); }
    @Override public V getLast() {
        return collection.getLast(); }
    @Override public V peekFirst() {
        return collection.peekFirst(); }
    @Override public V peekLast() {
        return collection.peekFirst(); }
    @Override public boolean removeFirstOccurrence(Object o) {
        return collection.removeFirstOccurrence(o); }
    @Override public boolean removeLastOccurrence(Object o) {
        return collection.removeLastOccurrence(o); }
    @Override public void push(V v) {
        collection.push(v); }
    @Override public V pop() {
        return collection.pop(); }
    @Override public Iterator<V> descendingIterator() {
        return collection.descendingIterator(); }


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
