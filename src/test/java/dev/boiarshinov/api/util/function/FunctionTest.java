package dev.boiarshinov.api.util.function;

import java.util.function.Function;
import org.testng.Assert;
import org.testng.annotations.Test;

public class FunctionTest {

    @Test
    public void testApply() {
        final Function<String, String> toUpperCase = String::toUpperCase;

        Assert.assertEquals(toUpperCase.apply("test"), "TEST");
    }

    @Test
    public void testCompose() {
        final Function<Double, String> formatDouble = d -> String.format("%.3f", d);
        final Function<String, Integer> getLength = String::length;

        final Function<Double, Integer> countSymbols = getLength.compose(formatDouble);

        Assert.assertEquals(countSymbols.apply(5.16), Integer.valueOf(5));
    }

    @Test
    public void testComposeFail() {
        final Function<String, String> nullFunction = null;
        final Function<String, String> toUpperCase = String::toUpperCase;

        Assert.assertThrows(NullPointerException.class, () -> toUpperCase.compose(nullFunction));
    }

    @Test
    public void testAndThen() {
        final Function<String, StringBuilder> firstFunction = StringBuilder::new;
        final Function<StringBuilder, String> secondFunction = sb -> sb.reverse().toString();
        final String expectedResult = "nolem on ,nomel oN";

        final String actualResult = firstFunction.andThen(secondFunction).apply("No lemon, no melon");

        Assert.assertEquals(actualResult, expectedResult);
    }

    @Test
    public void testAndThenFail() {
        final Function<String, String> nullFunction = null;
        final Function<String, String> toUpperCase = String::toUpperCase;

        Assert.assertThrows(NullPointerException.class, () -> toUpperCase.andThen(nullFunction));
    }

    @Test
    public void testIdentity() {
        final Function<String, String> identityFunction = Function.identity();
        final String example = "example";

        Assert.assertEquals(identityFunction.apply(example), example);
    }
}
