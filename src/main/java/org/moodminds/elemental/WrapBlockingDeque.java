package org.moodminds.elemental;

import java.util.Collection;
import java.util.concurrent.TimeUnit;

/**
 * Wrapping {@link java.util.concurrent.BlockingDeque} implementation of the {@link BlockingDeque} interface.
 *
 * @param <E> the type of elements
 * @param <D> the type of wrapped {@link java.util.concurrent.BlockingDeque}
 */
public class WrapBlockingDeque<E, D extends java.util.concurrent.BlockingDeque<E>>
        extends WrapDeque<E, D> implements BlockingDeque<E> {

    private static final long serialVersionUID = -1407580045921265679L;

    /**
     * Construct the object with the given {@link D} queue.
     *
     * @param wrapped the given {@link D} queue to wrap
     */
    protected WrapBlockingDeque(D wrapped) {
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
    @Override public int drainTo(java.util.Collection<? super E> c) {
        return wrapped.drainTo(c); }
    @Override public int drainTo(Collection<? super E> c, int maxElements) {
        return wrapped.drainTo(c, maxElements); }

    @Override public void putFirst(E e) throws InterruptedException {
        wrapped.putLast(e); }
    @Override public void putLast(E e) throws InterruptedException {
        wrapped.putLast(e); }
    @Override public boolean offerFirst(E e, long timeout, TimeUnit unit) throws InterruptedException {
        return wrapped.offerFirst(e, timeout, unit); }
    @Override public boolean offerLast(E e, long timeout, TimeUnit unit) throws InterruptedException {
        return wrapped.offerLast(e, timeout, unit); }
    @Override public E takeFirst() throws InterruptedException {
        return wrapped.takeFirst(); }
    @Override public E takeLast() throws InterruptedException {
        return wrapped.takeLast(); }
    @Override public E pollFirst(long timeout, TimeUnit unit) throws InterruptedException {
        return wrapped.pollFirst(timeout, unit); }
    @Override public E pollLast(long timeout, TimeUnit unit) throws InterruptedException {
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
