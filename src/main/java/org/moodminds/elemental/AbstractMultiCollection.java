package org.moodminds.elemental;

import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.Map;
import java.util.Spliterator;
import java.util.function.Consumer;

import static java.util.Objects.requireNonNull;
import static org.moodminds.sneaky.Cast.cast;

/**
 * Template implementation of the {@link Collection} interface,
 * which allows duplicates and is powered by an internal {@link Map}.
 *
 * @param <E> the element type
 * @param <N> the type of the internal {@link Node}
 * @param <B> the type of the internal {@link Bucket} that holds duplicates
 * @param <M> the type of the internal {@link Map}
 */
public abstract class AbstractMultiCollection<E,
            N extends AbstractMultiCollection.Node<E, N>,
            B extends AbstractMultiCollection.Bucket<E, N, B>,
            M extends Map<E, Object>>
        extends AbstractMultiContainer<E, N, B, M> implements Collection<E> {

    private static final long serialVersionUID = -7256940436987340179L;

    /**
     * Construct the object with the given {@link M} map and size.
     *
     * @param map the given {@link M} map
     * @param size the given size
     */
    public AbstractMultiCollection(M map, Integer size) {
        super(map, size);
    }

    @Override public boolean isEmpty() {
        return map.isEmpty(); }
    @Override public boolean add(E e) {
        put(e); countMod(); return true; }
    @Override public void clear() {
        if (contains()) { map.clear(); size = 0; countMod(); } }

    @Override protected Iterator<E> iterator(B bucket) {
        return new CollectionBucketIterator(bucket.iterator());}
    @Override protected Iterator<E> iterator(E item, boolean hasNext) {
        return new CollectionSingleIterator(item, hasNext); }
    @Override protected Iterator<E> iterator(Iterator<Map.Entry<E, Object>> iterator) {
        return new CollectionIterator(iterator); }
    @Override protected Spliterator<E> spliterator(Spliterator<Map.Entry<E, Object>> spliterator) {
        return new CollectionSpliterator(spliterator) {
            @Override protected Spliterator<E> spliterator(Spliterator<Map.Entry<E, Object>> spliterator) {
                return AbstractMultiCollection.this.spliterator(spliterator); }
        }; }

    @Override protected String toStringThis() {
        return "(this Collection)"; }

    protected void put(E value) {
        super.put(value); count(value, 1);
    }

    protected void count(E element, int number) {
        if (size != null) size = size + number;
    }

    protected void checkMod(int expectedMod) {
        if (totalMod() != expectedMod)
            throw new ConcurrentModificationException();
    }

    protected abstract int totalMod();

    protected abstract void countMod();


    /**
     * An inner duplicate elements linking container.
     *
     * @param <E> the element type
     * @param <N> the type of the internal {@link Node}
     * @param <B> the type of the internal {@link Bucket} that holds duplicates
     */
    protected abstract static class Bucket<E, N extends Node<E, N>, B extends Bucket<E, N, B>>
            extends AbstractMultiContainer.Bucket<E, N, B> {

        private static final long serialVersionUID = 1134195038756263659L;

        @Override
        public Iterator<E> iterator() {
            return new BucketIterator();
        }

        /**
         * Unlink the given linking {@link N node} from the container
         *
         * @param node the given linking {@link N node}
         */
        protected void unlink(N node) {

            final N next = node.next;
            final N prev = node.prev;

            if (prev != null) {
                prev.next = next;
                node.prev = null;
            }

            if (next == null)
                tail = prev;
            else {
                next.prev = prev;
                node.next = null;
            }

            node.item = null; size--;
        }


        /**
         * Container's linking {@link N node} item values {@link Iterator}.
         */
        protected class BucketIterator extends AbstractMultiContainer.Bucket<E, N, B>.BucketIterator {

            @Override
            protected Runnable removal() {
                return () -> {
                    N lastNext = curr.next; unlink(curr);
                    if (next == curr) next = lastNext;
                };
            }
        }
    }

    /**
     * An inner duplicate elements container's linking node.
     *
     * @param <E> the element type
     * @param <N> the type of the internal {@link Node}
     */
    protected static class Node<E, N extends Node<E, N>> extends AbstractMultiContainer.Node<E, N> {

        protected N next;

        /**
         * Construct the object with the given previous linking {@link N node} and item value.
         *
         * @param prev the given previous linking {@link N node}
         * @param item the given item value
         */
        protected Node(N prev, E item) {
            super(prev, item);
            if (prev != null)
                prev.next = cast(this);
        }
    }

    /**
     * {@link Iterator} over a single value in this Collection.
     */
    protected class CollectionSingleIterator extends ContainerSingleIterator<E> {

        protected int expectedMod = totalMod(); protected E next;

        protected CollectionSingleIterator(E item, boolean hasNext) {
            super(item, hasNext);
        }

        @Override public E next() {
            checkMod(expectedMod); return next = super.next(); }
        @Override protected Runnable removal() {
            return () -> {
                checkMod(expectedMod); map.remove(next);
                count(next, -1); countMod(); expectedMod++;
            }; }
    }

    /**
     * {@link Iterator} over a {@link B bucket} value items in this Collection.
     */
    protected class CollectionBucketIterator implements Iterator<E> {

        protected final Iterator<E> iterator;

        protected int expectedMod = totalMod(); protected E next;

        protected CollectionBucketIterator(Iterator<E> iterator) {
            this.iterator = iterator;
        }

        @Override public boolean hasNext() {
            return iterator.hasNext(); }
        @Override public E next() {
            checkMod(expectedMod); return iterator.next(); }
        @Override public void remove() {
            checkMod(expectedMod); iterator.remove();
            map.compute(next, (key, value) -> {
                B bucket = asBucket(value); return bucket.size > 0 ? bucket.size == 1
                        ? mask(bucket.tail.item) : value : null;
            });
            count(next, -1); countMod(); expectedMod++; }
        @Override public void forEachRemaining(Consumer<? super E> action) {
            requireNonNull(action);
            while (totalMod() == expectedMod && hasNext())
                action.accept(iterator.next());
            checkMod(expectedMod); }
    }

    /**
     * {@link Iterator} over this Collection.
     */
    protected class CollectionIterator extends ContainerIterator {

        protected int expectedMod = totalMod();

        protected CollectionIterator(Iterator<Map.Entry<E, Object>> iterator) {
            super(iterator);
        }

        @Override public E next() {
            checkMod(expectedMod); return super.next(); }
        @Override protected Runnable removal() {
            return () -> {
                checkMod(expectedMod);
                if (bucketIterator != null) {
                    bucketIterator.remove();
                    if (bucket.size == 1)
                        entry.setValue(mask(bucket.iterator().next()));
                    else if (bucket.size == 0) {
                        iterator.remove(); bucket = null; bucketIterator = null;
                    }
                } else iterator.remove();
                count(entry.getKey(), -1); countMod(); expectedMod++;
            }; }

        @Override public void forEachRemaining(Consumer<? super E> action) {
            requireNonNull(action);
            while (totalMod() == expectedMod && hasNext())
                action.accept(super.next());
            checkMod(expectedMod); }
    }

    /**
     * {@link Spliterator} over this Collection.
     */
    protected abstract class CollectionSpliterator extends ContainerSpliterator {

        protected int expectedMod = totalMod();

        protected CollectionSpliterator(Spliterator<Map.Entry<E, Object>> spliterator) {
            super(spliterator);
        }

        @Override public boolean tryAdvance(Consumer<? super E> action) {
            boolean advanced = super.tryAdvance(action); checkMod(expectedMod); return advanced; }

        @Override public void forEachRemaining(Consumer<? super E> action) {
            while (true)
                if (totalMod() != expectedMod || !super.tryAdvance(action)) break;
            checkMod(expectedMod); }

        @Override public int characteristics() {
            return super.characteristics() & ~IMMUTABLE; }
    }
}
