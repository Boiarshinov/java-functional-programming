package dev.boiarshinov.stepik;

import lombok.Data;
import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.*;
import java.util.function.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * These tests are related to topic 1.3 - Functions are objects.
 * Part of these tests are solutions of exercises 2.7-2.14.
 */
public class FunctionsAreObjectsTest {
    //todo: add missing exercises

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

    /**
     * Exercise 2.9 "Behaviour parametrization with lambda expressions".
     */
    @Test
    private void behaviorParametrization() {
        final Long BIG_CASH = 100_000_000L;
        final List<Account> accounts = Arrays.asList(
            new Account("Judy Doe", -5L, false),
            new Account("John Doe", BIG_CASH, false)
        );

        final List<Account> nonEmptyAccounts = filter(accounts, account -> account.getBalance() > 0L);
        final List<Account> accountsWithTooMuchMoney = filter(
            accounts,
            (account -> !account.isLocked() && account.getBalance() >= BIG_CASH)
        );

        Assert.assertEquals(nonEmptyAccounts, Collections.singletonList(accounts.get(1)));
        Assert.assertEquals(accountsWithTooMuchMoney, Collections.singletonList(accounts.get(1)));
    }

    /**
     * Method for exercise 2.9.
     * @param elems list of elements.
     * @param predicate predicate to filter list.
     * @param <T> type of list elements.
     * @return list filtered by predicate.
     */
    private static <T> List<T> filter(List<T> elems, Predicate<T> predicate) {
        return elems.stream().filter(predicate).collect(Collectors.toList());
    }

    /**
     * Exercise 2.10 "Your own functional interface".
     */
    @Test(dataProvider = "ternaryPredicate")
    private void myOwnFunctionalInterface(int i, int j, int k, boolean areDifferent) {
        final TernaryIntPredicate areAllDifferent = (a, b, c) -> a != b && b != c && c != a;

        Assert.assertEquals(areAllDifferent.test(i, j, k), areDifferent);
    }

    /**
     * Data provider for exercise 2.10.
     * @return three integer numbers and boolean value.
     */
    @DataProvider(name = "ternaryPredicate")
    private Object[][] provideToTernaryPredicate() {
        return new Object[][]{
            {1, 2, 3, true},
            {1, 1, 1, false},
            {1, 1, 2, false},
            {1, 2, 1, false},
            {1, 2, 2, false}
        };
    }

    /**
     * Exercise 2.11 "Understanding of the function composition". Task 1.
     */
    @Test
    private void unaryOperatorComposition() {
        final IntUnaryOperator mult2 = num -> num * 2;
        final IntUnaryOperator add3 = num -> num + 3;

        final IntUnaryOperator combinedOperator = add3.compose(mult2.andThen(add3)).andThen(mult2);
        final int result = combinedOperator.applyAsInt(5);

        Assert.assertEquals(result, 32);
    }

    /**
     * Exercise 2.11 "Understanding of the function composition". Task 2.
     */
    @Test
    private void functionComposition() {
        final Consumer<Integer> printer = System.out::println;
        final Consumer<Integer> devNull = (val) -> { int v = val * 2; };

        final Consumer<Integer> combinedConsumer = devNull.andThen(devNull.andThen(printer));
        combinedConsumer.accept(100);
    }

    /**
     * Exercise 2.12 "Composing predicates".
     */
    @Test(dataProvider = "disjunction")
    private void listDisjunction(int input, List<IntPredicate> predicates, boolean expected) {
        Assert.assertEquals(disjunctAll(predicates).test(input), expected);
        Assert.assertEquals(disjunctAllByStreams(predicates).test(input), expected);
    }

    /**
     * Old fashion written predicates disjunction. For exercise 2.12.
     * @param predicates list of {@link IntPredicate}.
     * @return disjunction of all predicates.
     */
    private static IntPredicate disjunctAll(List<IntPredicate> predicates) {
        IntPredicate result = a -> false;
        for (IntPredicate predicate: predicates) {
            result = result.or(predicate);
        }
        return result;
    }

    /**
     * Another implementation of predicates disjunction written by streams. For exercise 2.12.
     * @param predicates list of {@link IntPredicate}.
     * @return disjunction of all predicates.
     */
    private static IntPredicate disjunctAllByStreams(List<IntPredicate> predicates) {
        return predicates.stream().reduce(a -> false, IntPredicate::or);
    }

    /**
     * Data provider for exercise 2.12.
     * @return input value, list of {@link IntPredicate} and expected boolean result.
     */
    @DataProvider(name = "disjunction")
    private Object[][] provideIntPredicates() {
        final IntPredicate negative = value -> value < 0;
        final IntPredicate equalsZero = value -> value == 0;
        final IntPredicate positive = value -> value > 0;

        final List<IntPredicate> empty = new ArrayList<>();
        final List<IntPredicate> all = Arrays.asList(negative, equalsZero, positive);
        final List<IntPredicate> negativeOrZero = Arrays.asList(negative, equalsZero);
        final List<IntPredicate> onlyPositive = Collections.singletonList(positive);

        final int input = 5;

        return new Object[][] {
            {input, empty, false},
            {input, all, true},
            {input, negativeOrZero, false},
            {input, onlyPositive, true}
        };
    }

    /**
     * Exercise 2.14 "The chain of responsibility pattern in the functional style".
     */
    @Test
    private void chainOfResponsibility() {
        final String data =
            "<type>payment</type>" +
            "<sum>100000</sum>" +
            "<order_id>e94dc619-6172-4ffe-aae8-63112c551570</order>" +
            "<desc>We'd like to buy an elephant</desc>";
        final String expected = "<request>" + "<transaction>" + data + "</transaction>" +
                "<digest>CZVMYTgc3iiOdJjFP+6dhQ==</digest>" + "</request>";

        final Request actual = commonRequestHandler.handle(new Request(data));

        Assert.assertEquals(actual.getData(), expected);
    }

    /**
     * Accepts a request and returns new request with data wrapped in the tag <transaction>...</transaction>
     */
    final static RequestHandler wrapInTransactionTag =
        (req) -> new Request(String.format("<transaction>%s</transaction>", req.getData()));

    /**
     * Accepts a request and returns a new request with calculated digest inside the tag <digest>...</digest>
     */
    final static RequestHandler createDigest =
        (req) -> {
            String digest = "";
            try {
                final MessageDigest md5 = MessageDigest.getInstance("MD5");
                final byte[] digestBytes = md5.digest(req.getData().getBytes(StandardCharsets.UTF_8));
                digest = new String(Base64.getEncoder().encode(digestBytes));
            } catch (Exception ignored) { }
            return new Request(req.getData() + String.format("<digest>%s</digest>", digest));
        };

    /**
     * Accepts a request and returns a new request with data wrapped in the tag <request>...</request>
     */
    final static RequestHandler wrapInRequestTag =
        (req) -> new Request(String.format("<request>%s</request>", req.getData()));

    /**
     * It should represents a chain of responsibility combined from another handlers.
     * The format: commonRequestHandler = handler1.setSuccessor(handler2.setSuccessor(...))
     * The combining method setSuccessor may has another name
     */
    final static RequestHandler commonRequestHandler =
        wrapInRequestTag.combine(
            createDigest.combine(
                wrapInTransactionTag));
}

/**
 * Class for exercise 2.9.
 */
@Data
class Account {
    private final String number;
    private final Long balance;
    private final boolean isLocked;
}

/**
 * Interface for exercise 2.10.
 */
@FunctionalInterface
interface TernaryIntPredicate {
    boolean test(int a, int b, int c);
}

/**
 * Class for exercise 2.14.
 */
@Data
class Request {
    private final String data;
}

/**
 * Interface for exercise 2.14.
 */
@FunctionalInterface
interface RequestHandler {
    Request handle(Request request);

    default RequestHandler combine(RequestHandler handler) {
        return (v) -> handle(handler.handle(v));
    }
}