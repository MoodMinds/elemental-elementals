package org.moodminds.elemental;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
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

    private static final long serialVersionUID = -5644048732017255222L;

    /**
     * Construct the object with the given {@link M} map.
     *
     * @param map the given {@link M} map
     */
    protected AbstractMultiCollection(M map) {
        super(map);
    }

    @Override public boolean add(E e) {
        put(e); countMod(); return true; }
    @Override public void clear() {
        map.clear(); countMod(); }
    @Override public boolean isEmpty() {
        return map.isEmpty(); }

    @Override protected Iterator<E> iterator(B bucket) {
        return new CollectionBucketIterator(bucket.iterator()); }
    @Override protected Iterator<E> iterator(Object value, boolean hasNext) {
        return hasNext ? new CollectionSingleIterator(cast(value)) : new CollectionEmptyIterator(); }
    @Override protected Iterator<E> iterator(Iterator<Map.Entry<E, Object>> iterator) {
        return new CollectionIterator(iterator); }
    @Override protected Spliterator<E> spliterator(Spliterator<Map.Entry<E, Object>> spliterator, Integer size) {
        return new CollectionSpliterator(spliterator, size) {
            @Override protected Spliterator<E> spliterator(Spliterator<Map.Entry<E, Object>> spliterator) {
                return AbstractMultiCollection.this.spliterator(spliterator, null); }
        };
    }

    @Override protected String toStringThis() {
        return "(this Collection)"; }

    protected void put(E e) {
        Object value = map.get(e);
        if (value == null && (e != null || !map.containsKey(null)))
            map.put(e, value(e));
        else {
            B bucket;
            if (isBucket(value)) bucket = asBucket(value);
            else map.put(cast(value), bucket = bucket(node(cast(value))));
            bucket.add(node(e));
        }
    }

    protected Object value(E e) {
        return e;
    }

    protected abstract N node(E value);

    protected abstract B bucket(N head);

    protected void checkMod(int expectedMod) {
        if (totalMod() != expectedMod)
            throw new ConcurrentModificationException();
    }

    protected abstract int totalMod();

    protected abstract void countMod();


    /**
     * An inner duplicate elements container's node.
     *
     * @param <E> the element type
     * @param <N> the type of the internal {@link Node}
     */
    protected static class Node<E, N extends Node<E, N>>
            extends AbstractMultiContainer.Node<E, N> {

        private static final long serialVersionUID = 8385101652280335435L;

        /**
         * Previous {@link N} node holding field.
         */
        protected transient N prev;

        /**
         * Construct the object with the given item value.
         *
         * @param item the given item value
         */
        protected Node(E item) {
            super(item);
        }

        @Override
        protected void serialize(ObjectOutputStream output) throws Exception {
            super.serialize(output); output.writeObject(prev);
        }

        @Override
        protected void deserialize(ObjectInputStream input) throws Exception {
            super.deserialize(input); prev = cast(input.readObject());
        }
    }

    /**
     * An inner duplicate elements container.
     *
     * @param <E> the element type
     * @param <N> the type of the internal {@link Node}
     * @param <B> the type of the internal {@link Bucket} that holds duplicates
     */
    protected static class Bucket<E, N extends Node<E, N>, B extends Bucket<E, N, B>>
            extends AbstractMultiContainer.Bucket<E, N, B> {

        private static final long serialVersionUID = -1109448213030786254L;

        /**
         * Tail {@link N} node holding field.
         */
        protected transient N tail;

        /**
         * Construct the object with the given {@link N} head node.
         *
         * @param head the given {@link N} head node
         */
        protected Bucket(N head) {
            super(head); tail = head;
        }

        @Override
        public BucketIterator iterator() {
            return new HeadIterator();
        }

        /**
         * Add the given {@link N} node as next.
         *
         * @param node the given {@link N} node to link
         */
        protected void add(N node) {
            tail.next = node; node.prev = tail; tail = node; size++;
        }

        /**
         * Remove the given {@link N} node.
         *
         * @param node the given {@link N} node to unlink
         */
        protected void remove(N node) {

            final N next = node.next, prev = node.prev;

            if (prev == null) head = next;
            else {
                prev.next = next; node.prev = null;
            }

            if (next == null) tail = prev;
            else {
                next.prev = prev; node.next = null;
            }

            node.item = null; size--;
        }

        @Override
        protected void serialize(ObjectOutputStream output) throws Exception {
            super.serialize(output); output.writeObject(tail);
        }

        @Override
        protected void deserialize(ObjectInputStream input) throws Exception {
            super.deserialize(input); tail = cast(input.readObject());
        }


        /**
         * The {@link B bucket}'s item values {@link Iterator}.
         */
        protected abstract class BucketIterator
                extends AbstractMultiContainer.Bucket<E, N, B>.BucketIterator {

            protected Bucket<E, N, B> bucket = Bucket.this;

            protected BucketIterator(N next) {
                super(next); }

            @Override protected void removeElement() {
                N lastNext = next(curr); bucket.remove(curr);
                if (next == curr) next = lastNext; }
        }

        /**
         * The {@link B bucket}'s item values head {@link Iterator}.
         */
        protected class HeadIterator extends BucketIterator {

            protected HeadIterator() {
                super(head); }

            @Override protected N next(N link) {
                return link.next; }
        }

        /**
         * The {@link B bucket}'s item values tail {@link Iterator}.
         */
        protected class TailIterator extends BucketIterator {

            protected TailIterator() {
                super(tail); }

            @Override protected N next(N link) {
                return link.prev; }
        }
    }

    /**
     * Empty {@link Iterator} in this Collection.
     */
    protected class CollectionEmptyIterator extends EmptyIterator<E> {

        protected int expectedMod = totalMod();

        @Override public E next() {
            checkMod(expectedMod); return super.next(); }
        @Override protected void removeElement() {
            /* will never happen */ }
    }

    /**
     * {@link Iterator} over a single value in this Collection.
     */
    protected class CollectionSingleIterator extends SingleIterator<E> {

        protected int expectedMod = totalMod();

        protected CollectionSingleIterator(E item) {
            super(item);
        }

        @Override public E next() {
            checkMod(expectedMod); return super.next(); }
        @Override protected void removeElement() {
            checkMod(expectedMod); map.remove(next);
            countMod(); expectedMod++; }
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
            checkMod(expectedMod); return next = iterator.next(); }
        @Override public void remove() {
            checkMod(expectedMod); iterator.remove();
            Bucket<E, N, B> bucket = ((Bucket<E, N, B>.BucketIterator) iterator).bucket;
            if (bucket.size == 0) map.remove(next);
            else if (bucket.size == 1) map.put(next, bucket.head.item);
            countMod(); expectedMod++; }
        @Override public void forEachRemaining(Consumer<? super E> action) {
            requireNonNull(action);
            while (totalMod() == expectedMod && hasNext())
                action.accept(iterator.next());
            checkMod(expectedMod); }
    }

    /**
     * {@link Iterator} over this Collection.
     */
    protected class CollectionIterator extends AbstractMultiContainer<E, N, B, M>.ContainerIterator {

        protected int expectedMod = totalMod();

        protected CollectionIterator(Iterator<Map.Entry<E, Object>> iterator) {
            super(new EntryIterator<>(iterator));
        }

        @Override public E next() {
            checkMod(expectedMod); return super.next(); }
        @Override protected void removeElement() {
            checkMod(expectedMod);
            if (flattenIterator != null) {
                flattenIterator.remove();
                Bucket<E, N, B> bucket = ((Bucket<E, N, B>.BucketIterator) flattenIterator).bucket;
                if (bucket.size == 1)
                    ((EntryIterator<E>) iterator).entry.setValue(bucket.head.item);
                else if (bucket.size == 0) {
                    iterator.remove(); flattenIterator = null;
                }
            } else iterator.remove();
            countMod(); expectedMod++; }

        @Override public void forEachRemaining(Consumer<? super E> action) {
            requireNonNull(action);
            while (totalMod() == expectedMod && hasNext())
                action.accept(super.next());
            checkMod(expectedMod); }
    }

    private static class EntryIterator<E> implements Iterator<Map.Entry<E, Object>> {

        final Iterator<Map.Entry<E, Object>> iterator; Map.Entry<E, Object> entry;

        EntryIterator(Iterator<Map.Entry<E, Object>> iterator) {
            this.iterator = iterator;
        }

        @Override public boolean hasNext() { return iterator.hasNext(); }
        @Override public Map.Entry<E, Object> next() { return entry = iterator.next(); }
        @Override public void remove() { iterator.remove(); }
    }

    /**
     * {@link Spliterator} over this Collection.
     */
    protected abstract class CollectionSpliterator extends AbstractMultiContainer<E, N, B, M>.ContainerSpliterator {

        protected int expectedMod = totalMod();

        protected CollectionSpliterator(Spliterator<Map.Entry<E, Object>> spliterator, Integer size) {
            super(spliterator, size);
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
