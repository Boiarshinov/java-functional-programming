package dev.boiarshinov.stepik;

import java.util.stream.LongStream;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * These tests are related to topic 1.7 - Parallel streams.
 * Part of these tests are solutions of exercises 2.30-2.31.
 */
public class ParallelStreamsTest {

    /**
     * Exercise 2.31 "Parallel filtering of prime numbers".
     */
    @Test
    public void parallelStream() {
        final long start = 1L;
        final long end = 20L;
        final long expectedCount = 9L;

        final LongStream primesFilteringStream = createPrimesFilteringStream(start, end);
        final long actualCount = primesFilteringStream.count();

        Assert.assertEquals(actualCount, expectedCount);
    }

    /**
     * @param rangeBegin begin of the range inclusive.
     * @param rangeEnd end of the range inclusive.
     * @return parallel stream of prime longs.
     */
    private static LongStream createPrimesFilteringStream(long rangeBegin, long rangeEnd) {
        return LongStream.rangeClosed(rangeBegin, rangeEnd).filter(NumberUtils::isPrime).parallel();
    }

    /**
     * Util class for exercise 2.31.
     */
    private static class NumberUtils {
        static boolean isPrime(final long number){
            return LongStream.rangeClosed(2L, (long) Math.sqrt(number)).allMatch(value -> number % value != 0);
        }
    }
}
