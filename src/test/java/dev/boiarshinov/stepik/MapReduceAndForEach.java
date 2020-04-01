package dev.boiarshinov.stepik;

import java.util.Arrays;
import java.util.List;
import java.util.function.UnaryOperator;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
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

    /**
     * Exercise 2.22 "Understanding of flatMap together with stream creating".
     */
    @Test(dataProvider = "flatMap")
    private void flatMap(final UnaryOperator<List<Integer>> operator, final List<Integer> expected) {
        final List<Integer> numbers = Arrays.asList(1, 2, 3, 4, 5);

        final List<Integer> actual = operator.apply(numbers);

        Assert.assertEquals(actual.size(), expected.size());
        actual.forEach(n -> Assert.assertTrue(expected.contains(n)));
    }

    /**
     * Data provider for exercise 2.22.
     * @return unary operator that change incoming list of integers and expected result list.
     */
    @DataProvider(name = "flatMap")
    private Object[][] flatMapProvider() {
        final UnaryOperator<List<Integer>> operator1 = list -> list.stream()
                .flatMap(n -> Stream.generate(() -> n).limit(n))
                .collect(Collectors.toList());

        final UnaryOperator<List<Integer>> operator2 = list -> list.stream()
            .flatMapToInt(n -> IntStream.rangeClosed(1, n))
            .boxed()
            .collect(Collectors.toList());

        final UnaryOperator<List<Integer>> operator3 = list -> list.stream()
            .flatMapToInt(n -> IntStream.iterate(n, val -> val + 1).limit(n))
            .boxed()
            .collect(Collectors.toList());

        final UnaryOperator<List<Integer>> operator4 = list -> list.stream()
            .flatMap(Stream::of)
            .collect(Collectors.toList());

        return new Object[][]{
            {operator1, Arrays.asList(1, 2, 2, 3, 3, 3, 4, 4, 4, 4, 5, 5, 5, 5, 5)},
            {operator2, Arrays.asList(1, 1, 2, 1, 2, 3, 1, 2, 3, 4, 1, 2, 3, 4, 5)},
            {operator3, Arrays.asList(1, 2, 3, 3, 4, 5, 4, 5, 6, 7, 5, 6, 7, 8, 9)},
            {operator4, Arrays.asList(1, 2, 3, 4, 5)}
        };
    }
}

@Data
class Book {
    private final String name;
    private final int year;
    private final List<String> authors;
}
