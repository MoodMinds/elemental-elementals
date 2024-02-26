package org.moodminds.elemental;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Iterator;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Spliterator;

import static java.util.Optional.ofNullable;
import static org.moodminds.sneaky.Cast.cast;

/**
 * Template implementation of the {@link Set} interface, which
 * is powered by an internal {@link Map}.
 *
 * @param <E> the element type
 * @param <M> the type of the internal {@link Map}
 */
public abstract class AbstractMapSet<E, M extends Map<E, Object>>
        extends java.util.AbstractSet<E> implements Set<E>, Serializable {

    private static final long serialVersionUID = 5732197695390790710L;

    /**
     * Masking {@code null} object holder field.
     */
    protected static final Object NULL = new Object();

    /**
     * Backing {@link M map} holder field.
     */
    protected transient M map;

    /**
     * Construct the object with the given {@link M} map.
     *
     * @param map the given  {@link M} map
     */
    protected AbstractMapSet(M map) {
        this.map = map;
    }

    @Override public Iterator<E> getAll(Object o) {
        return ofNullable(map.get(o)).<Iterator<E>>map(value -> new SetSingleIterator(unmask(value)))
                .orElseGet(SetEmptyIterator::new); }
    @Override public boolean add(E e) {
        return map.put(e, mask(e)) == null; }
    @Override public boolean remove(Object o) {
        return map.remove(o) != null; }
    @Override public void clear() {
        map.clear(); }
    @Override public boolean contains() {
        return !map.isEmpty(); }
    @Override public boolean contains(Object o) {
        return map.containsKey(o); }
    @Override public int size() {
        return map.size(); }
    @Override public Iterator<E> iterator() {
        return map.keySet().iterator(); }
    @Override public Spliterator<E> spliterator() {
        return map.keySet().spliterator(); }

    protected void serialize(ObjectOutputStream output) throws Exception {
        output.writeObject(map);
    }

    protected void deserialize(ObjectInputStream input) throws Exception {
        map = cast(input.readObject());
    }

    private void writeObject(ObjectOutputStream output) throws Exception {
        output.defaultWriteObject(); serialize(output);
    }

    private void readObject(ObjectInputStream input) throws Exception {
        input.defaultReadObject(); deserialize(input);
    }

    private Object mask(E o) {
        return o == null ? NULL : o;
    }

    private E unmask(Object o) {
        return cast(o == NULL ? null : o);
    }


    /**
     * Empty {@link Iterator} in this Set.
     */
    protected class SetEmptyIterator extends EmptyIterator<E> {

        Iterator<?> modCheckIterator = AbstractMapSet.this.iterator();

        @Override public E next() {
            modCheckIterator.next(); return super.next(); }
        @Override protected void removeElement() {
            /* will never happen */ }
    }

    /**
     * {@link Iterator} over a single value in this Set.
     */
    protected class SetSingleIterator extends SingleIterator<E> {

        Iterator<?> modCheckIterator = AbstractMapSet.this.iterator();

        public SetSingleIterator(E next) {
            super(next); }

        @Override public E next() {
            modCheckIterator.next(); return super.next(); }
        @Override protected void removeElement() {
            try { modCheckIterator.next(); }
            catch (NoSuchElementException ignored) {}
            map.remove(next); modCheckIterator = AbstractMapSet.this.iterator(); }
    }
}
