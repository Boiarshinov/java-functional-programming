package dev.boiarshinov.stepik;

import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.function.Function;

/**
 * These tests are related to topic 1.3 - Functions are objects.
 * Part of these tests are solutions of exercises 2.7-2.14.
 */
public class FunctionsAreObjectsTest {

    /**
     * Usage examples of {@link Function#andThen(Function)} and {@link Function#compose(Function)}.
     */
    @Test
    private void composeAndThen() {
        final Function<Integer, Integer> sum = x -> x + 10;
        final Function<Integer, Integer> multiply = x -> x * 5;

        final Integer input = 5;
        final Integer multiplyAndSum = sum.compose(multiply).apply(input);
        final Integer sumAndMultiply = sum.andThen(multiply).apply(input);

        Assert.assertEquals(multiplyAndSum, Integer.valueOf(35));
        Assert.assertEquals(sumAndMultiply, Integer.valueOf(75));
    }
}
