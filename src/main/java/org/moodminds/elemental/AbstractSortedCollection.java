package org.moodminds.elemental;

import org.moodminds.sneaky.Cast;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.lang.ref.Reference;
import java.lang.ref.WeakReference;
import java.util.Comparator;
import java.util.Iterator;
import java.util.SortedMap;
import java.util.function.BiPredicate;

import static org.moodminds.sneaky.Cast.cast;

/**
 * Template implementation of the {@link SortedCollection} interface,
 * which allows duplicates and is powered by an internal {@link SortedMap}.
 *
 * @param <E> the element type
 * @param <N> the type of the internal {@link Node}
 * @param <B> the type of the internal {@link Bucket} that holds duplicates
 * @param <M> the type of the internal {@link SortedMap}
 */
public abstract class AbstractSortedCollection<E,
            N extends AbstractMultiCollection.Node<E, N>,
            B extends AbstractMultiCollection.Bucket<E, N, B>,
            M extends SortedMap<E, Object>>
        extends AbstractMultiCollection<E, N, B, M> implements SortedCollection<E> {

    private static final long serialVersionUID = -3448341295695770529L;

    /**
     * Parent Container holder field.
     */
    protected final AbstractSortedCollection<E, N, B, ?> parent;

    /**
     * Collection's {@link Comparator} holder field.
     */
    protected final Comparator<E> comparator = comparator(map.comparator());

    /**
     * Sub Collection's range check predicate holder field.
     */
    protected final BiPredicate<Comparator<E>, E> range;

    /**
     * Child sub-Collection {@link Reference} holder field.
     */
    protected transient SubReference<E, N, B> child;

    /**
     * Construct the object with the given {@link M} map and size.
     *
     * @param map the given {@link M} map
     * @param size the given size
     */
    protected AbstractSortedCollection(M map, Integer size) {
        this(map, size, null);
    }

    /**
     * Construct the object by the given {@link M} map, size and parent Collection.
     *
     * @param map the given {@link M} map
     * @param size the given size
     * @param parent the given parent Collection
     */
    protected AbstractSortedCollection(M map, Integer size, AbstractSortedCollection<E, N, B, ?> parent) {
        this(map, size, parent, (comp, e) -> true);
    }

    /**
     * Construct the object by the given {@link M} map, parent Collection
     * and {@link BiPredicate} elements range check bound for subs.
     *
     * @param map the given {@link M} map
     * @param parent the given parent Collection
     * @param range the given {@link BiPredicate} elements range check bound
     * @param <P> the type of the {@link Serializable} {@link BiPredicate} intersection
     */
    protected <P extends BiPredicate<Comparator<E>, E> & Serializable> AbstractSortedCollection(M map, AbstractSortedCollection<E, N, B, ?> parent, P range) {
        this(map, null, parent, range);
    }

    /**
     * Construct the object by the given {@link M} map, size, parent Collection
     * and {@link BiPredicate} elements range check bound for subs.
     *
     * @param map the given {@link M} map
     * @param size the given size
     * @param parent the given parent Collection
     * @param range the given {@link BiPredicate} elements range check bound
     * @param <P> the type of the {@link Serializable} {@link BiPredicate} intersection
     */
    protected <P extends BiPredicate<Comparator<E>, E> & Serializable> AbstractSortedCollection(M map, Integer size, AbstractSortedCollection<E, N, B, ?> parent, P range) {
        super(map, size);
        if ((this.parent = parent) != null)
            parent.child = new SubReference<>(this, parent.child);
        this.range = range;
    }

    @Override public Comparator<? super E> comparator() {
        return map.comparator(); }

    @Override public Iterator<E> firstAll() {
        return contains() ? getAll(map.firstKey()) : iterator(newBucket()); }
    @Override public Iterator<E> lastAll() {
        return contains() ? getAll(map.lastKey()) : iterator(newBucket()); }

    /**
     * Propagate size change for all parent and child sub-views.
     *
     * @param element the given element that was added or removed
     * @param number the given size adjustment number
     */
    @Override protected void count(E element, int number) {
        AbstractSortedCollection<E, N, B, ?> collection = this;
        while (collection.parent != null)
            collection = collection.parent;
        count(collection, element, number);
    }

    /**
     * Propagate size change for all child sub-views.
     *
     * @param collection the given child sub-view
     * @param element the given element that was added or removed
     * @param number the given size adjustment number
     */
    private void count(AbstractSortedCollection<E, N, B, ?> collection, E element, int number) {
        collection.size = collection.size == null ? null : collection.size + number;
        for (SubReference<E, N, B> child = collection.child; child != null; child = child.prev)
            if ((collection = child.get()) != null && collection.range.test(collection.comparator, element))
                count(collection, element, number);
    }

    private Comparator<E> comparator(Comparator<? super E> comparator) {
        return comparator != null ? (Comparator<E> & Serializable) comparator::compare
                : (Comparator<E> & Serializable) (e1, e2) -> Cast.<Comparable<E>>cast(e1).compareTo(e2);
    }

    private void readObject(ObjectInputStream input) throws IOException, ClassNotFoundException {
        input.defaultReadObject();

        for (Object read = input.readObject(); read instanceof AbstractSortedCollection; read = input.readObject())
            child = new SubReference<>(cast(read), child);
    }

    private void writeObject(ObjectOutputStream output) throws IOException {
        output.defaultWriteObject();

        AbstractSortedCollection<E, N, B, ?> sub;

        for (SubReference<E, N, B> child = this.child; child != null; child = child.prev)
            if ((sub = child.get()) != null)
                output.writeObject(sub);

        output.writeObject(0);
    }

    /**
     * The {@link WeakReference} link for the child sub-views.
     *
     * @param <E> the element type
     * @param <N> the type of the internal {@link Node}
     * @param <B> the type of the internal {@link Bucket} that holds duplicates
     */
    protected static class SubReference<E, N extends Node<E, N>, B extends Bucket<E, N, B>> extends WeakReference<AbstractSortedCollection<E, N, B, ?>> {

        final SubReference<E, N, B> prev;

        /**
         * Construct the reference by the given sub-view and previous linked sub-view reference.
         *
         * @param collection the given sub-view to link
         * @param prev the previous linked sub-view reference
         */
        SubReference(AbstractSortedCollection<E, N, B, ?> collection, SubReference<E, N, B> prev) {
            super(collection); this.prev = prev;
        }
    }
}
