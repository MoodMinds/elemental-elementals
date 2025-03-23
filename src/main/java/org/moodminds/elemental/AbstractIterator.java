package org.moodminds.elemental;

import java.lang.reflect.Method;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.function.Supplier;

import static java.util.Objects.requireNonNull;

/**
 * A template implementation of the {@link Iterator} interface, providing a default immutable behavior.
 * <p>
 * By default, instances of this iterator are immutable, meaning that calling {@link Iterator#remove()}
 * will result in an {@link UnsupportedOperationException}. To allow element removal, a {@link Runnable}
 * removal operation should be provided during construction, or the {@link #removeElement()} method should
 * be overridden to define custom removal behavior.
 * </p>
 *
 * @param <E> the type of elements
 */
public abstract class AbstractIterator<E> implements Iterator<E> {

    private static final Predicate<Method> REMOVE_METHOD_PREDICATE = method ->
            "removeElement".equals(method.getName()) && method.getParameterCount() == 0;


    private final Supplier<Boolean> hasPreviousPredicate = this::hasPrevious;
    private final Supplier<E> previousElementSupplier = this::previousElement;

    private final Supplier<Boolean> hasNextPredicate = this::hasNext;
    private final Supplier<E> nextElementSupplier = this::nextElement;

    private final Runnable removal;


    /**
     * Marker for the 'has previous' and 'has next' check states.
     */
    private Boolean hasPrevious, hasNext;

    /**
     * Marker for the 'on an element' cursor state.
     */
    private boolean current;

    /**
     * Construct the object.
     */
    protected AbstractIterator() {
        this(null);
    }

    /**
     * Construct the object with the given {@link Runnable} removal operation.
     *
     * @param removal the given {@link Runnable} removal operation
     */
    protected AbstractIterator(Runnable removal) {
        this.removal = removeElementDefined() ? removal != null ? () -> {
            removal.run(); removeElement();
        } : this::removeElement : removal;
    }

    /**
     * Determine whether this iterator has additional elements when moving
     * in the reverse direction, returning {@code true} if there are more
     * elements available for retrieval using {@link #previous} without
     * encountering an exception.
     *
     * @return {@code true} if there are more elements available for retrieval
     *         in the reverse direction, {@code false} otherwise
     */
    protected boolean hasPrevious() {
        hasNext = null; return hasPrevious == null && (hasPrevious = hasPreviousElement()) || hasPrevious;
    }

    /**
     * Retrieve the element that precedes the current cursor position and
     * moves the cursor backward. This method can be used iteratively to
     * navigate backward through the linearity or can be mixed with calls
     * to {@link #next} to traverse back and forth. It's important to note
     * that alternating calls to {@code next} and {@code previous} will
     * yield the same element repeatedly.
     *
     * @return the element preceding the current cursor position
     * @throws NoSuchElementException if there are no previous elements
     *         in the iteration
     */
    protected E previous() {
        E element = element(hasPreviousPredicate, previousElementSupplier); hasPrevious = null; return element;
    }

    /**
     * {@inheritDoc}
     *
     * @return {@inheritDoc}
     */
    @Override
    public boolean hasNext() {
        hasPrevious = null; return hasNext == null && (hasNext = hasNextElement()) || hasNext;
    }

    /**
     * {@inheritDoc}
     *
     * @return {@inheritDoc}
     * @throws NoSuchElementException {@inheritDoc}
     */
    @Override
    public E next() {
        E element = element(hasNextPredicate, nextElementSupplier); hasNext = null; return element;
    }

    /**
     * {@inheritDoc}
     *
     * @throws IllegalStateException {@inheritDoc}
     * @throws UnsupportedOperationException {@inheritDoc}
     */
    @Override
    public final void remove() {
        if (!current && removal != null)
            throw new IllegalStateException();
        if (removal != null) {
            removal.run(); current = false;
        } else throw new UnsupportedOperationException();
    }

    /**
     * Execute the given action for each preceding element in reverse order,
     * processing all elements unless an exception is thrown by the action.
     * <p>
     * If the iteration order is defined, actions are applied in reverse.
     * Any exceptions thrown by the action are propagated to the caller.
     * <p>
     * Modifying the source during iteration (e.g., using {@link #remove remove}
     * or other mutator methods) results in undefined behavior, unless a subclass
     * explicitly allows concurrent modifications.
     * <p>
     * If the action throws an exception, the subsequent behavior of the iterator
     * is unspecified.
     *
     * @param action The action to perform on each element
     * @throws NullPointerException if the action is {@code null}
     */
    protected void forEachPreceding(Consumer<? super E> action) {
        requireNonNull(action);
        if (hasPrevious()) {
            action.accept(previous());
            while (hasPreviousElement())
                action.accept(previousElement());
        }
    }

    /**
     * {@inheritDoc}
     *
     * @param action {@inheritDoc}
     * @throws NullPointerException {@inheritDoc}
     */
    @Override
    public void forEachRemaining(Consumer<? super E> action) {
        requireNonNull(action);
        if (hasNext()) {
            action.accept(next());
            while (hasNextElement())
                action.accept(nextElement());
        }
    }

    /**
     * Retrieve an element based on the provided predicate and supplier.
     *
     * <p>This method checks if an element is available using the {@code hasPredicate}. If the predicate evaluates to {@code false},
     * a {@link NoSuchElementException} is thrown. If the predicate evaluates to {@code true}, the element is obtained using the
     * {@code elementSupplier} and returned.</p>
     *
     * @param hasPredicate a {@link Supplier has predicate} that returns {@code true} if an element is available, {@code false} otherwise
     * @param elementSupplier an {@link Supplier element supplier} that provides the element to be retrieved
     * @return the element supplied by {@code elementSupplier} if {@code hasPredicate} is {@code true}
     * @throws NoSuchElementException if {@code hasPredicate} evaluates to {@code false}
     */
    private E element(Supplier<Boolean> hasPredicate, Supplier<E> elementSupplier) {
        if (!hasPredicate.get())
            throw new NoSuchElementException();
        E element = elementSupplier.get(); current = true; return element;
    }

    /**
     * Determine if more elements precede in the iteration.
     * A return value of {@code true} guarantees that a subsequent call
     * to {@link #previousElement()} will provide an element.
     *
     * @return {@code true} if additional elements are available in the iteration
     */
    protected boolean hasPreviousElement() {
        return false;
    }

    /**
     * Return the previous element immediately following a successful invocation of {@link #hasPreviousElement()}.
     *
     * @return the previous element if {@link #hasPreviousElement()} returned {@code true}
     */
    protected E previousElement() {
        return null;
    }

    /**
     * Determine if more elements remain in the iteration.
     * A return value of {@code true} guarantees that a subsequent call
     * to {@link #nextElement()} will provide an element.
     *
     * @return {@code true} if additional elements are available in the iteration
     */
    protected abstract boolean hasNextElement();

    /**
     * Return the next element immediately following a successful invocation of {@link #hasNextElement()}.
     *
     * @return the next element if {@link #hasNextElement()} returned {@code true}
     */
    protected abstract E nextElement();

    /**
     * Remove from the underlying source the last element returned by this iterator (optional operation).
     *
     * @implSpec
     * The default implementation throws an instance of
     * {@link UnsupportedOperationException} and performs no other action.
     *
     * @throws UnsupportedOperationException if the {@code remove} operation is not supported by this iterator
     */
    protected void removeElement() {}

    /**
     * Determine whether the removal operation is definitely supported.
     *
     * @return {@code true} if the removal operation is definitely supported, {@code false} otherwise.
     */
    protected boolean removeElementDefined() {
        for (Class<?> type = getClass(); type != AbstractIterator.class; type = type.getSuperclass())
            for (Method method : type.getDeclaredMethods())
                if (REMOVE_METHOD_PREDICATE.test(method))
                    return true;
        return false;
    }
}
