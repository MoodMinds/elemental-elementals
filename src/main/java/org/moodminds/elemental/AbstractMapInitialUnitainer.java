package org.moodminds.elemental;

import java.util.Comparator;
import java.util.Map;
import java.util.Spliterator;
import java.util.function.Consumer;

import static java.util.Optional.ofNullable;

/**
 * A template unique-values immutable implementation of the {@link Container}
 * interface, which is powered by an internal {@link Map}.
 *
 * @param <E> the element type
 * @param <M> the type of the internal {@link Map}
 */
public abstract class AbstractMapInitialUnitainer<E, M extends Map<E, E>>
        extends AbstractMapUnitainer<E, M> {

    private static final long serialVersionUID = 3144778333080270832L;

    /**
     * Construct the object with the given {@link M map}.
     *
     * @param map the given {@link M map}
     */
    public AbstractMapInitialUnitainer(M map) {
        super(map);
    }

    /**
     * Construct the object with the given {@link M}
     * and sequential single-threaded {@link Producer} of elements.
     *
     * @param map      the given {@link M}
     * @param elements the given sequential single-threaded {@link Producer} of elements
     */
    public AbstractMapInitialUnitainer(M map, Producer<? extends E> elements) {
        super(map); init(elements);
    }

    /**
     * Initialize the inner {@link M map} of this Container with the specified
     * sequential single-threaded {@link Producer} of elements.
     *
     * @param elements the specified sequential single-threaded {@link Producer} of elements
     */
    protected void init(Producer<? extends E> elements) {
        elements.provide(element -> map.putIfAbsent(element, element)); }

    /**
     * {@inheritDoc}
     *
     * @param keysSpliterator {@inheritDoc}
     * @return {@inheritDoc}
     */
    @Override
    protected Spliterator<E> containerSpliterator(Spliterator<E> keysSpliterator) {
        return new Spliterator<E>() {
            @Override public boolean tryAdvance(Consumer<? super E> action) {
                return keysSpliterator.tryAdvance(action); }
            @Override public long estimateSize() {
                return keysSpliterator.estimateSize(); }
            @Override public long getExactSizeIfKnown() {
                return keysSpliterator.getExactSizeIfKnown(); }
            @Override public void forEachRemaining(Consumer<? super E> action) {
                keysSpliterator.forEachRemaining(action); }
            @Override public Comparator<? super E> getComparator() {
                return keysSpliterator.getComparator(); }
            @Override public int characteristics() {
                return keysSpliterator.characteristics() | Spliterator.IMMUTABLE; }
            @Override public boolean hasCharacteristics(int characteristics) {
                return (characteristics & Spliterator.IMMUTABLE) != 0
                        || keysSpliterator.hasCharacteristics(characteristics); }

            @Override public Spliterator<E> trySplit() {
                return ofNullable(keysSpliterator.trySplit()).map(split -> containerSpliterator(split))
                        .orElse(null); }
        };
    }
}
