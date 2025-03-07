package org.moodminds.elemental;

import java.util.stream.Stream;

import static org.moodminds.elemental.Producer.producer;

/**
 * A forward-linked implementation of the {@link Sequence} interface.
 *
 * @param <E> the type of elements
 */
public class LinkSequence<E> extends AbstractLinkInitialSequence<E> {

    private static final long serialVersionUID = -1418808492462226766L;

    /**
     * Size holding field.
     */
    protected transient int size;

    /**
     * Construct the object with the specified elements array.
     *
     * @param elements the specified elements array
     */
    @SafeVarargs
    public LinkSequence(E... elements) {
        this(producer(elements));
    }

    /**
     * Construct the object with the specified elements {@link Container}.
     *
     * @param elements the specified elements {@link Container}
     */
    public LinkSequence(Container<? extends E> elements) {
        this(elements.stream());
    }

    /**
     * Construct the object with the specified elements {@link java.util.Collection}.
     *
     * @param elements the specified elements {@link java.util.Collection}
     */
    public LinkSequence(java.util.Collection<? extends E> elements) {
        this(elements.stream());
    }

    /**
     * Construct the object with the specified elements {@link Collection}.
     *
     * @param elements the specified elements {@link Collection}
     */
    public LinkSequence(Collection<? extends E> elements) {
        this((java.util.Collection<? extends E>) elements);
    }

    /**
     * Construct the object with the specified elements {@link Stream}.
     *
     * @param elements the specified elements {@link Stream}
     */
    public LinkSequence(Stream<? extends E> elements) {
        this(elements.sequential()::forEach);
    }

    /**
     * Construct the object with the given sequential
     * single-threaded {@link Producer} of elements.
     *
     * @param elements the given sequential single-threaded {@link Producer} of elements
     * @throws NullPointerException if {@code elements} is {@code null}
     */
    public LinkSequence(Producer<? extends E> elements) {
        super(elements);
    }


    /**
     * Return a {@link LinkSequence} of the given elements values.
     *
     * @param elements the given element values
     * @param <E> the type of elements
     * @return a {@link LinkSequence} of the given element values
     */
    @SafeVarargs
    public static <E> LinkSequence<E> sequence(E... elements) {
        return new LinkSequence<>(elements);
    }

    /**
     * Return a {@link LinkSequence} by the given element {@link Producer}.
     *
     * @param elements the given element {@link Producer}
     * @param <E> the type of elements
     * @return a {@link LinkSequence} by the given {@link Producer}
     * @throws NullPointerException if {@code elements} is {@code null}
     */
    public static <E> LinkSequence<E> sequence(Producer<? extends E> elements) {
        return new LinkSequence<>(elements);
    }
}
