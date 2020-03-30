package dev.boiarshinov.stepik;

import java.util.Arrays;
import java.util.IntSummaryStatistics;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.LongStream;
import java.util.stream.Stream;
import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

/**
 * These tests are related to topic 1.4 - Introduction to streams.
 * Part of these tests are solutions of exercises 2.15-2.19.
 * There is no solution for exercises 2.15-2.16 because they too simple.
 */
public class IntroductionToStreamsTest {
    //todo: add missing exercises
    /**
     * Usage examples of {@link IntStream#summaryStatistics()}.
     */
    @Test
    private void summaryStatistics() {
        final IntSummaryStatistics statistics = IntStream.rangeClosed(1, 55_555).summaryStatistics();

        Assert.assertEquals(statistics.getCount(), 55_555);
        Assert.assertEquals(statistics.getMax(), 55_555);
        Assert.assertEquals(statistics.getMin(), 1);
        Assert.assertEquals(statistics.getAverage(), 27778);
        Assert.assertEquals(statistics.getSum(), 1_543_206_790);
    }

    /**
     * Exercise 2.17 "Checking if a number is prime".
     */
    @Test(dataProvider = "primeNumbers")
    private void primeNumbers(final long input, final boolean expected) {
        boolean actual = isPrime(input);

        Assert.assertEquals(actual, expected);
    }

    /**
     * Checking if a number is prime
     * @param number to test >= 2
     * @return true if number is prime else false
     */
    private static boolean isPrime(final long number) {
        return LongStream.rangeClosed(2L, (long) Math.sqrt(number)).allMatch(value -> number % value != 0);
    }

    /**
     * Data provider for exercise 2.17.
     * @return integer number and expected boolean value.
     */
    @DataProvider(name = "primeNumbers")
    public static Object[][] primeNumbers() {
        return new Object[][] {
            {2, true},
            {3, true},
            {4, false},
            {5, true},
            {29, true}
        };
    }

    /**
     * Exercise 2.18 "Bad words detecting".
     */
    @Test
    private void badWords() {
        final String text = "Вован пидр лох негодник лох";
        final List<String> badWords = Arrays.asList("петух", "пидр", "лох");
        final List<String> expected = Arrays.asList("лох", "пидр");

        final List<String> actual = createBadWordsDetectingStream(text, badWords).collect(Collectors.toList());

        Assert.assertEquals(actual, expected);
    }

    /**
     * @param text some text.
     * @param badWords list of bad words.
     * @return stream of bad words found in text sorted and without duplicates.
     */
    public static Stream<String> createBadWordsDetectingStream(final String text, final List<String> badWords) {
        return Arrays.stream(text.split(" "))
            .filter(badWords::contains)
            .distinct()
            .sorted();
    }
}
