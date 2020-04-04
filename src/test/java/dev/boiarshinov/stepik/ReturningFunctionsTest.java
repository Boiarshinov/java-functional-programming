package dev.boiarshinov.stepik;

import java.util.Arrays;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.IntBinaryOperator;
import java.util.function.IntUnaryOperator;
import java.util.function.UnaryOperator;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

/**
 * These tests are related to topic 1.8 - Returning functions and currying.
 * Part of these tests are solutions of exercises 2.32-2.35.
 */
public class ReturningFunctionsTest {

    /**
     * Exercise 2.33 "Currying functions" task 1.
     */
    @Test(dataProvider = "sumOfLevels")
    public void sumOfLevels(final Integer a, final Integer b, final Integer c, final Integer expected) {
        Function<Integer, Function<Integer, Function<Integer, Integer>>> function =
            x -> y -> z -> x + y * y + z * z * z;

        final Integer actual = function.apply(a).apply(b).apply(c);

        Assert.assertEquals(actual, expected);
    }

    /**
     * Data provider for exercise 2.33 task 1.
     * @return three numbers and expected result.
     */
    @DataProvider
    public static Object[][] sumOfLevels() {
        return new Object[][]{
            {1, 1, 1, 3},
            {2, 3, 4, 75}
        };
    }

    /**
     * Exercise 2.33 "Currying functions" task 2.
     */
    @Test(dataProvider = "sumOfStrings")
    public void sumOfStrings(final String a, final String b, final String c, final String d, final String expected) {
        Function<String, Function<String, Function<String, Function<String, String>>>> function =
            word1 -> word2 -> word3 -> word4 -> word1.toLowerCase() + word3.toUpperCase() +
                word2.toLowerCase() + word4.toUpperCase();

        final String actual = function.apply(a).apply(b).apply(c).apply(d);

        Assert.assertEquals(actual, expected);
    }

    /**
     * Data provider for exercise 2.33 task 2.
     * @return four words and expected result.
     */
    @DataProvider
    public static Object[][] sumOfStrings() {
        return new Object[][]{
            {"aa", "bb", "cc", "dd", "aaCCbbDD"},
            {"AAA", "bbb", "CCC", "ddd", "aaaCCCbbbDDD"}
        };
    }

    /**
     * Exercise 2.34 "Multifunctional mapper".
     */
    @Test(dataProvider = "multifunctional")
    public void multifunctional(final List<Integer> numbers,
                                final List<Integer> expectedIdentity,
                                final List<Integer> expectedMult,
                                final List<Integer> expectedSquare)
    {
        final Function<List<IntUnaryOperator>, UnaryOperator<List<Integer>>> multifunctionalMapper =
            intUnaryOperators -> intList -> intList
                .stream()
                .map(num -> intUnaryOperators.stream().reduce(x -> x, IntUnaryOperator::andThen).applyAsInt(num))
                .collect(Collectors.toList());

        final UnaryOperator<List<Integer>> identityTransformation =
            multifunctionalMapper.apply(Arrays.asList(x -> x, x -> x, x -> x));
        final UnaryOperator<List<Integer>> multTwoAndThenAddOneTransformation =
            multifunctionalMapper.apply(Arrays.asList(x -> 2 * x, x -> x + 1));
        final UnaryOperator<List<Integer>> squareAndThenGetNextEvenNumberTransformation =
            multifunctionalMapper.apply(Arrays.asList(x -> x * x, x -> x % 2 == 0 ? x + 2 : x + 1));

        final List<Integer> actualIdentity = identityTransformation.apply(numbers);
        final List<Integer> actualMult = multTwoAndThenAddOneTransformation.apply(numbers);
        final List<Integer> actualSquare = squareAndThenGetNextEvenNumberTransformation.apply(numbers);

        Assert.assertEquals(actualIdentity, expectedIdentity);
        Assert.assertEquals(actualMult, expectedMult);
        Assert.assertEquals(actualSquare, expectedSquare);
    }

    /**
     * Data provider for exercise 2.34.
     * @return input list of numbers and three expected lists of numbers for each operator.
     */
    @DataProvider
    public static Object[][] multifunctional() {
        return new Object[][]{
            {Arrays.asList(1, 1, 1, 1), Arrays.asList(1, 1, 1, 1), Arrays.asList(3, 3, 3, 3), Arrays.asList(2, 2, 2, 2)},
            {Arrays.asList(1, 2, 3), Arrays.asList(1, 2, 3), Arrays.asList(3, 5, 7), Arrays.asList(2, 6, 10)}
        };
    }

    /**
     * Exercise 2.35 "Custom integer reducer".
     */
    @Test(dataProvider = "integerReducer")
    public void integerReducer(final int leftBoundary,
                                final int rightBoundary,
                                final int expectedSum,
                                final int expectedProduct)
    {
        final BiFunction<Integer, IntBinaryOperator, IntBinaryOperator> reduceIntOperator =
            (seed, combiner) -> (left, right) -> IntStream.rangeClosed(left, right).reduce(seed, combiner);
        final IntBinaryOperator sumOperator = reduceIntOperator.apply(0, Integer::sum);
        final IntBinaryOperator productOperator = reduceIntOperator.apply(1, (x, y) -> x * y);

        final int actualSum = sumOperator.applyAsInt(leftBoundary, rightBoundary);
        final int actualProduct = productOperator.applyAsInt(leftBoundary, rightBoundary);

        Assert.assertEquals(actualSum, expectedSum);
        Assert.assertEquals(actualProduct, expectedProduct);
    }

    /**
     * Data provider for exercise 2.35.
     * @return left and right boundaries and expected sum and product results.
     */
    @DataProvider
    public static Object[][] integerReducer() {
        return new Object[][]{
            {1, 4, 10, 24},
            {5, 6, 11, 30}
        };
    }
}
