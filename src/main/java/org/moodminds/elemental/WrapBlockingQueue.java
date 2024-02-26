package org.moodminds.elemental;

import java.util.Collection;
import java.util.concurrent.BlockingDeque;
import java.util.concurrent.TimeUnit;

import static org.moodminds.sneaky.Cast.cast;

/**
 * Wrapping {@link java.util.concurrent.BlockingQueue} implementation of the {@link BlockingQueue} interface.
 *
 * @param <V> the type of elements
 * @param <Q> the type of wrapped {@link java.util.concurrent.BlockingQueue}
 */
public class WrapBlockingQueue<V, Q extends java.util.concurrent.BlockingQueue<V>>
        extends WrapQueue<V, Q> implements BlockingQueue<V> {

    private static final long serialVersionUID = -2768058413421006881L;

    /**
     * Construct the object with the given {@link Q} queue.
     *
     * @param wrapped the given {@link Q} queue to wrap
     */
    protected WrapBlockingQueue(Q wrapped) {
        super(wrapped);
    }

    @Override public void put(V v) throws InterruptedException {
        collection.put(v); }
    @Override public boolean offer(V v, long timeout, TimeUnit unit) throws InterruptedException {
        return collection.offer(v, timeout, unit); }
    @Override public V take() throws InterruptedException {
        return collection.take(); }
    @Override public V poll(long timeout, TimeUnit unit) throws InterruptedException {
        return collection.poll(timeout, unit); }
    @Override public int remainingCapacity() {
        return collection.remainingCapacity(); }
    @Override public int drainTo(Collection<? super V> c) {
        return collection.drainTo(c); }
    @Override public int drainTo(Collection<? super V> c, int maxElements) {
        return collection.drainTo(c, maxElements); }

    /**
     * Return wrapping {@link BlockingQueue} instance of the given {@link java.util.concurrent.BlockingQueue} queue.
     *
     * @param wrapped the given {@link java.util.concurrent.BlockingQueue} queue
     * @return wrapping {@link BlockingQueue} instance of the given {@link java.util.concurrent.BlockingQueue} queue
     * @param <V> the type of elements
     * @throws NullPointerException if the given {@link java.util.concurrent.BlockingQueue} queue is {@code null}
     */
    public static <V> BlockingQueue<V> wrap(java.util.concurrent.BlockingQueue<V> wrapped) {
        return wrapped instanceof BlockingDeque ? WrapBlockingDeque.wrap(cast(wrapped))
                : new WrapBlockingQueue<>(wrapped);
    }
}
