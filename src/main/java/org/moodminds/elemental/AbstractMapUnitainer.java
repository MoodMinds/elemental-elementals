package org.moodminds.elemental;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Iterator;
import java.util.Map;
import java.util.Spliterator;

import static java.util.Spliterator.IMMUTABLE;
import static org.moodminds.sneaky.Cast.cast;

/**
 * Template unique-values implementation of the {@link Container}
 * interface, which is powered by an internal {@link Map}.
 *
 * @param <E> the element type
 * @param <M> the type of the internal {@link Map}
 */
public abstract class AbstractMapUnitainer<E, M extends Map<E, E>> extends AbstractContainer<E>
        implements Serializable {

    private static final long serialVersionUID = -2001815907225641108L;

    /**
     * Backing {@link M map} holder field.
     */
    protected transient M map;

    /**
     * Construct the object with the given {@link M} map.
     *
     * @param map the given {@link M} map
     */
    protected AbstractMapUnitainer(M map) {
        this.map = map;
    }

    @Override public Iterator<E> getAll(Object o) {
        E value = map.get(o); return (value != null || o == null && map.containsKey(null))
                ? SingleIterator.iterator(value) : EmptyIterator.iterator(); }
    @Override public int getCount(Object o) {
        return contains(o) ? 1 : 0; }
    @Override public boolean contains() {
        return !map.isEmpty(); }
    @Override public boolean contains(Object o) {
        return map.containsKey(o); }
    @Override public int size() {
        return map.size(); }
    @Override public Iterator<E> iterator() {
        return WrapIterator.wrap(map.keySet().iterator()); }
    @Override public Spliterator<E> spliterator() {
        return WrapSpliterator.wrap(map.keySet().spliterator(), ch -> ch | IMMUTABLE); }

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
}
