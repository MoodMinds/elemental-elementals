package org.moodminds.elemental;

import org.moodminds.elemental.LinkHashContainer.Bucket;
import org.moodminds.elemental.LinkHashContainer.Bucket.NodeNode;
import org.moodminds.elemental.LinkHashContainer.Bucket.LinkNodeNode;
import org.moodminds.sneaky.Cast;

import java.io.InvalidObjectException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.stream.Stream;

import static java.lang.Math.max;
import static java.util.Spliterator.*;
import static org.moodminds.elemental.Producer.producer;
import static org.moodminds.sneaky.Cast.cast;
import static org.moodminds.sneaky.Sneak.sneak;

/**
 * A {@link LinkedHashMap}-powered implementation of the {@link Container} interface,
 * preserving the order of elements as defined during construction.
 *
 * @param <E> the element type
 */
public class LinkHashContainer<E> extends AbstractMapContainer<E, Bucket<E>, Map<E, Object>> {

    private static final long serialVersionUID = -7910725201009294081L;

    /**
     * Container size holder field.
     */
    protected transient int size;

    /**
     * Construct the object with the given elements array.
     *
     * @param elements the given elements array
     */
    @SafeVarargs
    public LinkHashContainer(E... elements) {
        this(new LinkedHashMap<>(capacity(elements.length)), producer(elements));
    }

    /**
     * Construct the object with the given elements {@link Stream}.
     *
     * @param elements the given elements {@link Stream}
     */
    public LinkHashContainer(Stream<? extends E> elements) {
        this(new LinkedHashMap<>(), elements.sequential()::forEach);
    }

    /**
     * Construct the object with the given elements {@link Container}.
     *
     * @param elements the given elements {@link Container}
     */
    public LinkHashContainer(Container<? extends E> elements) {
        this(new LinkedHashMap<>(capacity(elements.size())), elements::forEach);
    }

    /**
     * Construct the object with the given elements {@link java.util.Collection}.
     *
     * @param elements the given elements {@link java.util.Collection}
     */
    public LinkHashContainer(java.util.Collection<? extends E> elements) {
        this(new LinkedHashMap<>(capacity(elements.size())), elements::forEach);
    }

    /**
     * Construct the object with the given elements {@link Collection}.
     *
     * @param elements the given elements {@link Collection}
     */
    public LinkHashContainer(Collection<? extends E> elements) {
        this((java.util.Collection<? extends E>) elements);
    }

    /**
     * Construct the object with the given {@link LinkedHashMap map}
     * and sequential single-threaded {@link Producer} of elements.
     *
     * @param map the given {@link LinkedHashMap map}
     * @param elements the given sequential single-threaded {@link Producer} of elements
     */
    protected LinkHashContainer(LinkedHashMap<E, Object> map, Producer<? extends E> elements) {
        super(map); init(elements);
    }

    /**
     * Initialize the inner {@link LinkedHashMap map} of this Container with the specified
     * sequential single-threaded {@link Producer} of elements.
     *
     * @param elements the specified sequential single-threaded {@link Producer} of elements
     */
    protected void init(Producer<? extends E> elements) {
        elements.provide(new Consumer<E>() {

            final Map<Bucket<E>, Bucket.Node<E>> tails = new IdentityHashMap<>(); Bucketing bucketing;

            @Override public void accept(E element) {
                Object value = map.get(element);
                if (!tryBucket(value, bucket -> bucketing.add(bucket, element)))
                    if (value instanceof Node)
                        bucketing.add(Cast.<Node<E>>cast(value), element);
                    else if (isMapped(element, value))
                        bucketing.add(Cast.<E>cast(value), element);
                    else {
                        bucketing = new ValueBucketing(element); map.put(element, element);
                    }
                size++; }

            /**
             * Strategy for handling insertion of an element that is already present in the container.
             * <p>
             * Implementations define how to merge or bucket the new {@code element}
             * with an existing occurrence, represented by either a raw value, node, or bucket.
             */
            abstract class Bucketing {

                /**
                 * Merge the given element with an existing value already present in the container.
                 *
                 * @param value the existing value
                 * @param element the element to add
                 */
                abstract void add(E value, E element);

                /**
                 * Merge the given element with an existing {@link Node} already present in the container.
                 *
                 * @param value the {@link Node} representing the existing value
                 * @param element the element to add
                 */
                abstract void add(Node<E> value, E element);

                /**
                 * Merge the given element into an existing {@link Bucket} already associated with the value.
                 *
                 * @param value the {@link Bucket} representing the existing value
                 * @param element the element to add
                 */
                abstract void add(Bucket<E> value, E element);
            }

            /**
             * Bucketing algorithm for adding an element after a unique predecessor.
             */
            class ValueBucketing extends Bucketing {

                final E last;

                ValueBucketing(E last) { this.last = last; }

                @Override void add(E value, E element) {
                    bucketing = new MultiBucketing() {{
                        if (value == last)
                            map.put(value, bucket = new Bucket<>(value, element, (LinkNodeNode<E>) (head, tail) -> {
                                previous = head; before = head; next = tail;
                            }));
                        else map.put(value, bucket = new Bucket<>(value, element, (NodeNode<E>) (head, tail) -> {
                            previous = head; map.put(last, before = new NodeLink<>(last, next = tail));
                        })); tails.put(bucket, next);
                    }}; }

                @Override void add(Node<E> value, E element) {
                    bucketing = new MultiBucketing() {{
                        map.put(value.item(), bucket = new Bucket<>(value.item(), value.next(), element, (head, tail) -> {
                            previous = head; map.put(last, before = new NodeLink<>(last, next = tail));
                        })); tails.put(bucket, next);
                    }}; }

                @Override void add(Bucket<E> value, E element) {
                    bucketing = new MultiBucketing() {{
                        tails.compute((bucket = value), (bucket, tail) -> {
                            bucket.put(tail, element, (previous, next) -> {
                                this.previous = previous; map.put(last, before = new NodeLink<>(last, this.next = next));
                            }); return next;
                        });
                    }}; }
            }

            /**
             * Bucketing algorithm for adding an element after a non-unique predecessor.
             */
            class MultiBucketing extends Bucketing {

                Bucket<E> bucket; Bucket.Node<E> previous, next; Link<E> before;

                @Override void add(E value, E element) {
                    map.put(value, bucket = new Bucket<>(value, element, (NodeNode<E>) (head, tail) ->
                        this.bucket.link(previous, before, tail, (previous, next) -> {
                            this.previous = head; before = previous; this.next = next; tails.put(bucket, previous);
                        })
                    )); tails.put(bucket, next); }

                @Override void add(Node<E> value, E element) {
                    map.put(value.item(), bucket = new Bucket<>(value.item(), value.next(), element, (head, tail) ->
                        this.bucket.link(previous, before == value ? head : before, tail, (previous, next) -> {
                            this.previous = head; before = previous; this.next = next; tails.put(bucket, previous);
                        })
                    )); tails.put(bucket, next); }

                @Override void add(Bucket<E> value, E element) {
                    if (bucket == value)
                        bucket.put(previous, before, element, (previous, next) -> {
                            this.previous = previous; before = previous; this.next = next;
                        });
                    else value.put(tails.get(value), element, (previous, next) -> {
                            bucket.link(this.previous, before, next, (before, unused) -> {
                                this.previous = previous; this.before = before; this.next = next; tails.put(bucket, before);
                            }); bucket = value;
                        });
                    tails.put(bucket, next); }
            }
        });
    }

    @Override public int size() {
        return size; }

    @Override public Iterator<E> iterator() {
        return new AbstractIterator<E>() {

            final Iterator<Map.Entry<E, Object>> entriesIterator = map.entrySet().iterator(); Node<E> node;

            @Override protected boolean hasNextElement() {
                return node != null || entriesIterator.hasNext(); }

            @Override protected E nextElement() {
                while (true) {
                    if (node != null) {
                        E element = node.item(); node = node.next(); return element; }
                    Object value = entriesIterator.next().getValue();
                    if (tryBucket(value, bucket -> node = bucket.head))
                        continue;
                    else if (value instanceof Node) {
                        node = cast(value); continue;
                    } return cast(value);
                } }
        }; }

    @Override public Spliterator<E> spliterator() {
        return Spliterators.spliterator(iterator(), size, ORDERED | IMMUTABLE
                | (size > map.size() ? 0 : DISTINCT)); }

    @Override protected Iterator<E> iterator(Object value, boolean present) {
        return OptionalIterator.iterator(() -> cast(value instanceof Node
                ? ((Node<?>) value).item() : value), present); }

    @Override protected boolean isBucket(Object value) {
        return value instanceof Bucket; }

    @Override
    protected void serialize(ObjectOutputStream output) throws Exception {
        output.writeInt(size); for (E e : this)
            output.writeObject(e);
    }

    @Override
    protected void deserialize(ObjectInputStream input) throws Exception {
        int size; if ((size = input.readInt()) < 0)
            throw new InvalidObjectException("Negative size: " + size);
        map = new LinkedHashMap<>(capacity(size)); init(consumer -> {
            try {
                while (this.size < size)
                    consumer.accept(cast(input.readObject()));
            } catch (Exception e) { sneak(e); }
        });
    }

    /**
     * Represents a node in this Container referencing to the next {@link Node}.
     *
     * @param <E> the type of element
     */
    protected interface Node<E> {

        /**
         * Return the element stored in this node.
         *
         * @return the stored element
         */
        E item();

        /**
         * Return the next {@link Node} in the sequence.
         *
         * @return the next {@link Node}, or {@code null} if none exists
         */
        Node<E> next();
    }

    /**
     * Represents a linking node in this Container referencing to the next {@link Node}.
     *
     * @param <E> the type of element
     */
    protected interface Link<E> extends Node<E> {

        /**
         * Link the given {@link Node} as next.
         *
         * @param next the given {@link Node}
         */
        void link(Node<E> next);
    }

    /**
     * A basic implementation of {@link Node} that holds an element and a reference to the next {@link Node}.
     *
     * @param <E> the type of element
     */
    protected static class NodeLink<E> implements Link<E> {

        protected final E item; protected Node<E> next;

        /**
         * Construct the object with the specified element and next {@link Node}.
         *
         * @param item the given value
         * @param next the given next {@link Node}
         */
        public NodeLink(E item, Node<E> next) {
            this.item = item; this.next = next; }

        @Override public E item() { return item; }
        @Override public Node<E> next() { return next; }
        @Override public void link(Node<E> next) { this.next = next; }
    }

    /**
     * Represents a bucket in the Container, extending {@link AbstractLinkSequence}.
     * Each bucket is responsible for holding a sequence of elements and provides the necessary
     * functionality to manage and iterate over those elements efficiently.
     *
     * @param <E> the type of elements contained in this bucket
     */
    protected static class Bucket<E> extends AbstractLinkSequence<E, Bucket.Node<E>> {

        private static final long serialVersionUID = 5178607093351787071L;

        protected Bucket(E first, E second, NodeNode<E> handle) {
            put(null, new Node<>(first), (unused, head) -> put(head, new Node<>(second), handle)); }

        protected Bucket(E first, E second, LinkNodeNode<E> handle) {
            this(first, new Node<>(second), second, handle); }

        protected Bucket(E first, LinkHashContainer.Node<E> next, E second, LinkNodeNode<E> handle) {
            put(null, new LinkNode<>(first, next), (unused, head) -> put(head, new Node<>(second), handle)); }


        protected void put(Node<E> tail, E element, NodeNode<E> handle) {
            put(tail, new Node<>(element), handle); }

        protected void put(Node<E> previous, Link<E> before, E element, LinkNodeNode<E> handle) {
            link(previous, before, new Node<>(element), putAndHandle(handle)); }

        protected void link(Node<E> previous, Link<E> before, Node<E> node, LinkNodeNode<E> handle) {
            link(previous, before, new LinkNode<>(previous.next.item, node), node, handle); }

        @Override public Sequence<E> sub(int fromIndex, int toIndex) {
            return new SubBucket(size, fromIndex, toIndex); }

        @Override protected Node<E> previous(Node<E> link) {
            return null; }

        @Override protected void serialize(ObjectOutputStream output) {}
        @Override protected void deserialize(ObjectInputStream input) {}

        private <P extends Node<E>, N extends Node<E>> void put(P previous, N next, BiConsumer<P, N> handle) {
            link(previous, next); size++; handle.accept(previous, next); }

        private void link(Node<E> previous, Link<E> before, LinkNode<E> next, Node<E> node, LinkNodeNode<E> handle) {
            unlink(previous); link(previous, next); before.link(next); handle.accept(next, node); }

        private LinkNodeNode<E> putAndHandle(LinkNodeNode<E> handle) {
            return (previous, next) -> put(previous, next, handle); }


        @FunctionalInterface
        protected interface NodeNode<E> extends BiConsumer<Node<E>, Node<E>> {}

        @FunctionalInterface
        protected interface LinkNodeNode<E> extends BiConsumer<LinkNode<E>, Node<E>> {}


        /**
         * Implementation of the {@link AbstractLinkSequence.Node}.
         *
         * @param <E> the type of elements
         */
        protected static class Node<E> extends AbstractLinkSequence.Node<E, Node<E>>
                implements LinkHashContainer.Node<E> {

            /**
             * Construct the object with the specified item.
             *
             * @param item the item to store in this node
             */
            protected Node(E item) { super(item); }

            @Override public E item() { return item; }
            @Override public LinkHashContainer.Node<E> next() { return null; }
        }

        /**
         * A specialized {@link Node} implementation that also acts as a {@link LinkHashContainer.Link}.
         *
         * @param <E> the type of element
         */
        protected static class LinkNode<E> extends Node<E> implements Link<E> {

            protected LinkHashContainer.Node<E> after;

            /**
             * Construct the object with the specified element and next {@link LinkHashContainer.Node}.
             *
             * @param item the given value
             * @param node the given next {@link LinkHashContainer.Node}
             */
            protected LinkNode(E item, LinkHashContainer.Node<E> node) {
                super(item); this.after = node; }

            @Override public LinkHashContainer.Node<E> next() { return after; }
            @Override public void link(LinkHashContainer.Node<E> next) { this.after = next; }
        }


        /**
         * A formal immutable implementation of a sub-bucket view.
         */
        protected class SubBucket extends AbstractLinkSubSequence {

            private static final long serialVersionUID = -7000073677354528483L;

            protected  SubBucket(int size, int fromIndex, int toIndex) {
                super(size, fromIndex, toIndex); }

            protected SubBucket(SubBucket parent, int fromIndex, int toIndex) {
                super(parent, fromIndex, toIndex); }

            @Override public Iterator<E> iterator() {
                return Bucket.super.iterator(previous(0), this, 0, null); }
            @Override public SequenceIterator<E> iterator(int index) {
                return Bucket.super.iterator(previous(positionIndex(index, size)), this, index, null); }
            @Override public Sequence<E> sub(int fromIndex, int toIndex) {
                return new SubBucket(this, fromIndex, toIndex); }
        }
    }


    /**
     * Calculate the initial {@link HashMap} capacity basing on the given size.
     *
     * @param size the given size
     * @return the initial {@link HashMap} capacity basing on the given size
     */
    protected static int capacity(int size) {
        return max((int) (size/.75f) + 1, 16);
    }

    /**
     * Return a {@link LinkHashContainer} of the given values.
     *
     * @param elements the given values
     * @param <E> the element type
     * @return a {@link LinkHashContainer} of the given values
     */
    @SafeVarargs
    public static <E> LinkHashContainer<E> container(E... elements) {
        return new LinkHashContainer<>(elements);
    }
}
