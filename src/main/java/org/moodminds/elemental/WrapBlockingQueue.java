package org.moodminds.elemental;

import java.util.Collection;
import java.util.concurrent.BlockingDeque;
import java.util.concurrent.TimeUnit;

import static org.moodminds.sneaky.Cast.cast;

/**
 * Wrapping {@link java.util.concurrent.BlockingQueue} implementation of the {@link BlockingQueue} interface.
 *
 * @param <E> the type of elements
 * @param <Q> the type of wrapped {@link java.util.concurrent.BlockingQueue}
 */
public class WrapBlockingQueue<E, Q extends java.util.concurrent.BlockingQueue<E>>
        extends WrapQueue<E, Q> implements BlockingQueue<E> {

    private static final long serialVersionUID = -2768058413421006881L;

    /**
     * Construct the object with the given {@link Q} queue.
     *
     * @param wrapped the given {@link Q} queue to wrap
     */
    protected WrapBlockingQueue(Q wrapped) {
        super(wrapped);
    }

    @Override public void put(E e) throws InterruptedException {
        wrapped.put(e); }
    @Override public boolean offer(E e, long timeout, TimeUnit unit) throws InterruptedException {
        return wrapped.offer(e, timeout, unit); }
    @Override public E take() throws InterruptedException {
        return wrapped.take(); }
    @Override public E poll(long timeout, TimeUnit unit) throws InterruptedException {
        return wrapped.poll(timeout, unit); }
    @Override public int remainingCapacity() {
        return wrapped.remainingCapacity(); }
    @Override public int drainTo(Collection<? super E> c) {
        return wrapped.drainTo(c); }
    @Override public int drainTo(Collection<? super E> c, int maxElements) {
        return wrapped.drainTo(c, maxElements); }

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
