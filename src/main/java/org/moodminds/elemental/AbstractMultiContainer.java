package org.moodminds.elemental;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Comparator;
import java.util.Iterator;
import java.util.Map;
import java.util.Optional;
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

    private static final long serialVersionUID = -2859472909964515355L;

    /**
     * Backing {@link M map} holder field.
     */
    protected transient M map;

    /**
     * Construct the object with the given {@link M} map.
     *
     * @param map the given {@link M} map
     */
    protected AbstractMultiContainer(M map) {
        this.map = map;
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
                : iterator(value, value != null || o == null && map.containsKey(null));
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
                : value != null || o == null && map.containsKey(null) ? 1 : 0;
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
        return spliterator(map.entrySet().spliterator(), getSize().orElse(null));
    }

    protected Iterator<E> iterator(B bucket) {
        return bucket.iterator();
    }

    protected Iterator<E> iterator(Object value, boolean hasNext) {
        return hasNext ? SingleIterator.iterator(cast(value)) : EmptyIterator.iterator();
    }

    protected Iterator<E> iterator(Iterator<Map.Entry<E, Object>> iterator) {
        return new ContainerIterator(iterator);
    }

    protected Spliterator<E> spliterator(Spliterator<Map.Entry<E, Object>> spliterator, Integer size) {
        return new ContainerSpliterator(spliterator, size) {
            @Override protected Spliterator<E> spliterator(Spliterator<Map.Entry<E, Object>> spliterator) {
                return AbstractMultiContainer.this.spliterator(spliterator, null); }
        };
    }

    protected B asBucket(Object value) {
        return cast(value);
    }

    protected abstract boolean isMulti();

    protected abstract boolean isBucket(Object value);

    protected abstract Optional<Integer> getSize();

    protected void serialiaze(ObjectOutputStream output) throws Exception {
        output.writeObject(map);
    }

    protected void deserialize(ObjectInputStream input) throws Exception {
        map = cast(input.readObject());
    }

    private void writeObject(ObjectOutputStream output) throws Exception {
        output.defaultWriteObject(); serialiaze(output);
    }

    private void readObject(ObjectInputStream input) throws Exception {
        input.defaultReadObject(); deserialize(input);
    }


    /**
     * An inner duplicate elements container's node.
     *
     * @param <E> the element type
     * @param <N> the type of the internal {@link Node}
     */
    protected static class Node<E, N extends Node<E, N>> implements Serializable {

        private static final long serialVersionUID = 6423883202670048771L;

        /**
         * Next {@link N} node holding field.
         */
        protected transient N next;

        /**
         * Item {@link E} value holding field.
         */
        protected transient E item;

        /**
         * Construct the object with the given item value.
         *
         * @param item the given item value
         */
        protected Node(E item) {
            this.item = item;
        }

        protected void serialize(ObjectOutputStream output) throws Exception {
            output.writeObject(next);
            output.writeObject(item);
        }

        protected void deserialize(ObjectInputStream input) throws Exception {
            next = cast(input.readObject());
            item = cast(input.readObject());
        }

        private void writeObject(ObjectOutputStream output) throws Exception {
            output.defaultWriteObject(); serialize(output);
        }

        private void readObject(ObjectInputStream input) throws Exception {
            input.defaultReadObject(); deserialize(input);
        }
    }

    /**
     * An inner duplicate elements linking container.
     *
     * @param <E> the element type
     * @param <N> the type of the internal {@link Node}
     * @param <B> the type of the internal {@link Bucket} that holds duplicates
     */
    protected abstract static class Bucket<E, N extends Node<E, N>, B extends Bucket<E, N, B>>
            implements Iterable<E>, Serializable {

        private static final long serialVersionUID = -4264492137323652367L;

        /**
         * Head {@link N} node holding field.
         */
        protected transient N head;

        /**
         * Bucket size holding field.
         */
        protected transient int size;

        /**
         * Construct the object with the given {@link N} head node.
         *
         * @param head the given {@link N} head node
         */
        protected Bucket(N head) {
            this.head = head; this.size = 1;
        }

        @Override
        public Iterator<E> iterator() {
            return new HeadIterator();
        }

        protected void serialize(ObjectOutputStream output) throws Exception {
            output.writeInt(size);
            output.writeObject(head);
        }

        protected void deserialize(ObjectInputStream input) throws Exception {
            size = input.readInt();
            head = cast(input.readObject());
        }

        private void writeObject(ObjectOutputStream output) throws Exception {
            output.defaultWriteObject(); serialize(output);
        }

        private void readObject(ObjectInputStream input) throws Exception {
            input.defaultReadObject(); deserialize(input);
        }

        /**
         * The {@link B bucket}'s item values {@link Iterator}.
         */
        protected abstract class BucketIterator extends AbstractLinkIterator<E, N> {

            protected BucketIterator(N next) {
                super(next); }

            @Override protected E value(N link) {
                return link.item; }
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
    }

    /**
     * {@link Iterator} over this Container.
     */
    protected class ContainerIterator extends AbstractIterator<E> {

        protected final Iterator<Map.Entry<E, Object>> iterator;

        protected Iterator<E> flattenIterator;

        protected ContainerIterator(Iterator<Map.Entry<E, Object>> iterator) {
            this.iterator = iterator;
        }

        @Override public boolean hasNext() {
            return hasFlattenNext() || iterator.hasNext(); }

        @Override protected E nextElement() {
            while (true)
                if (hasFlattenNext())
                    return flattenIterator.next();
                else {
                    Object value = iterator.next().getValue();
                    if (tryFlatten(value))
                        continue;
                    return cast(value);
                } }

        protected boolean tryFlatten(Object value) {
            if (isBucket(value)) {
                flattenIterator = asBucket(value).iterator(); return true;
            } return false;
        }

        private boolean hasFlattenNext() {
            if (flattenIterator != null) {
                if (!flattenIterator.hasNext()) {
                    flattenIterator = null;
                } else return true;
            } return false;
        }
    }

    /**
     * {@link Spliterator} over this Container.
     */
    protected abstract class ContainerSpliterator implements Spliterator<E> {

        protected final Spliterator<Map.Entry<E, Object>> spliterator; protected Integer size;

        protected Spliterator<E> flattenSpliterator;

        protected ContainerSpliterator(Spliterator<Map.Entry<E, Object>> spliterator, Integer size) {
            this.spliterator = spliterator; this.size = size;
        }

        @Override public boolean tryAdvance(Consumer<? super E> action) {
            while (true) {
                if (tryAdvanceBucket(action))
                    return true;
                Object[] value = new Object[]{null};
                if (spliterator.tryAdvance(entry -> value[0] = entry.getValue())) {
                    if (tryFlatten(value[0]))
                        continue;
                    action.accept(cast(value[0])); return true;
                } else return false;
            }
        }

        @Override public Spliterator<E> trySplit() {
            return ofNullable(spliterator.trySplit()).map(this::spliterator).orElse(null); }
        @Override public long estimateSize() {
            return !isMulti() || size == null ? spliterator.estimateSize() : size; }
        @Override public int characteristics() {
            return spliterator.characteristics()
                    & (isMulti() ? ~DISTINCT : -1)
                    & (isMulti() ? size == null ? ~SIZED : -1 : -1)
                    & (isMulti() ? ~SUBSIZED : -1)
                    | IMMUTABLE; }
        @Override public Comparator<? super E> getComparator() {
            return ofNullable(spliterator.getComparator()).map(comparator -> (Comparator<E>) (e1, e2) ->
                    comparator.compare(pair(e1, null), pair(e2, null))).orElse(null); }

        protected abstract Spliterator<E> spliterator(Spliterator<Map.Entry<E, Object>> spliterator);

        protected boolean tryFlatten(Object value) {
            if (isBucket(value)) {
                flattenSpliterator = asBucket(value).spliterator(); return true;
            } return false;
        }

        private boolean tryAdvanceBucket(Consumer<? super E> action) {
            if (flattenSpliterator != null)
                if (!flattenSpliterator.tryAdvance(action)) {
                    flattenSpliterator = null;
                } else return true;
            return false;
        }
    }
}
