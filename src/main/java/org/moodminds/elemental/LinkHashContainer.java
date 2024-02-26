package org.moodminds.elemental;

import org.moodminds.sneaky.Cast;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Spliterator;
import java.util.function.Consumer;
import java.util.stream.Stream;

import static java.lang.Math.max;
import static java.util.Spliterators.spliteratorUnknownSize;
import static org.moodminds.sneaky.Cast.cast;
import static org.moodminds.sneaky.Sneak.sneak;

/**
 * A {@link LinkedHashMap}-powered implementation
 * of the {@link Container} interface, preserving the order of elements
 * as defined during construction.
 *
 * @param <E> the element type
 */
public class LinkHashContainer<E> extends AbstractMultiContainer<E,
            LinkHashContainer.Node<E>, LinkHashContainer.Bucket<E>, Map<E, Object>>
        implements RandomMatch {

    private static final long serialVersionUID = -7910725201009294081L;

    /**
     * Container size holder field.
     */
    private transient int size;

    /**
     * Construct the object with the given elements array.
     *
     * @param elements the given elements array
     */
    @SafeVarargs
    public LinkHashContainer(E... elements) {
        this(new LinkedHashMap<>(capacity(elements.length)), Stream.of(elements));
    }

    /**
     * Construct the object with the given elements {@link Stream}.
     *
     * @param elements the given elements {@link Stream}
     */
    public LinkHashContainer(Stream<? extends E> elements) {
        this(new LinkedHashMap<>(), elements);
    }

    /**
     * Construct the object with the given elements {@link Container}.
     *
     * @param elements the given elements {@link Container}
     */
    public LinkHashContainer(Container<? extends E> elements) {
        this(new LinkedHashMap<>(capacity(elements.size())), elements.stream());
    }

    /**
     * Construct the object with the given elements {@link java.util.Collection}.
     *
     * @param elements the given elements {@link java.util.Collection}
     */
    public LinkHashContainer(java.util.Collection<? extends E> elements) {
        this(new LinkedHashMap<>(capacity(elements.size())), elements.stream());
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
     * Construct the object with the given {@link LinkedHashMap}, elements {@link Stream} and size.
     *
     * @param map the given  {@link LinkedHashMap}
     * @param elements the given elements {@link Stream}
     */
    protected LinkHashContainer(LinkedHashMap<E, Object> map, Stream<? extends E> elements) {
        super(map); populate(elements.sequential()::forEach);
    }

    @Override protected Iterator<E> iterator(Object value, boolean hasNext) {
        return value instanceof Link ? iterator(Cast.<Link<E>>cast(value))
                : super.iterator(value, hasNext); }
    @Override protected Iterator<E> iterator(Iterator<Map.Entry<E, Object>> iterator) {
        return new ContainerIterator(iterator); }
    @Override protected Spliterator<E> spliterator(Spliterator<Map.Entry<E, Object>> spliterator, Integer size) {
        return new ContainerSpliterator(spliterator, size) {
            @Override protected Spliterator<E> spliterator(Spliterator<Map.Entry<E, Object>> spliterator) {
                return LinkHashContainer.this.spliterator(spliterator, null); }
        }; }

    @Override protected boolean isBucket(Object value) {
        return value instanceof Bucket; }
    @Override protected boolean isMulti() {
        return size > map.size(); }
    @Override protected Optional<Integer> getSize() {
        return Optional.of(size); }
    @Override public int size() {
        return size; }

    @Override
    protected void serialiaze(ObjectOutputStream output) throws Exception {
        output.writeInt(size);
        for (E e : this)
            output.writeObject(e);
    }

    @Override
    protected void deserialize(ObjectInputStream input) throws Exception {
        int size = input.readInt();
        map = new LinkedHashMap<>(capacity(size));
        populate(consumer -> {
            try {
                while (this.size < size)
                    consumer.accept(cast(input.readObject()));
            } catch (Exception e) { sneak(e); }
        });
    }

    protected Iterator<E> iterator(Link<E> link) {
        return SingleIterator.iterator(link.item);
    }

    private void populate(Producer<? extends E> elements) {
        elements.provide(new Consumer<E>() {

            final Map<Bucket<E>, Node<E>> tails = new HashMap<>();

            Linking linking;

            /**
             * Linking algorithm basing on the last added element details.
             */
            abstract class Linking {

                abstract void add(E value, E element);
                abstract void add(Link<E> value, E element);
                abstract void add(Bucket<E> value, E element);

                Node<E> getTail(Bucket<E> bucket) {
                    return bucket.size == 2 ? bucket.head.next : tails.get(bucket); }

                void setTail(Bucket<E> bucket, Node<E> node) {
                    tails.put(bucket, node); }

                Bucket<E> addBucket(Node<E> head, Node<E> next) {
                    Bucket<E> bucket = new Bucket<>(head); head.next = next;
                    bucket.size++; map.put(head.item, bucket); return bucket;
                }

                void setNext(Bucket<E> bucket, Node<E> node) {
                    Node<E> tail = bucket.size == 2 ? bucket.head.next : tails.get(bucket);
                    tail.next = node; bucket.size++; tails.put(bucket, node);
                }
            }

            /**
             * Linking algorithm for a single element that was added lastly.
             */
            class ItemLinking extends Linking {

                final E last;

                ItemLinking(E last) {
                    this.last = last;
                }

                @Override
                void add(E value, E element) {
                    if (value == last) {
                        Node<E> next = new Node<>(element);
                        LinkNode<E> head = new LinkNode<>(last, next);
                        linking = new NodeLinking(next, addBucket(head, next), head, head);
                    } else {
                        Node<E> head = new Node<>(value), next = new Node<>(element);
                        Link<E> link = new Link<>(last, next); map.put(last, link);
                        linking = new LinkLinking(next, addBucket(head, next), head, link);
                    }
                }

                @Override
                void add(Link<E> value, E element) {
                    Node<E> next = new Node<>(element);
                    LinkNode<E> head = new LinkNode<>(value.item, value.next);
                    Link<E> link = new Link<>(last, next); map.put(last, link);
                    linking = new LinkLinking(next, addBucket(head, next), head, link);
                }

                @Override
                void add(Bucket<E> value, E element) {
                    Node<E> prev = getTail(value), next = new Node<>(element);
                    setNext(value, next);
                    Link<E> link = new Link<>(last, next); map.put(last, link);
                    linking = new LinkLinking(next, value, prev, link);
                }
            }

            /**
             * Linking algorithm for a duplicate element that was added lastly to a bucket.
             */
            abstract class BucketLinking extends Linking {

                final Node<E> last; final Bucket<E> bucket;

                BucketLinking(Node<E> last, Bucket<E> bucket) {
                    this.last = last; this.bucket = bucket;
                }

                @Override
                void add(E value, E element) {
                    Node<E> next = new Node<>(element);
                    LinkNode<E> link = new LinkNode<>(last.item, next);
                    relink(link); setTail(bucket, link);
                    Node<E> head = new Node<>(value);
                    linking = new NodeLinking(next, addBucket(head, next), head, link);
                }

                @Override
                void add(Link<E> value, E element) {
                    Node<E> next = new Node<>(element);
                    LinkNode<E> link = new LinkNode<>(last.item, next);
                    relink(link); setTail(bucket, link);
                    Node<E> head = new LinkNode<>(value.item, value.next);
                    linking = new NodeLinking(next, addBucket(head, next), head, link);
                }

                @Override
                void add(Bucket<E> value, E element) {
                    Node<E> prev = getTail(value), next = new Node<>(element); LinkNode<E> link;
                    if (value == bucket)
                        prev = link = new LinkNode<>(prev.item, next);
                    else
                        link = new LinkNode<>(last.item, next);
                    relink(link); setTail(bucket, link); setNext(value, next);
                    linking = new NodeLinking(next, value, prev, link);
                }

                abstract void relink(LinkNode<E> node);
            }

            /**
             * Linking algorithm for a duplicate element and linked to from a {@link Link}.
             */
            class LinkLinking extends BucketLinking {

                final Node<E> prev; final Link<E> link;

                LinkLinking(Node<E> last, Bucket<E> bucket, Node<E> prev, Link<E> link) {
                    super(last, bucket); this.prev = prev; this.link = link; }

                @Override void relink(LinkNode<E> node) {
                    prev.next = node; link.next = node; }
            }

            /**
             * Linking algorithm for a duplicate element and linked to from a {@link LinkNode}.
             */
            class NodeLinking extends BucketLinking {

                final Node<E> prev; final LinkNode<E> link;

                NodeLinking(Node<E> last, Bucket<E> bucket, Node<E> prev, LinkNode<E> link) {
                    super(last, bucket); this.prev = prev; this.link = link; }

                @Override void relink(LinkNode<E> node) {
                    prev.next = node; link.nextLink = node; }
            }

            @Override
            public void accept(E element) {
                Object value = map.get(element);
                if (isBucket(value))
                    linking.add(asBucket(value), element);
                else if (value instanceof Link)
                    linking.add(Cast.<Link<E>>cast(value), element);
                else if (value != null || (element == null && map.containsKey(null)))
                    linking.add(Cast.<E>cast(value), element);
                else {
                    map.put(element, element); linking = new ItemLinking(element);
                } size++;
            }
        });
    }

    /**
     * Implementation of the {@link AbstractMultiContainer.Node}.
     *
     * @param <E> the type of elements
     */
    protected static class Node<E> extends AbstractMultiContainer.Node<E, Node<E>> {

        private static final long serialVersionUID = 7988703318076870209L;

        protected Node(E item) {
            super(item);
        }
    }

    /**
     * Linking {@link Node} extension.
     *
     * @param <E> the type of elements
     */
    protected static class LinkNode<E> extends Node<E> {

        private static final long serialVersionUID = -4418143230042931515L;

        protected Node<E> nextLink;

        protected LinkNode(E item, Node<E> nextLink) {
            super(item); this.nextLink = nextLink;
        }
    }

    /**
     * Linking {@link Node} element holder.
     *
     * @param <E> the type of elements
     */
    protected static class Link<E> {

        protected E item; protected Node<E> next;

        protected Link(E item, Node<E> next) {
            this.item = item; this.next = next;
        }
    }

    /**
     * Implementation of the {@link AbstractMultiContainer.Bucket}.
     *
     * @param <E> the type of elements
     */
    protected static class Bucket<E> extends AbstractMultiContainer.Bucket<E, Node<E>, Bucket<E>> {

        private static final long serialVersionUID = -8680478261146107072L;

        protected Bucket(Node<E> head) {
            super(head);
        }
    }

    /**
     * The linking item values {@link Iterator}.
     */
    protected class LinkIterator extends AbstractLinkIterator<E, Object> {

        protected LinkIterator(Link<E> next) {
            super(next); }
        protected LinkIterator(Node<E> next) {
            super(next); }

        @Override protected Object next(Object link) {
            return link instanceof Link ? ((Link<?>) link).next
                    : link instanceof LinkNode ? ((LinkNode<?>) link).nextLink : null; }

        @Override protected E value(Object link) {
            return link instanceof Link ? Cast.<Link<E>>cast(link).item
                    : Cast.<Node<E>>cast(link).item; }
    }

    /**
     * {@link Iterator} over this Container.
     */
    protected class ContainerIterator extends AbstractMultiContainer<E, Node<E>, Bucket<E>, Map<E, Object>>.ContainerIterator {

        protected ContainerIterator(Iterator<Map.Entry<E, Object>> iterator) {
            super(iterator);
        }

        @Override
        protected boolean tryFlatten(Object value) {
            if (isBucket(value)) {
                flattenIterator = new LinkIterator(asBucket(value).head); return true;
            } else if (value instanceof Link) {
                flattenIterator = new LinkIterator(Cast.<Link<E>>cast(value)); return true;
            } return false;
        }
    }

    /**
     * {@link Spliterator} over this Container.
     */
    protected abstract class ContainerSpliterator extends AbstractMultiContainer<E, Node<E>, Bucket<E>, Map<E, Object>>.ContainerSpliterator {

        protected ContainerSpliterator(Spliterator<Map.Entry<E, Object>> spliterator, Integer size) {
            super(spliterator, size);
        }

        @Override
        protected boolean tryFlatten(Object value) {
            if (isBucket(value)) {
                flattenSpliterator = spliteratorUnknownSize(new LinkIterator(asBucket(value).head), 0); return true;
            } else if (value instanceof Link) {
                flattenSpliterator = spliteratorUnknownSize(new LinkIterator(Cast.<Link<E>>cast(value)), 0); return true;
            } return false;
        }
    }



    /**
     * Calculate the initial {@link LinkedHashMap} capacity basing on the given size.
     *
     * @param size the given size
     * @return the initial {@link LinkedHashMap} capacity basing on the given size
     */
    private static int capacity(int size) {
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
