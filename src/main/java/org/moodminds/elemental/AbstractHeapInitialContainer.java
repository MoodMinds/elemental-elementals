package org.moodminds.elemental;

import org.moodminds.elemental.AbstractHeapInitialContainer.Bucket;
import org.moodminds.elemental.AbstractLinkInitialSequence.Link;

import java.util.IdentityHashMap;
import java.util.Map;
import java.util.function.Function;

import static org.moodminds.sneaky.Cast.cast;

/**
 * A template immutable implementation of the {@link Container} interface,
 * which allows duplicates and is powered by an internal {@link Map}.
 *
 * @param <E> the element type
 * @param <M> the type of the internal {@link Map}
 */
public abstract class AbstractHeapInitialContainer<E, M extends Map<E, Object>>
        extends AbstractHeapContainer<E, Bucket<E>, M> {

    private static final long serialVersionUID = 1742087467134207373L;

    /**
     * Construct the object with the given {@link M map}.
     *
     * @param map the given {@link M map}
     */
    protected AbstractHeapInitialContainer(M map) {
        super(map);
    }

    /**
     * Construct the object with the given {@link M map}
     * and sequential single-threaded {@link Producer} of elements.
     *
     * @param map the given {@link M map}
     * @param elements the given sequential single-threaded {@link Producer} of elements
     */
    protected AbstractHeapInitialContainer(M map, Producer<? extends E> elements) {
        super(map); init(elements);
    }

    /**
     * Initialize the inner {@link M map} of this Container with the specified
     * sequential single-threaded {@link Producer} of elements.
     *
     * @param elements the specified sequential single-threaded {@link Producer} of elements
     */
    protected void init(Producer<? extends E> elements) {

        class Tail implements Function<Link<E>, Link<E>> {
            Link<E> tail; Tail(Bucket<E> unused) { /* this is only for method reference */ }
            @Override public Link<E> apply(Link<E> next) {
                Link<E> tail = this.tail; this.tail = next; return tail; }
        }

        Map<Bucket<E>, Tail> tails = new IdentityHashMap<>();

        elements.provide(element -> {
            Object value = map.get(element);
            if (!tryBucket(value, bucket -> bucket
                    .add(tails.computeIfAbsent(bucket, Tail::new), element)))
                if (isMapped(element, value)) {
                    Bucket<E> bucket = new Bucket<>(); map.put(cast(value), bucket
                            .add(tails.computeIfAbsent(bucket, Tail::new), cast(value), element));
                } else map.put(element, element);
            count(1);
        });
    }

    @Override protected boolean isBucket(Object value) {
        return value instanceof Bucket; }

    /**
     * Adjust the size of this container by the specified amount.
     *
     * @param number the amount by which to adjust the size
     */
    protected abstract void count(int number);


    /**
     * Represents a bucket in the container, extending {@link AbstractLinkInitialSequence}.
     * Each bucket is responsible for holding a sequence of elements and provides the necessary
     * functionality to manage and iterate over those elements efficiently.
     *
     * @param <E> the type of elements contained in this bucket
     */
    protected static class Bucket<E> extends AbstractLinkInitialSequence<E> {

        private static final long serialVersionUID = 5178607093351787071L;

        @SafeVarargs
        protected final Bucket<E> add(Function<Link<E>, Link<E>> tails, E... elements) {
            for (E element : elements) {
                Link<E> next; put(tails.apply(next = new Link<>(element)), next);
            } return this;
        }
    }
}
