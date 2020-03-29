package dev.boiarshinov.stepik;

import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.IntSummaryStatistics;
import java.util.stream.IntStream;

/**
 * These tests are related to topic 1.4 - Introduction to streams.
 * Part of these tests are solutions of exercises 2.15-2.19.
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
}
