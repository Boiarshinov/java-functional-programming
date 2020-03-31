package dev.boiarshinov.stepik;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.LongStream;
import java.util.stream.Stream;
import lombok.Data;
import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

/**
 * These tests are related to topic 1.5 - Learn more about map, reduce and forEach.
 * Part of these tests are solutions of exercises 2.20-2.24.
 */
public class MapReduceAndForEach {

    /**
     * Example of flatMap usage.
     */
    @Test
    private void flatMapExample() {
        final Book book1 = new Book("Java EE 7 Essentials", 2013, Arrays.asList("Arun Gupta"));
        final Book book2 = new Book("Algorithms", 2011, Arrays.asList("Robert Sedgewick", "Kevin Wayne"));
        final Book book3 = new Book("Clean code", 2014, Arrays.asList("Robert Martin"));
        final List<String> expectedAuthors =
            Arrays.asList("Arun Gupta", "Robert Sedgewick", "Kevin Wayne", "Robert Martin");

        final List<Book> javaBooks = Stream.of(book1, book2, book3).collect(Collectors.toList());

        final List<String> actualAuthors = javaBooks.stream()
            .flatMap(book -> book.getAuthors().stream())
            .distinct()
            .collect(Collectors.toList());

        Assert.assertEquals(actualAuthors.size(), expectedAuthors.size());
        actualAuthors.forEach(book -> Assert.assertTrue(expectedAuthors.contains(book)));
    }

    /**
     * Exercise 2.20 "Calculating a factorial".
     */
    @Test(dataProvider = "factorial")
    private void factorial(final long input, final long expected) {
        Assert.assertEquals(factorial(input), expected);
    }

    /**
     * @param n root of factorial.
     * @return factorial of n.
     */
    private static long factorial(long n) {
        return LongStream.rangeClosed(2, n).reduce(1, (acc, val) -> acc *= val);
    }

    /**
     * Data provider for exercise 2.20.
     * @return long number and its factorial.
     */
    @DataProvider(name = "factorial")
    private Object[][] factorialProvider() {
        return new Object[][]{
            {0, 1},
            {1, 1},
            {2, 2},
            {5, 120},
            {10, 3628800}
        };
    }

    /**
     * Exercise 2.21 "The sum of odd numbers".
     */
    @Test(dataProvider = "oddsSum")
    private void oddsSumInRange(final long start, final long end, final long expected) {
        Assert.assertEquals(sumOfOddNumbersInRange(start, end), expected);
    }

    /**
     * @param start start of range.
     * @param end end of range inclusive.
     * @return sum of odds in range.
     */
    private static long sumOfOddNumbersInRange(long start, long end) {
        return LongStream.rangeClosed(start, end).filter(x -> x % 2 != 0).reduce(0, (sum, x) -> sum += x);
    }

    /**
     * Data provider for exercise 2.21.
     * @return start and end of the range and also expected result.
     */
    @DataProvider(name = "oddsSum")
    private Object[][] oddsSumProvider() {
        return new Object[][]{
            {0, 0, 0},
            {7, 9, 16},
            {21, 30, 125}
        };
    }
}

@Data
class Book {
    private final String name;
    private final int year;
    private final List<String> authors;
}
