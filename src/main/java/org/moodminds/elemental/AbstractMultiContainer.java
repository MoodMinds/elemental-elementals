package org.moodminds.elemental;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Comparator;
import java.util.Iterator;
import java.util.Map;
import java.util.Spliterator;
import java.util.function.Consumer;

import static java.util.Optional.ofNullable;
import static org.moodminds.elemental.Pair.pair;
import static org.moodminds.sneaky.Cast.cast;

/**
 * Template implementation of the {@link Container} interface,
 * which allows duplicates and is powered by an internal {@link Map}.
 * <p>
 *
 * @param <E> the element type
 * @param <N> the type of the internal {@link Node}
 * @param <B> the type of the internal {@link Bucket} that holds duplicates
 * @param <M> the type of the internal {@link Map}
 */
public abstract class AbstractMultiContainer<E,
            N extends AbstractMultiContainer.Node<E, N>,
            B extends AbstractMultiContainer.Bucket<E, N, B>,
            M extends Map<E, Object>>
        extends AbstractContainer<E> implements Serializable {

    private static final long serialVersionUID = -395230885275509258L;

    /**
     * Masking {@code null} object holder field.
     */
    protected static final Object NULL = new Object();

    /**
     * Backing {@link M map} holder field.
     */
    protected final M map;

    /**
     * Container size holder field.
     */
    protected Integer size;

    /**
     * Construct the object with the given {@link M} map and size.
     *
     * @param map the given {@link M} map
     * @param size the given size
     */
    public AbstractMultiContainer(M map, Integer size) {
        this.map = map; this.size = size;
    }

    /**
     * {@inheritDoc}
     *
     * @param o {@inheritDoc}
     * @return {@inheritDoc}
     */
    @Override
    public Iterator<E> getAll(Object o) {
        Object value = map.get(o); return isBucket(value)
                ? iterator(asBucket(value))
                : iterator(unmask(value), value != null);
    }

    /**
     * {@inheritDoc}
     *
     * @param o {@inheritDoc}
     * @return {@inheritDoc}
     * @throws ClassCastException {@inheritDoc}
     * @throws NullPointerException {@inheritDoc}
     */
    @Override
    public int getCount(Object o) {
        Object value = map.get(o); return isBucket(value)
                ? asBucket(value).size
                : value != null ? 1 : 0;
    }

    /**
     * {@inheritDoc}
     *
     * @return {@inheritDoc}
     */
    @Override
    public int size() {
        if (size != null) return size;
        int size = 0; for (Object value : map.values())
            size = size + (isBucket(value) ? asBucket(value).size : 1);
        return this.size = size;
    }

    /**
     * {@inheritDoc}
     *
     * @return {@inheritDoc}
     */
    @Override
    public boolean contains() {
        return !map.isEmpty();
    }

    /**
     * {@inheritDoc}
     *
     * @return {@inheritDoc}
     */
    @Override
    public Iterator<E> iterator() {
        return iterator(map.entrySet().iterator());
    }

    /**
     * {@inheritDoc}
     *
     * @return {@inheritDoc}
     */
    @Override
    public Spliterator<E> spliterator() {
        return spliterator(map.entrySet().spliterator());
    }

    protected Iterator<E> iterator(B bucket) {
        return bucket.iterator();
    }

    protected Iterator<E> iterator(E item, boolean hasNext) {
        return new ContainerSingleIterator<>(item, hasNext);
    }

    protected Iterator<E> iterator(Iterator<Map.Entry<E, Object>> iterator) {
        return new ContainerIterator(iterator);
    }

    protected Spliterator<E> spliterator(Spliterator<Map.Entry<E, Object>> spliterator) {
        return new ContainerSpliterator(spliterator) {
            @Override protected Spliterator<E> spliterator(Spliterator<Map.Entry<E, Object>> spliterator) {
                return AbstractMultiContainer.this.spliterator(spliterator); }
        };
    }


    protected Object mask(E o) {
        return o == null ? NULL : o;
    }

    protected E unmask(Object o) {
        return cast(o == NULL ? null : o);
    }

    protected void put(E value) {
        map.merge(value, mask(value), (previous, __) -> (isBucket(previous)
                ? asBucket(previous) : newBucket().link(unmask(previous))).link(value));
    }

    protected boolean tryBucket(Object value, Consumer<B> consumer) {
        if (isBucket(value)) {
            consumer.accept(asBucket(value)); return true;
        } return false;
    }

    protected abstract B newBucket();

    protected abstract boolean isBucket(Object value);

    protected abstract B asBucket(Object value);


    /**
     * An inner duplicate elements linking container.
     *
     * @param <E> the element type
     * @param <N> the type of the internal {@link Node}
     * @param <B> the type of the internal {@link Bucket} that holds duplicates
     */
    protected abstract static class Bucket<E, N extends Node<E, N>, B extends Bucket<E, N, B>>
            implements Iterable<E>, Serializable {

        private static final long serialVersionUID = -6035284654785671662L;

        protected transient N tail; protected transient int size;

        @Override
        public Iterator<E> iterator() {
            return new BucketIterator();
        }

        /**
         * Link the given item and return container's self instance.
         *
         * @param item the given item to link
         * @return the self instance
         */
        protected B link(E item) {
            tail = link(tail, item); size++; return cast(this);
        }

        /**
         * Return a new linking {@link N node} by the specified
         * previous linking {@link N node} and the item value.
         *
         * @param prev the given previous item linking {@link N node}
         * @param item the given item value to link
         * @return the new linking {@link N node}
         */
        protected abstract N link(N prev, E item);


        protected void writeObject(ObjectOutputStream output) throws IOException {
            output.defaultWriteObject(); output.writeInt(size);
            for (N node = tail; node != null; node = node.prev)
                output.writeObject(node.item);
        }

        protected void readObject(ObjectInputStream input) throws IOException, ClassNotFoundException {
            input.defaultReadObject(); int size = input.readInt();
            for (int i = 0; i < size; i++)
                link(cast(input.readObject()));
        }


        /**
         * Container's linking {@link N node} item values {@link Iterator}.
         */
        protected class BucketIterator extends AbstractIterator<E> {

            protected N curr, next;

            protected BucketIterator() { this.next = Bucket.this.tail; }

            @Override public boolean hasNext() {
                return next != null; }
            @Override protected E element() {
                curr = next; next = curr.prev; return curr.item; }
        }
    }

    /**
     * An inner duplicate elements container's linking node.
     *
     * @param <E> the element type
     * @param <N> the type of the internal {@link Node}
     */
    protected static class Node<E, N extends Node<E, N>> {

        /**
         * Previous linking node holding field.
         */
        N prev;

        /**
         * Item value holding field.
         */
        E item;

        /**
         * Construct the object with the given previous linking {@link N node} and item value.
         *
         * @param prev the given previous linking {@link N node}
         * @param item the given item value
         */
        protected Node(N prev, E item) {
            this.prev = prev; this.item = item;
        }
    }

    /**
     * {@link Iterator} over a single value in this Container.
     */
    protected static class ContainerSingleIterator<E> extends AbstractIterator<E> {

        protected E item; protected boolean hasNext;

        protected ContainerSingleIterator(E item, boolean hasNext) {
            this.item = item; this.hasNext = hasNext; }

        @Override public boolean hasNext() {
            return hasNext; }
        @Override protected E element() {
            hasNext = false; return item; }
    }

    /**
     * {@link Iterator} over this Container.
     */
    protected class ContainerIterator extends AbstractIterator<E> {

        protected final Iterator<Map.Entry<E, Object>> iterator;

        protected Map.Entry<E, Object> entry; protected B bucket; protected Iterator<E> bucketIterator;

        protected ContainerIterator(Iterator<Map.Entry<E, Object>> iterator) {
            this.iterator = iterator;
        }

        @Override public boolean hasNext() {
            return hasBucketNext() || iterator.hasNext(); }

        @Override protected E element() {
            while (true)
                if (hasBucketNext())
                    return bucketIterator.next();
                else {
                    entry = iterator.next(); Object value = entry.getValue();
                    if (tryBucket(value, bucket -> bucketIterator = (this.bucket = bucket).iterator()))
                        continue;
                    return unmask(value);
                } }

        protected boolean hasBucketNext() {
            if (bucketIterator != null) {
                if (!bucketIterator.hasNext()) {
                    bucket = null; bucketIterator = null;
                } else return true;
            } return false;
        }
    }

    /**
     * {@link Spliterator} over this Container.
     */
    protected abstract class ContainerSpliterator implements Spliterator<E>, Consumer<Map.Entry<E, Object>> {

        protected final Spliterator<Map.Entry<E, Object>> spliterator;

        protected Map.Entry<E, Object> entry; protected B bucket; protected Spliterator<E> bucketSpliterator;

        protected ContainerSpliterator(Spliterator<Map.Entry<E, Object>> spliterator) {
            this.spliterator = spliterator;
        }

        @Override public boolean tryAdvance(Consumer<? super E> action) {
            while (true) {
                if (tryAdvanceBucket(action))
                    return true;
                if (spliterator.tryAdvance(this)) {
                    Object value = entry.getValue();
                    if (tryBucket(value, bucket -> bucketSpliterator = (this.bucket = bucket).spliterator()))
                        continue;
                    action.accept(unmask(value)); return true;
                } else return false;
            }
        }

        @Override public Spliterator<E> trySplit() {
            return ofNullable(spliterator.trySplit()).map(this::spliterator).orElse(null); }
        @Override public long estimateSize() {
            return spliterator.estimateSize(); }
        @Override public int characteristics() {
            return spliterator.characteristics() & ~DISTINCT & ~SIZED & ~SUBSIZED | IMMUTABLE; }
        @Override public Comparator<? super E> getComparator() {
            return ofNullable(spliterator.getComparator()).map(comparator -> (Comparator<E>) (e1, e2) ->
                    comparator.compare(pair(e1, NULL), pair(e2, NULL))).orElse(null); }

        @Override public void accept(Map.Entry<E, Object> entry) {
            this.entry = entry; }

        protected boolean tryAdvanceBucket(Consumer<? super E> action) {
            if (bucketSpliterator != null)
                if (!bucketSpliterator.tryAdvance(action)) {
                    bucket = null; bucketSpliterator = null;
                } else return true;
            return false;
        }

        protected abstract Spliterator<E> spliterator(Spliterator<Map.Entry<E, Object>> spliterator);
    }
}
