package org.moodminds.elemental;

import java.util.Comparator;
import java.util.Iterator;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Spliterator;
import java.util.function.Consumer;

import static java.util.Optional.ofNullable;

/**
 * A template implementation of the {@link Set} interface, which is powered by an internal {@link Map}.
 *
 * @param <E> the element type
 * @param <M> the type of the internal {@link Map}
 */
public abstract class AbstractMapSet<E, M extends Map<E, E>>
        extends AbstractMapUnitainer<E, M> implements Set<E>, Equatable {

    private static final long serialVersionUID = 5732197695390790710L;

    /**
     * Construct the object with the given {@link M map} .
     *
     * @param map the given {@link M map} 
     */
    protected AbstractMapSet(M map) {
        super(map);
    }

    /**
     * Construct the object with the given {@link M}
     * and sequential single-threaded {@link Producer} of elements.
     *
     * @param map the given {@link M}
     * @param elements the given sequential single-threaded {@link Producer} of elements
     */
    protected AbstractMapSet(M map, Producer<? extends E> elements) {
        super(map); elements.provide(this::add);
    }

    @Override public boolean equatable(Object obj) {
        return obj instanceof java.util.Set; }

    @Override public boolean add(E e) {
        return e == null ? !map.containsKey(null) &&
                map.put(null, null) == null  // 'map.put(null, null) == null' here is just for ternary, always true
                : map.putIfAbsent(e, e) == null; }

    @Override public boolean remove(Object o) {
        return map.remove(o) != null; }
    @Override public void clear() {
        map.clear(); }

    /**
     * {@inheritDoc}
     *
     * @param value {@inheritDoc}
     * @param present {@inheritDoc}
     * @param removal {@inheritDoc}
     */
    @Override
    protected Iterator<E> iterator(E value, boolean present, Runnable removal) {
        return new Iterator<E>() {

            OptionalIterator<E> iterator; Iterator<?> modCheckIterator = AbstractMapSet.this.iterator();

            {
                iterator = (OptionalIterator<E>) AbstractMapSet.super.iterator(value, present, () -> {
                    checkMod(); if (removal != null) removal.run();
                    map.remove(value); iterator.present = false; modCheckIterator = AbstractMapSet.this.iterator();
                });
            }

            @Override public boolean hasNext() { return iterator.hasNext(); }
            @Override public E next() { checkMod(); return iterator.next(); }
            @Override public void remove() { iterator.remove(); }

            void checkMod() { try { modCheckIterator.next(); } catch (NoSuchElementException ignored) {} }
        };
    }

    /**
     * {@inheritDoc}
     *
     * @param keysIterator {@inheritDoc}
     * @return {@inheritDoc}
     */
    @Override
    protected Iterator<E> containerIterator(Iterator<E> keysIterator) {
        return new Iterator<E>() {

            final Iterator<E> iterator = AbstractMapSet.super.containerIterator(keysIterator);

            @Override public boolean hasNext() { return iterator.hasNext(); }
            @Override public E next() { return iterator.next(); }
            @Override public void remove() { keysIterator.remove(); }
            @Override public void forEachRemaining(Consumer<? super E> action) {
                iterator.forEachRemaining(action); }
        };
    }

    /**
     * {@inheritDoc}
     *
     * @param keysSpliterator {@inheritDoc}
     * @return {@inheritDoc}
     */
    @Override
    protected Spliterator<E> containerSpliterator(Spliterator<E> keysSpliterator) {
        return new Spliterator<E>() {

            final Spliterator<E> spliterator = AbstractMapSet.super.containerSpliterator(keysSpliterator);

            @Override public boolean tryAdvance(Consumer<? super E> action) {
                return spliterator.tryAdvance(action); }
            @Override public long estimateSize() {
                return spliterator.estimateSize(); }
            @Override public long getExactSizeIfKnown() {
                return spliterator.getExactSizeIfKnown(); }
            @Override public void forEachRemaining(Consumer<? super E> action) {
                spliterator.forEachRemaining(action); }
            @Override public Comparator<? super E> getComparator() {
                return spliterator.getComparator(); }
            @Override public int characteristics() {
                return spliterator.characteristics() & ~IMMUTABLE; }
            @Override public boolean hasCharacteristics(int characteristics) {
                if ((characteristics & IMMUTABLE) != 0) return false;
                return spliterator.hasCharacteristics(characteristics); }

            @Override public Spliterator<E> trySplit() {
                return ofNullable(spliterator.trySplit()).map(split -> containerSpliterator(split))
                        .orElse(null); }
        };
    }
}
