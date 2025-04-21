package org.moodminds.elemental;

import org.moodminds.sneaky.Cast;

import java.io.InvalidObjectException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ConcurrentModificationException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.stream.Stream;

import static java.lang.Math.max;
import static java.util.Objects.requireNonNull;
import static java.util.Spliterator.*;
import static org.moodminds.elemental.Producer.producer;
import static org.moodminds.sneaky.Cast.cast;
import static org.moodminds.sneaky.Sneak.sneak;

/**
 * A {@link HashMap}-powered implementation of the {@link Collection} interface,
 * preserving the order of elements as defined during insertion.
 *
 * @param <E> the element type
 */
public class LinkHashCollection<E> extends AbstractMapContainer<E, LinkHashCollection.Bucket<E>, Map<E, Object>>
        implements Collection<E> {

    private static final long serialVersionUID = -2848920053576607533L;

    /**
     * The head and tail holding fields.
     */
    protected transient Node<E> head, tail;

    /**
     * Collection size holder field.
     */
    protected transient int size;

    /**
     * Modification count holder field.
     */
    protected transient int modCount;

    /**
     * Construct the object with the given elements array.
     *
     * @param elements the given elements array
     */
    @SafeVarargs
    public LinkHashCollection(E... elements) {
        this(new HashMap<>(capacity(elements.length)), producer(elements));
    }

    /**
     * Construct the object with the given elements {@link Stream}.
     *
     * @param elements the given elements {@link Stream}
     */
    public LinkHashCollection(Stream<? extends E> elements) {
        this(new HashMap<>(), elements.sequential()::forEach);
    }

    /**
     * Construct the object with the given elements {@link Container}.
     *
     * @param elements the given elements {@link Container}
     */
    public LinkHashCollection(Container<? extends E> elements) {
        this(new HashMap<>(capacity(elements.size())), elements::forEach);
    }

    /**
     * Construct the object with the given elements {@link java.util.Collection}.
     *
     * @param elements the given elements {@link java.util.Collection}
     */
    public LinkHashCollection(java.util.Collection<? extends E> elements) {
        this(new HashMap<>(capacity(elements.size())), elements::forEach);
    }

    /**
     * Construct the object with the given elements {@link Collection}.
     *
     * @param elements the given elements {@link Collection}
     */
    public LinkHashCollection(Collection<? extends E> elements) {
        this((java.util.Collection<? extends E>) elements);
    }

    /**
     * Construct the object with the given {@link HashMap map}
     * and sequential single-threaded {@link Producer} of elements.
     *
     * @param map the given {@link HashMap map}
     * @param elements the given sequential single-threaded {@link Producer} of elements
     */
    protected LinkHashCollection(HashMap<E, Object> map, Producer<? extends E> elements) {
        super(map); init(elements);
    }

    protected void init(Producer<? extends E> elements) {
        elements.provide(this::put);
    }

    @Override public Iterator<E> getAll(Object o) {
        return collectionIterator(super.getAll(o)); }

    @Override public boolean add(E e) {
        put(e); modCount++; return true; }
    @Override public void clear() {
        map.clear(); size = 0; head = tail = null; modCount++; }
    @Override public int size() {
        return size; }

    @Override public Iterator<E> iterator()  {
        return collectionIterator(new AbstractLinkIterator<E, Node<E>>(null) {
            @Override protected Node<E> previous(Node<E> link) {
                return link.previous(); }
            @Override protected boolean hasNext(Node<E> link) {
                return link != null ? link.next() != null : head != null; }
            @Override protected Node<E> next(Node<E> link) {
                return link != null ? link.next() : head; }
            @Override protected E item(Node<E> link) {
                return link.item(); }
            @Override protected void removeElement() {
                if (next instanceof Bucket.Node) {
                    Bucket<E>.Node next = (Bucket<E>.Node) this.next;
                    Bucket<E> bucket = next.bucket(); next.delete();
                    if (bucket.size() == 1) {
                        Bucket<E>.Node head = head(bucket);
                        Node<E> link = new NodeLink<>(head.item());
                        relink(head, link);
                        if (previous == head) previous = link;
                        map.put(head.item(), link);
                    } else if (!bucket.contains()) map.remove(next.item());
                } else map.remove(next.item()); unlink(previous); next = null; size--; }
        }); }

    @Override public Spliterator<E> spliterator() {
        return Spliterators.spliterator(iterator(), size, ORDERED
                | (size > map.size() ? 0 : DISTINCT)); }

    @Override
    protected Iterator<E> iterator(Object value, boolean present) {
        return iterator(cast(value), present);
    }

    protected Iterator<E> iterator(Node<E> node, boolean present) {
        return new OptionalIterator<E>(present ? node.item() : null, present) {
            @Override protected void removeElement() {
                map.remove(node.item()); unlink(node.previous()); size--; present = false; }
        };
    }

    @Override
    protected Iterator<E> iterator(Bucket<E> bucket, Iterator<E> bucketIterator) {
        return new Iterator<E>() {

            final AbstractLinkSequenceIterator<E, Bucket<E>.Node> iterator =
                    (AbstractLinkSequenceIterator<E, Bucket<E>.Node>) LinkHashCollection.super.iterator(bucket, bucketIterator);

            @Override public boolean hasNext() { return iterator.hasNext(); }
            @Override public E next() { return iterator.next(); }
            @Override public void remove() {
                Node<E> next = iterator.next, previous = next.previous(); iterator.remove();
                if (bucket.size() == 1) {
                    Bucket<E>.Node head = head(bucket);
                    Node<E> link = new NodeLink<>(head.item());
                    relink(head, link);
                    if (previous == head) previous = link;
                    map.put(head.item(), link);
                } else if (!bucket.contains()) map.remove(next.item());
                unlink(previous); size--; }
            @Override public void forEachRemaining(Consumer<? super E> action) {
                iterator.forEachRemaining(action); }
        };
    }

    @Override
    protected boolean isBucket(Object value) {
        return value instanceof Bucket;
    }

    /**
     * Return an {@link Iterator} that wraps the given iterator and provides additional checks for modification
     * during iteration. Support element removal, ensure consistency with the Collection's modification
     * state, and track the modification count.
     *
     * @param iterator the underlying iterator to wrap and iterate over
     * @return an {@link Iterator} that performs modification checks and supports element removal
     */
    protected Iterator<E> collectionIterator(Iterator<E> iterator) {
        return new Iterator<E>() {

            int expectedMod = modCount;

            @Override public boolean hasNext() { return iterator.hasNext(); }
            @Override public E next() { checkMod(); return iterator.next(); }
            @Override public void remove() { checkMod(); iterator.remove(); modCount++; expectedMod++; }

            @Override public void forEachRemaining(Consumer<? super E> action) {
                requireNonNull(action); checkMod(); iterator.forEachRemaining(action
                        .andThen(unused -> checkMod())); }

            void checkMod() { LinkHashCollection.this.checkMod(expectedMod); }
        };
    }

    protected void put(E element) {
        Object value = map.get(element);
        if (!tryBucket(value, bucket -> link(bucket.put(element))))
            if (value != null) {
                Bucket<E> bucket = new Bucket<E>(Cast.<Node<E>>cast(value).item(), element, (head, tail) -> {
                    relink(cast(value), head); link(tail);
                }); map.put(element, bucket);
            } else {
                Node<E> node; map.put(element, node = new NodeLink<>(element)); link(node);
            }
        size++;
    }

    protected void link(Node<E> next) {
        link(tail, next);
    }

    protected void link(Node<E> previous, Node<E> next) {
        if (previous != null) {
            next.next(previous.next()); previous.next(next);
            if (next.next() == null) tail = next;
            else next.next().previous(next);
        } else head = tail = next; next.previous(previous);
    }

    protected void unlink(Node<E> previous) {
        if (previous == null)
            if ((head = head.next()) == null) tail = null;
            else head.previous(null);
        else {
            previous.next(previous.next().next());
            if (previous.next() == null) tail = previous;
            else previous.next().previous(previous);
        }
    }

    protected void relink(Node<E> link, Node<E> node) {
        Node<E> previous = link.previous(), next = link.next();
        if (next != null) {
            next.previous(node); node.next(next); }
        if (previous != null) {
            previous.next(node); node.previous(previous); }
        if (head == link) head = node;
        if (tail == link) tail = node;
    }

    /**
     * Check if the Collection has been modified since the specified modification count.
     * If the Collection's modification count has changed, throw a {@link ConcurrentModificationException}.
     *
     * @param expectedMod the expected modification count to compare against the current modification count
     * @throws ConcurrentModificationException if the Collection has been modified since the specified modification count
     */
    protected void checkMod(int expectedMod) {
        if (modCount != expectedMod) throw new ConcurrentModificationException(); }

    @Override
    protected void serialize(ObjectOutputStream output) throws Exception {
        output.writeInt(size); for (E e : this)
            output.writeObject(e);
    }

    @Override
    protected void deserialize(ObjectInputStream input) throws Exception {
        int size; if ((size = input.readInt()) < 0)
            throw new InvalidObjectException("Negative size: " + size);
        map = new HashMap<>(capacity(size)); init(consumer -> {
            try {
                while (this.size < size)
                    consumer.accept(cast(input.readObject()));
            } catch (Exception e) { sneak(e); }
        });
    }

    private Bucket<E>.Node head(Bucket<E> bucket) {
        AbstractLinkSequenceIterator<E, Bucket<E>.Node> iterator
                = (AbstractLinkSequenceIterator<E, Bucket<E>.Node>) bucket.iterator();
        iterator.next(); return iterator.next;
    }

    /**
     * Represents a node in this Collection referencing to the next and previous {@link Node}.
     *
     * @param <E> the type of element
     */
    protected interface Node<E> {

        /**
         * Returns the element stored in this node.
         *
         * @return the stored element
         */
        E item();

        /**
         * Return the previous {@link Node} in the sequence.
         *
         * @return the previous {@link Node}, or {@code null} if none exists
         */
        Node<E> previous();

        /**
         * Link the given {@link Node} as previous.
         *
         * @param node the given {@link Node}
         */
        void previous(Node<E> node);

        /**
         * Return the next {@link Node} in the sequence.
         *
         * @return the next {@link Node}, or {@code null} if none exists
         */
        Node<E> next();

        /**
         * Link the given {@link Node} as next.
         *
         * @param node the given {@link Node}
         */
        void next(Node<E> node);
    }

    /**
     * A basic implementation of {@link Node} that holds an element and a reference to the next {@link Node}.
     *
     * @param <E> the type of element
     */
    protected static class NodeLink<E> implements Node<E> {

        protected E item; protected Node<E> previous, next;

        public NodeLink(E item) { this.item = item; }

        @Override public E item() { return item; }
        @Override public Node<E> previous() { return previous; }
        @Override public void previous(Node<E> node) { previous = node; }
        @Override public Node<E> next() { return next; }
        @Override public void next(Node<E> node) { next = node; }
    }

    /**
     * Represents a bucket in the Collection, extending {@link AbstractLinkSequence}.
     * Each bucket is responsible for holding a sequence of elements and provides the necessary
     * functionality to manage and iterate over those elements efficiently.
     *
     * @param <E> the type of elements contained in this bucket
     */
    protected static class Bucket<E> extends AbstractTailedLinkSequence<E, Bucket<E>.Node> {

        private static final long serialVersionUID = -7693277982552371297L;

        public Bucket(E first, E second, BiConsumer<Node, Node> handle) {
            handle.accept(put(first), put(second));
        }

        @Override
        public TailedSequence<E> sub(int fromIndex, int toIndex) {
            return new Bucket<E>.SubBucket(size, fromIndex, toIndex);
        }

        protected Node put(E element) {
            Node next; link(tail, next = new Node(element)); size++; return next; }

        protected void delete(Node previous) {
            unlink(previous); size--; }

        @Override
        protected TailedSequenceIterator<E> iterator(Node previous, Sequence<E> sequence, int index, Runnable removal) {
            return new Object() {

                AbstractLinkSequenceIterator<E, Node> iterator;

                {
                    iterator = (AbstractLinkSequenceIterator<E, Node>) Bucket.super.iterator(previous, sequence, index, () -> {
                        if (removal != null) removal.run(); unlink(iterator.previous); size--; iterator.next = null; iterator.index--;
                    });
                }

            }.iterator;
        }

        @Override protected void serialize(ObjectOutputStream output) {}
        @Override protected void deserialize(ObjectInputStream input) {}


        /**
         * Implementation of the {@link Node}.
         */
        protected class Node extends AbstractTailedLinkSequence.Node<E, Node> implements LinkHashCollection.Node<E> {

            protected LinkHashCollection.Node<E> before, after;

            protected Node(E item) { super(item); }

            @Override public E item() { return item; }
            @Override public LinkHashCollection.Node<E> previous() { return before; }
            @Override public void previous(LinkHashCollection.Node<E> node) { before = node; }
            @Override public LinkHashCollection.Node<E> next() { return after; }
            @Override public void next(LinkHashCollection.Node<E> node) { after = node; }

            protected Bucket<E> bucket() { return Bucket.this; }
            protected void delete() { bucket().delete(previous); }
        }

        /**
         * A formal immutable implementation of a sub-bucket view.
         */
        protected class SubBucket extends AbstractTailedLinkSubSequence {

            private static final long serialVersionUID = -7000073677354528483L;

            private transient final int bucketSize; private transient final Bucket<E>.Node bucketTail;

            protected SubBucket(int size, int fromIndex, int toIndex) {
                super(size, fromIndex, toIndex); this.bucketSize = Bucket.this.size; this.bucketTail = Bucket.this.tail; }

            protected SubBucket(int bucketSize, Bucket<E>.Node bucketTail, Bucket<E>.SubBucket parent, int fromIndex, int toIndex) {
                super(parent, fromIndex, toIndex); this.bucketSize = bucketSize; this.bucketTail = bucketTail; }

            @Override public int getCount(Object o) {
                checkMod(); return super.getCount(o); }
            @Override public Iterator<E> getAll(Object o) {
                checkMod(); return super.getAll(o); }
            @Override public <R extends E> R get(int index) {
                checkMod(); return super.get(index); }
            @Override public int size() {
                checkMod(); return super.size(); }
            @Override public Iterator<E> iterator() {
                checkMod(); return iterator(0, previous(0)); }
            @Override public TailedSequenceIterator<E> iterator(int index) {
                checkMod(); return iterator(positionIndex(index, size), previous(index)); }
            @Override public Spliterator<E> spliterator() {
                checkMod(); return super.spliterator(); }
            @Override public TailedSequence<E> sub(int fromIndex, int toIndex) {
                checkMod(); return new Bucket<E>.SubBucket(bucketSize, bucketTail, this, fromIndex, toIndex); }

            protected TailedSequenceIterator<E> iterator(int index, Bucket<E>.Node previous) {
                return new TailedSequenceIterator<E>() {

                    final TailedSequenceIterator<E> iterator = Bucket.super.iterator(previous, Bucket.SubBucket.this, index, null);

                    @Override public boolean hasPrevious() { return iterator.hasPrevious(); }
                    @Override public E previous() { checkMod(); return iterator.previous(); }
                    @Override public int previousIndex() { checkMod(); return iterator.previousIndex(); }
                    @Override public boolean hasNext() { return iterator.hasNext(); }
                    @Override public E next() { checkMod(); return iterator.next(); }
                    @Override public int nextIndex() { checkMod(); return iterator.nextIndex(); }
                    @Override public void remove() { checkMod(); iterator.remove(); }
                    @Override public void forEachRemaining(Consumer<? super E> action) {
                        checkMod(); iterator.forEachRemaining(action.andThen(unused -> checkMod())); }
                    @Override public void forEachPreceding(Consumer<? super E> action) {
                        checkMod(); iterator.forEachPreceding(action.andThen(unused -> checkMod())); }
                };
            }

            void checkMod() {
                if (bucketSize != Bucket.this.size || bucketTail != Bucket.this.tail)
                    throw new ConcurrentModificationException();
            }
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
     * Return a {@link LinkHashCollection} of the given values.
     *
     * @param elements the given values
     * @param <E> the element type
     * @return a {@link LinkHashCollection} of the given values
     */
    @SafeVarargs
    public static <E> LinkHashCollection<E> collection(E... elements) {
        return new LinkHashCollection<>(elements);
    }
}
