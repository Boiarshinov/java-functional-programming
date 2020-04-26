package dev.boiarshinov.api.util.function;

import java.util.function.Predicate;
import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

public class PredicateTest {

    @Test
    public void testTest() {
        final Predicate<Boolean> simplePredicate = b -> b;

        Assert.assertTrue(simplePredicate.test(true));
        Assert.assertFalse(simplePredicate.test(false));
    }

    @Test(dataProvider = "numberForAndProvider")
    public void testAnd(Integer number, boolean expectedResult, boolean predicateShouldBeLazy) {
        final StringBuilder lazyMarkChecker = new StringBuilder();
        final Predicate<Integer> moreThanTen = num -> num > 10;
        Predicate<Integer> isEven = num -> {
            lazyMarkChecker.append("not lazy");
            return num % 2 == 0;
        };

        final boolean actualResult = moreThanTen.and(isEven).test(number);

        Assert.assertEquals(actualResult, expectedResult);
        if (predicateShouldBeLazy) {
            Assert.assertTrue(lazyMarkChecker.toString().isEmpty());
        } else {
            Assert.assertEquals(lazyMarkChecker.toString(), "not lazy");
        }
    }

    @DataProvider
    private Object[][] numberForAndProvider() {
        return new Object[][]{
            {12, true, false},
            {6, false, true},
            {11, false, false}
        };
    }

    @Test(dataProvider = "numberForOrProvider")
    public void testOr(Integer number, boolean expectedResult, boolean predicateShouldBeLazy) {
        final StringBuilder lazyMarkChecker = new StringBuilder();
        final Predicate<Integer> moreThanTen = num -> num > 10;
        Predicate<Integer> isEven = num -> {
            lazyMarkChecker.append("not lazy");
            return num % 2 == 0;
        };

        final boolean actualResult = moreThanTen.or(isEven).test(number);

        Assert.assertEquals(actualResult, expectedResult);
        if (predicateShouldBeLazy) {
            Assert.assertTrue(lazyMarkChecker.toString().isEmpty());
        } else {
            Assert.assertEquals(lazyMarkChecker.toString(), "not lazy");
        }
    }

    @DataProvider
    private Object[][] numberForOrProvider() {
        return new Object[][]{
            {8, true, false},
            {11, true, true},
            {9, false, false}
        };
    }

    @Test
    public void testOrAndAndFail() {
        Predicate<Boolean> predicate = b -> b;
        Predicate<Boolean> nullPredicate = null;

        Assert.assertThrows(NullPointerException.class,
            () -> predicate.or(nullPredicate).test(true));
        Assert.assertThrows(NullPointerException.class,
            () -> predicate.and(nullPredicate).test(false));
    }

    @Test
    public void testNegate() {
        final Predicate<Boolean> predicate = b -> b;
        final Predicate<Boolean> negatePredicate = predicate.negate();

        Assert.assertTrue(predicate.test(true));
        Assert.assertFalse(negatePredicate.test(true));
    }

    @Test
    public void testIsEqual() {
        final Object customObject = new Object();
        final Predicate<Object> equalsToCustomObject = Predicate.isEqual(customObject);

        Assert.assertTrue(equalsToCustomObject.test(customObject));
        Assert.assertFalse(equalsToCustomObject.test(new Object()));
    }
}
