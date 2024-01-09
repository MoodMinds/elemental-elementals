package org.moodminds.elemental;

import java.io.Serializable;
import java.util.Iterator;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Spliterator;
import static org.moodminds.sneaky.Cast.cast;

/**
 * Template implementation of the {@link Set} interface, which
 * is powered by an internal {@link java.util.Map}.
 *
 * @param <E> the element type
 * @param <M> the type of the internal {@link Map}
 */
public abstract class AbstractSet<E, M extends Map<E, Object>>
        extends java.util.AbstractSet<E> implements Set<E>, Serializable {

    private static final long serialVersionUID = 5732197695390790710L;

    /**
     * Masking {@code null} object holder field.
     */
    protected static final Object NULL = new Object();

    protected final M map;

    /**
     * Construct the object with the given {@link M} map.
     *
     * @param map the given  {@link M} map
     */
    protected AbstractSet(M map) {
        this.map = map;
    }

    @Override public Iterator<E> getAll(Object o) {
        Object value = map.get(o); return iterator(unmask(value), value != null); }
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

    protected Object mask(E o) {
        return o == null ? NULL : o;
    }

    protected E unmask(Object o) {
        return cast(o == NULL ? null : o);
    }

    protected Iterator<E> iterator(E value, boolean hasValue) {
        return new AbstractRemoveIterator<E>() {

            Iterator<?> modCheckIterator = iterator(); boolean hasNext = hasValue;

            @Override public boolean hasNext() {
                return hasNext; }
            @Override public E next() {
                modCheckIterator.next(); return super.next(); }
            @Override protected E element() {
                hasNext = false; return value; }
            @Override protected void removeElement() {
                try { modCheckIterator.next(); }
                catch (NoSuchElementException ignored) {}
                map.remove(value); modCheckIterator = iterator(); }
        };
    }
}
