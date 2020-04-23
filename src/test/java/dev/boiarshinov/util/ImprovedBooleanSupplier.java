package dev.boiarshinov.util;

import java.util.Objects;
import java.util.function.BooleanSupplier;

/**
 * {@link java.util.function.BooleanSupplier}, with added default logical methods of conjunction,
 * disjunction and negate like in the {@link java.util.function.Predicate}.
 * @see java.util.function.BooleanSupplier
 * @see java.util.function.Predicate
 */
@FunctionalInterface
public interface ImprovedBooleanSupplier extends BooleanSupplier {

    /**
     * Returns a composed boolean supplier that represents a short-circuiting logical AND of this
     * boolean supplier and another. When evaluating the composed boolean supplier, if this boolean
     * supplier is {@code false}, then the {@code other} boolean supplier is not evaluated.
     *
     * @param other a boolean supplier that will be logically-ANDed with this boolean supplier.
     * @return composed boolean supplier that represents the short-circuiting logical AND
     * of the boolean supplier and the {@code other} boolean supplier.
     * @throws NullPointerException if {@code other} is null
     */
    default ImprovedBooleanSupplier and(final BooleanSupplier other) {
        Objects.requireNonNull(other);
        return () -> this.getAsBoolean() && other.getAsBoolean();
    }

    /**
     * Returns a composed boolean supplier that represents a short-circuiting logical OR of this
     * boolean supplier and another. When evaluating the composed boolean supplier, if this boolean
     * supplier is {@code true}, then the {@code other} boolean supplier is not evaluated.
     *
     * @param other a boolean supplier that will be logically-ORed with this boolean supplier.
     * @return composed boolean supplier that represents the short-circuiting logical OR
     * of the boolean supplier and the {@code other} boolean supplier.
     * @throws NullPointerException if {@code other} is null
     */
    default ImprovedBooleanSupplier or(final BooleanSupplier other) {
        Objects.requireNonNull(other);
        return () -> getAsBoolean() || other.getAsBoolean();
    }

    /**
     * @return a boolean supplier that represent the logical negation of this boolean supplier.
     */
    default ImprovedBooleanSupplier negate() {
        return () -> !this.getAsBoolean();
    }
}
