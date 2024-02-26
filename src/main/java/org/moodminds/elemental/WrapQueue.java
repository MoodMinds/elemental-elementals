package org.moodminds.elemental;

import java.util.Deque;
import java.util.concurrent.BlockingQueue;

import static org.moodminds.sneaky.Cast.cast;

/**
 * Wrapping {@link java.util.Queue} implementation of the {@link Queue} interface.
 *
 * @param <V> the type of elements
 * @param <Q> the type of wrapped {@link java.util.Queue}
 */
public class WrapQueue<V, Q extends java.util.Queue<V>> extends WrapCollection<V, Q> implements Queue<V> {

    private static final long serialVersionUID = 505202493521278051L;

    /**
     * Construct the object with the given {@link Q} queue.
     *
     * @param queue the given {@link Q} queue to wrap
     */
    protected WrapQueue(Q queue) { super(queue); }

    @Override public boolean equatable(Object obj) {
        return obj instanceof java.util.Queue; }
    @Override public V peek() {
        return collection.peek(); }
    @Override public V poll() {
        return collection.poll(); }
    @Override public V remove() {
        return collection.remove(); }
    @Override public V element() {
        return collection.element(); }
    @Override public boolean offer(V v) {
        return collection.offer(v); }
    @Override public int hashCode() {
        return collection.hashCode(); }
    @Override public boolean equals(Object obj) {
        return this == obj || (obj instanceof WrapQueue && collection.equals(((WrapQueue<?, ?>) obj).collection))
            || collection.equals(obj); }

    /**
     * Return wrapping {@link Queue} instance of the given {@link java.util.Queue} queue.
     *
     * @param queue the given {@link java.util.Queue} queue
     * @return wrapping {@link Queue} instance of the given {@link java.util.Queue} queue
     * @param <V> the type of elements
     * @throws NullPointerException if the given {@link java.util.Queue} queue is {@code null}
     */
    public static <V> Queue<V> wrap(java.util.Queue<V> queue) {
        return queue instanceof Deque ? WrapDeque.wrap(cast(queue))
                : queue instanceof BlockingQueue ? WrapBlockingQueue.wrap(cast(queue))
                : new WrapQueue<>(queue);
    }
}
