package org.moodminds.elemental;

import java.util.Collection;
import java.util.concurrent.TimeUnit;

/**
 * Wrapping {@link java.util.concurrent.BlockingDeque} implementation of the {@link BlockingDeque} interface.
 *
 * @param <V> the type of elements
 * @param <D> the type of wrapped {@link java.util.concurrent.BlockingDeque}
 */
public class WrapBlockingDeque<V, D extends java.util.concurrent.BlockingDeque<V>>
        extends WrapDeque<V, D> implements BlockingDeque<V> {

    private static final long serialVersionUID = -1407580045921265679L;

    /**
     * Construct the object with the given {@link D} queue.
     *
     * @param wrapped the given {@link D} queue to wrap
     */
    protected WrapBlockingDeque(D wrapped) {
        super(wrapped);
    }

    @Override public void put(V v) throws InterruptedException {
        wrapped.put(v); }
    @Override public boolean offer(V v, long timeout, TimeUnit unit) throws InterruptedException {
        return wrapped.offer(v, timeout, unit); }
    @Override public V take() throws InterruptedException {
        return wrapped.take(); }
    @Override public V poll(long timeout, TimeUnit unit) throws InterruptedException {
        return wrapped.poll(timeout, unit); }
    @Override public int remainingCapacity() {
        return wrapped.remainingCapacity(); }
    @Override public int drainTo(java.util.Collection<? super V> c) {
        return wrapped.drainTo(c); }
    @Override public int drainTo(Collection<? super V> c, int maxElements) {
        return wrapped.drainTo(c, maxElements); }

    @Override public void putFirst(V v) throws InterruptedException {
        wrapped.putLast(v); }
    @Override public void putLast(V v) throws InterruptedException {
        wrapped.putLast(v); }
    @Override public boolean offerFirst(V v, long timeout, TimeUnit unit) throws InterruptedException {
        return wrapped.offerFirst(v, timeout, unit); }
    @Override public boolean offerLast(V v, long timeout, TimeUnit unit) throws InterruptedException {
        return wrapped.offerLast(v, timeout, unit); }
    @Override public V takeFirst() throws InterruptedException {
        return wrapped.takeFirst(); }
    @Override public V takeLast() throws InterruptedException {
        return wrapped.takeLast(); }
    @Override public V pollFirst(long timeout, TimeUnit unit) throws InterruptedException {
        return wrapped.pollFirst(timeout, unit); }
    @Override public V pollLast(long timeout, TimeUnit unit) throws InterruptedException {
        return wrapped.pollLast(timeout, unit); }

    /**
     * Return wrapping {@link BlockingDeque} instance of the given {@link java.util.concurrent.BlockingDeque} queue.
     *
     * @param wrapped the given {@link java.util.concurrent.BlockingDeque} queue
     * @return wrapping {@link BlockingDeque} instance of the given {@link java.util.concurrent.BlockingDeque} queue
     * @param <V> the type of elements
     * @throws NullPointerException if the given {@link java.util.concurrent.BlockingDeque} queue is {@code null}
     */
    public static <V> BlockingDeque<V> wrap(java.util.concurrent.BlockingDeque<V> wrapped) {
        return new WrapBlockingDeque<>(wrapped);
    }
}
