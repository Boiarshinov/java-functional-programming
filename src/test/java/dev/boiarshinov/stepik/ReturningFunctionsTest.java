package dev.boiarshinov.stepik;

import java.util.function.Function;
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
    private void sumOfLevels(final Integer a, final Integer b, final Integer c, final Integer expected) {
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
        return new Object[][] {
            {1, 1, 1, 3},
            {2, 3, 4, 75}
        };
    }

    /**
     * Exercise 2.33 "Currying functions" task 2.
     */
    @Test(dataProvider = "sumOfStrings")
    private void sumOfStrings(final String a, final String b, final String c, final String d, final String expected) {
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
        return new Object[][] {
            {"aa", "bb", "cc", "dd", "aaCCbbDD"},
            {"AAA", "bbb", "CCC", "ddd", "aaaCCCbbbDDD"}
        };
    }
}
