package dev.boiarshinov.stepik;

import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.Arrays;
import java.util.List;
import java.util.function.Function;
import java.util.function.IntPredicate;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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

    /**
     * Usage examples of {@link Predicate#negate()} and {@link Predicate#or(Predicate)}.
     */
    @Test
    private void predicates() {
        final IntPredicate isEven = x -> x % 2 == 0;
        final IntPredicate dividedBy3 = x -> x % 3 == 0;
        final IntPredicate oddOrDividedBy3 = isEven.negate().or(dividedBy3);
        final List<Integer> expected =
            Arrays.asList(1, 3, 5, 6, 7, 9, 11, 12, 13, 15, 17, 18, 19, 21, 23, 24, 25, 27, 29, 30);

        final List<Integer> actual = Stream
            .iterate(1, a -> a += 1)
            .limit(30)
            .filter(oddOrDividedBy3::test)
            .collect(Collectors.toList());

        Assert.assertEquals(actual, expected);
    }
}
