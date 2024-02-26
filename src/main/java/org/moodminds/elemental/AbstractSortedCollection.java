package org.moodminds.elemental;

import org.moodminds.sneaky.Cast;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.lang.ref.Reference;
import java.lang.ref.WeakReference;
import java.util.Comparator;
import java.util.Iterator;
import java.util.Map;
import java.util.Optional;
import java.util.SortedMap;
import java.util.function.BiPredicate;
import java.util.function.Predicate;

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
            N extends AbstractSortedCollection.Node<E, N>,
            B extends AbstractSortedCollection.Bucket<E, N, B>,
            M extends SortedMap<E, Object>>
        extends AbstractMultiCollection<E, N, B, M> implements SortedCollection<E> {

    private static final long serialVersionUID = 8097575755780232788L;

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
     * Container size holder field.
     */
    protected Integer size = 0;

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
        super(map); this.size = size;
        if ((this.parent = parent) != null)
            parent.child = new SubReference<>(this, parent.child);
        this.range = range;
    }

    @Override public boolean add(E e) {
        super.add(e); propagateCount(e, +1); return true; }
    @Override public void clear() {
        super.clear(); propagateClear(); }

    @Override public int size() {
        if (size != null) return size;
        int size = 0; for (Object value : map.values())
            size = size + (isBucket(value) ? asBucket(value).size : 1);
        return this.size = size; }

    @Override public Comparator<? super E> comparator() {
        return map.comparator(); }

    @Override public Iterator<E> firstAll() {
        return contains() ? getAll(map.firstKey()) : iterator(null, false); }
    @Override public Iterator<E> lastAll() {
        return contains() ? getAll(map.lastKey()) : iterator(null, false); }

    @Override protected Iterator<E> iterator(B bucket) {
        return new CollectionBucketIterator(bucket.iterator()) {
            @Override public void remove() {
                super.remove(); propagateCount(next, -1); }
        }; }
    @Override protected Iterator<E> iterator(Object next, boolean hasNext) {
        return hasNext ? new CollectionSingleIterator(cast(next)) {
            @Override protected void removeElement() {
                super.removeElement(); propagateCount(next, -1); }
        } : new CollectionEmptyIterator(); }
    @Override protected Iterator<E> iterator(Iterator<Map.Entry<E, Object>> iterator) {
        return new CollectionIterator(iterator) {
            E next;
            @Override public E next() {
                return next = super.next(); }
            @Override protected void removeElement() {
                super.removeElement(); propagateCount(next, -1); }
        }; }

    @Override protected Optional<Integer> getSize() {
        return Optional.of(size); }

    /**
     * Propagate collection size change for all parent and child sub-views.
     *
     * @param element the given element that was added or removed
     * @param number the given size adjustment number
     */
    protected void propagateCount(E element, int number) {
        propagate(collection -> {
            if (collection.range.test(collection.comparator, element)) {
                if (collection.size != null)
                    collection.size = collection.size + number;
                return true;
            } return false;
        });
    }

    /**
     * Propagate collection clearing for all parent and child sub-views.
     */
    protected void propagateClear() {
        propagate(collection -> {
            collection.size = 0; return true;
        });
    }

    /**
     * Apply the provided {@link Predicate} operation to this Collection tree, beginning from the root,
     * and determine whether to execute this operation on sub-views by the returning boolean flag.
     *
     * @param action the {@link Predicate} operation to be applied
     */
    private void propagate(Predicate<AbstractSortedCollection<E, N, B, ?>> action) {
        AbstractSortedCollection<E, N, B, ?> root = this;
        while (root.parent != null)
            root = root.parent;
        new Object() {
            void propagate(AbstractSortedCollection<E, N, B, ?> collection) {
                if (action.test(collection))
                    for (SubReference<E, N, B> child = collection.child; child != null; child = child.prev)
                        if ((collection = child.get()) != null) propagate(collection); }
        }.propagate(root);
    }

    @Override
    protected void serialiaze(ObjectOutputStream output) throws Exception {
        super.serialiaze(output);

        AbstractSortedCollection<E, N, B, ?> sub;

        for (SubReference<E, N, B> child = this.child; child != null; child = child.prev)
            if ((sub = child.get()) != null)
                output.writeObject(sub);
        output.writeObject(0);
    }

    @Override
    protected void deserialize(ObjectInputStream input) throws Exception {
        super.deserialize(input);

        for (Object read = input.readObject(); read instanceof AbstractSortedCollection; read = input.readObject())
            child = new SubReference<>(cast(read), child);
    }


    /**
     * An inner duplicate elements container's node.
     *
     * @param <E> the element type
     * @param <N> the type of the internal {@link Node}
     */
    protected static class Node<E, N extends Node<E, N>>
            extends AbstractMultiCollection.Node<E, N> {

        private static final long serialVersionUID = -18764814922312942L;

        protected Node(E item) {
            super(item);
        }
    }

    /**
     * An inner duplicate elements linking container.
     *
     * @param <E> the element type
     * @param <N> the type of the internal {@link Node}
     * @param <B> the type of the internal {@link Bucket} that holds duplicates
     */
    protected static class Bucket<E, N extends Node<E, N>, B extends Bucket<E, N, B>>
            extends AbstractMultiCollection.Bucket<E, N, B> {

        private static final long serialVersionUID = -5403953493952113608L;

        protected Bucket(N head) {
            super(head);
        }
    }

    /**
     * The {@link WeakReference} link for the child sub-views.
     *
     * @param <E> the element type
     * @param <N> the type of the internal {@link Node}
     * @param <B> the type of the internal {@link Bucket} that holds duplicates
     */
    protected static class SubReference<E, N extends Node<E, N>, B extends Bucket<E, N, B>>
            extends WeakReference<AbstractSortedCollection<E, N, B, ?>> {

        /**
         * Previous sub-view holding field.
         */
        protected final SubReference<E, N, B> prev;

        /**
         * Construct the reference by the given sub-view and previous linked sub-view reference.
         *
         * @param collection the given sub-view to link
         * @param prev the previous linked sub-view reference
         */
        protected SubReference(AbstractSortedCollection<E, N, B, ?> collection, SubReference<E, N, B> prev) {
            super(collection); this.prev = prev;
        }
    }

    /**
     * Sub-collection extension of the {@link AbstractSortedCollection}.
     *
     * @param <E> the element type
     */
    protected static abstract class SortedSubCollection<E,
                N extends AbstractSortedCollection.Node<E, N>,
                B extends AbstractSortedCollection.Bucket<E, N, B>>
            extends AbstractSortedCollection<E, N, B, SortedMap<E, Object>> {

        private static final long serialVersionUID = -8180789980980477589L;

        protected <P extends BiPredicate<Comparator<E>, E> & Serializable> SortedSubCollection(SortedMap<E, Object> map,
                                                                                               AbstractSortedCollection<E, N, B, ?> parent,
                                                                                               P range) {
            super(map, parent, range);
        }

        @Override protected int totalMod() {
            return parent.totalMod(); }
        @Override protected void countMod() {
            parent.countMod(); }

        @Override protected N node(E item) {
            return parent.node(item); }
        @Override protected B bucket(N node) {
            return parent.bucket(node); }
        @Override protected boolean isBucket(Object value) {
            return parent.isBucket(value); }
        @Override protected boolean isMulti() {
            return parent.isMulti(); }
    }


    /**
     * Return the {@link Serializable} wrapper of the given {@link Comparator}.
     *
     * @param comparator the given {@link Comparator}
     * @return the {@link Serializable} wrapper of the given {@link Comparator}
     * @param <E> the value type
     */
    private static <E> Comparator<E> comparator(Comparator<? super E> comparator) {
        return comparator != null ? (Comparator<E> & Serializable) comparator::compare
                : (Comparator<E> & Serializable) (e1, e2) -> Cast.<Comparable<E>>cast(e1).compareTo(e2);
    }
}
