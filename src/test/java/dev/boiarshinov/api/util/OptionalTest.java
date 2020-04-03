package dev.boiarshinov.api.util;

import java.util.NoSuchElementException;
import java.util.Optional;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * Tests for {@link java.util.Optional} methods.
 */
public class OptionalTest {
    //todo: add tests for all methods

    /**
     * Test for {@link Optional#empty}.
     */
    @Test
    public void testEmpty() {
        final Optional<Object> empty = Optional.empty();

        Assert.assertFalse(empty.isPresent());
        Assert.assertThrows(NoSuchElementException.class, empty::get);
    }

    /**
     * Test for {@link Optional#of} and also {@link Optional#get}.
     */
    @Test
    public void testOf() {
        final Object okObject = new Object();
        final Object badObject = null;

        final Optional<Object> optional = Optional.of(okObject);
        final Assert.ThrowingRunnable optionalSupplier = () -> Optional.of(badObject);

        Assert.assertSame(okObject, optional.get());
        Assert.assertThrows(NullPointerException.class, optionalSupplier);
    }

    /**
     * Test for {@link Optional#ofNullable}.
     */
    @Test
    public void testOfNullable() {
        final Object okObject = new Object();
        final Object badObject = null;
        final Optional<Object> empty = Optional.empty();

        final Optional<Object> okOptional = Optional.ofNullable(okObject);
        final Optional<Object> badOptional = Optional.ofNullable(badObject);

        Assert.assertEquals(okOptional.get(), okObject);
        Assert.assertEquals(badOptional, empty);
    }
}
