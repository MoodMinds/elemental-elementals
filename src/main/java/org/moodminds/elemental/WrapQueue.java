package org.moodminds.elemental;

import java.util.Deque;
import java.util.concurrent.BlockingQueue;

import static org.moodminds.sneaky.Cast.cast;

/**
 * Wrapping {@link java.util.Queue} implementation of the {@link Queue} interface.
 *
 * @param <E> the type of elements
 * @param <Q> the type of wrapped {@link java.util.Queue}
 */
public class WrapQueue<E, Q extends java.util.Queue<E>> extends WrapCollection<E, Q> implements Queue<E> {

    private static final long serialVersionUID = 505202493521278051L;

    /**
     * Construct the object with the given {@link Q} queue.
     *
     * @param queue the given {@link Q} queue to wrap
     */
    protected WrapQueue(Q queue) { super(queue); }

    @Override public boolean equatable(Object obj) {
        return obj instanceof java.util.Queue; }
    @Override public E peek() {
        return wrapped.peek(); }
    @Override public E poll() {
        return wrapped.poll(); }
    @Override public E remove() {
        return wrapped.remove(); }
    @Override public E element() {
        return wrapped.element(); }
    @Override public boolean offer(E e) {
        return wrapped.offer(e); }
    @Override public int hashCode() {
        return wrapped.hashCode(); }
    @Override public boolean equals(Object obj) {
        return this == obj || (obj instanceof WrapQueue && wrapped.equals(((WrapQueue<?, ?>) obj).wrapped))
            || wrapped.equals(obj); }

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
