package dev.boiarshinov.api.util;

import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * Tests for {@link java.util.Optional} methods.
 */
public class OptionalTest {

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

    /**
     * Test for {@link Optional#isPresent()}.
     */
    @Test
    public void testIsPresent() {
        final Object okObject = new Object();
        final Object badObject = null;

        Assert.assertTrue(Optional.of(okObject).isPresent());
        Assert.assertFalse(Optional.ofNullable(badObject).isPresent());
    }

    /**
     * Test for {@link Optional#ifPresent(Consumer)}.
     */
    @Test
    public void testIfPresent() {
        final Object okObject = new Object();

        Optional.of(okObject).ifPresent(o -> Assert.assertEquals(o, okObject));
        Optional.empty().ifPresent(o -> {throw new IllegalStateException(o.toString());});
    }

    /**
     * Test for {@link Optional#get()}.
     */
    @Test
    public void testGet() {
        final Object okObject = new Object();

        final Optional<Object> okOptional = Optional.of(okObject);
        Assert.assertSame(okOptional.get(), okObject);

        final Assert.ThrowingRunnable fail = () -> Optional.empty().get();
        Assert.assertThrows(NoSuchElementException.class, fail);
    }

    /**
     * Test for {@link Optional#orElse(Object)}.
     */
    @Test
    public void testOrElse() {
        final Object okObject = new Object();
        final Object replacingObject = new Object();

        final Optional<Object> notEmpty = Optional.of(okObject);
        final Optional<Object> empty = Optional.empty();

        Assert.assertSame(notEmpty.orElse(replacingObject), okObject);
        Assert.assertSame(empty.orElse(replacingObject), replacingObject);
        Assert.assertSame(empty.orElse(null), null);
    }

    /**
     * Test for {@link Optional#orElseGet(Supplier)}.
     */
    @Test
    public void testOrElseGet() {
        final Object okObject = new Object();
        final Object replacingObject = new Object();
        final Supplier<Object> replacingSupplier = () -> replacingObject;

        final Optional<Object> notEmpty = Optional.of(okObject);
        final Optional<Object> empty = Optional.empty();

        Assert.assertSame(notEmpty.orElseGet(replacingSupplier), okObject);
        Assert.assertSame(empty.orElseGet(replacingSupplier), replacingObject);
        Assert.assertThrows(NullPointerException.class, () -> empty.orElseGet(null));
    }

    /**
     * Test for {@link Optional#orElseThrow(Supplier)}.
     */
    @Test
    public void testOrElseThrow() {
        final Object okObject = new Object();
        final Supplier<RuntimeException> replacingSupplier = () -> {throw new NoSuchElementException();};

        final Optional<Object> notEmpty = Optional.of(okObject);
        final Optional<Object> empty = Optional.empty();

        Assert.assertSame(notEmpty.orElseThrow(replacingSupplier), okObject);
        Assert.assertThrows(NoSuchElementException.class, () -> empty.orElseThrow(replacingSupplier));
        Assert.assertThrows(NullPointerException.class, () -> empty.orElseThrow(() -> null));
    }

    /**
     * Test for {@link Optional#filter(Predicate)}.
     */
    @Test
    public void testFilter() {
        final Object okObject = new Object();
        final Boolean badBoolean = null;

        final Predicate truePredicate = o -> true;
        final Predicate falsePredicate = o -> false;

        final Optional<Object> filtered = Optional.of(okObject).filter(truePredicate);
        final Optional<Object> notFiltered = Optional.of(okObject).filter(falsePredicate);

        Assert.assertTrue(filtered.isPresent());
        Assert.assertFalse(notFiltered.isPresent());
        Assert.assertFalse(Optional.empty().filter(falsePredicate).isPresent());
        Assert.assertFalse(Optional.empty().filter(truePredicate).isPresent());
        Assert.assertThrows(NullPointerException.class, () -> Optional.of(okObject).filter(o -> badBoolean));
        Assert.assertThrows(NullPointerException.class, () -> Optional.empty().filter(null));
        Assert.assertFalse(Optional.empty().filter(o -> badBoolean).isPresent());
    }

    /**
     * Test for {@link Optional#map(Function)}.
     */
    @Test
    public void testMap() {
        final Object okObject = new Object();
        final String okString = okObject.toString();

        final Optional<String> mapped = Optional.of(okObject).map(Object::toString);
        final Optional<String> badMapped = Optional.empty().map(Object::toString);

        Assert.assertEquals(mapped.get(), okString);
        Assert.assertFalse(badMapped.isPresent());
        Assert.assertThrows(NullPointerException.class, () -> Optional.of(okObject).map(null));
    }

    /**
     * Test for {@link Optional#flatMap(Function)}.
     */
    @Test
    public void testFlatMap() {
        final Object okObject = new Object();
        final String okString = okObject.toString();

        final Function<Object, Optional<Object>> function = Optional::ofNullable;

        final Optional<Object> flatted = Optional.of(okObject).flatMap(function);
        final Optional<Object> badFlatted = Optional.empty().flatMap(function);

        Assert.assertSame(flatted.get(), okObject);
        Assert.assertFalse(badFlatted.isPresent());
    }
}
