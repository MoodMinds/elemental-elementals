package org.moodminds.elemental;

import org.moodminds.elemental.AbstractHeapInitialContainer.Bucket;
import org.moodminds.elemental.AbstractLinkInitialSequence.Node;

import java.util.IdentityHashMap;
import java.util.Map;
import java.util.function.BiFunction;

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
        BiFunction<Bucket<E>, Node<E>, Node<E>> tails = new IdentityHashMap<Bucket<E>, Node<E>>()::put;
        elements.provide(element -> {
            Object value = map.get(element);
            if (!tryBucket(value, bucket -> bucket.add(tails, element)))
                if (!isMapped(element, value)) map.put(element, element);
                else map.put(cast(value), new Bucket<E>()
                        .add(tails, cast(value)).add(tails, element));
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

        protected Bucket<E> add(BiFunction<Bucket<E>, Node<E>, Node<E>> tails, E element) {
            Node<E> link; linkNext(tails.apply(this, link = new Node<>(element)), link); size++; return this;
        }
    }
}
