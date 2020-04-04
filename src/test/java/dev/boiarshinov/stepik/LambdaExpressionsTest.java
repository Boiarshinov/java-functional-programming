package dev.boiarshinov.stepik;

import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.util.*;
import java.util.function.BinaryOperator;
import java.util.function.DoubleUnaryOperator;
import java.util.function.UnaryOperator;
import java.util.stream.Collectors;
import java.util.stream.LongStream;
import java.util.stream.Stream;

/**
 * These tests are related to topic 1.2 - Lambda expressions and Method references.
 * Part of these tests are solutions of exercises 2.2-2.6.
 */
public class LambdaExpressionsTest {

    /**
     * Exercise 2.2 "Writing simple lambda expressions" task 1.
     */
    @Test
    public void testGetMaxOfTwo() {
        BinaryOperator<Integer> getMaxOfTwo = (a, b) -> a >= b ? a : b;
        final Integer SMALL = 5;
        final Integer BIG = 10;

        Assert.assertEquals(getMaxOfTwo.apply(SMALL, BIG), BIG);
    }

    /**
     * Exercise 2.2 "Writing simple lambda expressions" task 2.
     */
    @Test
    public void testGetNextEven() {
        UnaryOperator<Integer> getNextEvenNumber = (num) -> num % 2 == 0 ? num + 2 : num + 1;
        final Integer EVEN = 4;
        final Integer ODD = 5;
        final Integer NEXT_EVEN = 6;

        Assert.assertEquals(getNextEvenNumber.apply(EVEN), NEXT_EVEN);
        Assert.assertEquals(getNextEvenNumber.apply(ODD), NEXT_EVEN);
    }

    /**
     * Exercise 2.3 "Too many arguments".
     */
    @Test(dataProvider = "appendAndUpperCase")
    public void testTooManyArguments(
        String s1, String s2, String s3, String s4, String s5, String s6, String s7, String expected)
    {
        SevenStringsFunction appendAndUpperCase = (first, second, third, fourth, fifth, sixth, seventh) ->
            String.join("", first, second, third, fourth, fifth, sixth, seventh).toUpperCase();

        final String actual = appendAndUpperCase.apply(s1, s2, s3, s4, s5, s6, s7);

        Assert.assertEquals(actual, expected);
    }

    /**
     * Data provider for exercise 2.3.
     * @return left and right borders and expected result.
     */
    @DataProvider(name = "appendAndUpperCase")
    private Object[][] provideDataToAppend() {
        return new Object[][]{
            {"ab", "ab", "ab", "ab", "ab", "ab", "ab", "ABABABABABABAB"},
            {"The", "lambda", "has", "too", "many", "string", "arguments", "THELAMBDAHASTOOMANYSTRINGARGUMENTS"}
        };
    }

    /**
     * Exercise 2.4 "Calculating production of all numbers in the range".
     */
    @Test(dataProvider = "production")
    public void testProductionOfRange(Long leftBorder, Long rightBorder, Long expected) {
        BinaryOperator<Long> productionOfRange = (left, right) -> {
            long production = 1L;
            for (long i = left; i <= right; i++) {
                production *= i;
            }
            return production;
        };
        //Variant with streams
        BinaryOperator<Long> productionOfRangeByStream = (left, right) -> Stream
            .iterate(left, a -> a += 1)
            .limit(right - left + 1)
            .reduce(1L, (a, b) -> a * b);
        //Most popular solution from course
        BinaryOperator<Long> productionOfRangeByLongStream =
            (left, right) -> LongStream
                .rangeClosed(left, right)
                .reduce(1L, (accumulator, el) -> accumulator * el);

        Assert.assertEquals(productionOfRange.apply(leftBorder, rightBorder), expected);
        Assert.assertEquals(productionOfRangeByStream.apply(leftBorder, rightBorder), expected);
        Assert.assertEquals(productionOfRangeByLongStream.apply(leftBorder,rightBorder), expected);
    }

    /**
     * Data provider for exercise 2.4.
     * @return left and right borders and expected result.
     */
    @DataProvider(name = "production")
    private Object[][] provideDataToProduction() {
        return new Object[][]{
            {0L, 1L, 0L},
            {2L, 2L, 2L},
            {1L, 4L, 24L},
            {5L, 15L, 54486432000L}
        };
    }

    /**
     * Exercise 2.5 "Getting distinct strings".
     */
    @Test(dataProvider = "distinct")
    public void testDistinct(List<String> inputList, List<String> expected) {
        UnaryOperator<List<String>> distinct = strings -> strings.stream().distinct().collect(Collectors.toList());
        //The most popular solution from course.
        UnaryOperator<List<String>> distinctBySet = strings -> new ArrayList<>(new HashSet<>(strings));

        List<String> actual = distinct.apply(inputList);
        Collections.sort(actual);
        List<String> actualBySet = distinctBySet.apply(inputList);
        Collections.sort(actualBySet);

        Assert.assertEquals(actual, expected);
        Assert.assertEquals(actualBySet, expected);
    }

    /**
     * Data provider for exercise 2.5.
     * @return two lists with input data and expected data.
     */
    @DataProvider(name = "distinct")
    private Object[][] provideDataToDistinct() {
        return new Object[][]{
            {Arrays.asList("a", "b", "a", "c"),
                Arrays.asList("a", "b", "c")},
            {Arrays.asList("java", "scala", "java", "clojure", "clojure"),
                Arrays.asList("clojure", "java", "scala")},
            {Arrays.asList("the", "three", "the", "three", "the", "three", "an", "an", "a"),
                Arrays.asList("a", "an", "the", "three")}
        };
    }

    /**
     * Exercise 2.6 "Writing closures" task 1.
     */
    @Test(dataProvider = "quadraticEquation")
    public void testQuadraticEquation(final Integer a,
                                      final Integer b,
                                      final Integer c,
                                      final Double input,
                                      final Double expected)
    {
        DoubleUnaryOperator quadraticEquation = x -> a * x * x + b * x + c;

        int compareResult = Double.compare(quadraticEquation.applyAsDouble(input), expected);

        Assert.assertEquals(compareResult, 0);
    }

    /**
     * Data provider for exercise 2.6 task 1.
     * @return a, b, c, input, result
     */
    @DataProvider(name = "quadraticEquation")
    private Object[][] provideDataToQuadraticEquation() {
        return new Object[][]{
            {0, 1, -1, 1d, 0d},
            {1, 0, -4, 2d, 0d},
            {1, -1, 0, 0d, 0d},
            {1, -3, 2, 1d, 0d}
        };
    }

    /**
     * Exercise 2.6 "Writing closures" task 2.
     */
    @Test
    public void testAdditionalAdder() {
        final String suffix = "ism";
        final String prefix = "para";

        UnaryOperator<String> addAdditionals = string -> prefix + string.trim() + suffix;

        final String input = " planer ";
        final String expected = "paraplanerism";

        Assert.assertEquals(addAdditionals.apply(input), expected);
    }

    /**
     * Custom functional interface for exercise 2.3.
     */
    @FunctionalInterface
    interface SevenStringsFunction {
        String apply(String first,
                     String second,
                     String third,
                     String fourth,
                     String fifth,
                     String sixth,
                     String seventh);
    }
}
