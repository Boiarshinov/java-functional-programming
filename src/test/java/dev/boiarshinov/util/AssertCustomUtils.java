package dev.boiarshinov.util;

import org.testng.Assert;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

public class AssertCustomUtils {

    /**
     * Asserts that two collections contain the same elements in random order. 
     * If they don't, an AssertionError is thrown.
     * 
     * @param actual actual collection.
     * @param expected expected collection.
     * @param <T> element type.
     */
    public static <T> void assertEqualsNoOrder(final Collection<T> actual,
                                               final Collection<T> expected)
    {
        if (actual == expected) {
            return;
        }
        
        if (actual == null || expected == null) {
            Assert.fail("Collections not equal: expected: " + expected + " and actual: " + actual);
        }
        
        Assert.assertEquals(actual.size(), expected.size(), "Collections don't have the same size:");
        final List<T> orphanElements = actual.stream()
            .filter(element -> !expected.contains(element))
            .collect(Collectors.toList());

        if (!orphanElements.isEmpty()) {
            final String orphansString = orphanElements.stream()
                .map(Object::toString)
                .collect(Collectors.joining(", "));
            Assert.fail("Expected collection don't have these elements from actual collection: " + orphansString);
        }
    }
}
