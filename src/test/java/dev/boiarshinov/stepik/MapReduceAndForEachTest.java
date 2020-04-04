package dev.boiarshinov.stepik;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.function.UnaryOperator;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.LongStream;
import java.util.stream.Stream;
import lombok.Data;
import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

/**
 * These tests are related to topic 1.5 - Learn more about map, reduce and forEach.
 * Part of these tests are solutions of exercises 2.20-2.24.
 */
public class MapReduceAndForEachTest {

    /**
     * Example of flatMap usage.
     */
    @Test
    public void flatMapExample() {
        final Book book1 = new Book("Java EE 7 Essentials", 2013, Arrays.asList("Arun Gupta"));
        final Book book2 = new Book("Algorithms", 2011, Arrays.asList("Robert Sedgewick", "Kevin Wayne"));
        final Book book3 = new Book("Clean code", 2014, Arrays.asList("Robert Martin"));
        final List<String> expectedAuthors =
            Arrays.asList("Arun Gupta", "Robert Sedgewick", "Kevin Wayne", "Robert Martin");

        final List<Book> javaBooks = Stream.of(book1, book2, book3).collect(Collectors.toList());

        final List<String> actualAuthors = javaBooks.stream()
            .flatMap(book -> book.getAuthors().stream())
            .distinct()
            .collect(Collectors.toList());

        Assert.assertEquals(actualAuthors.size(), expectedAuthors.size());
        actualAuthors.forEach(book -> Assert.assertTrue(expectedAuthors.contains(book)));
    }

    /**
     * Exercise 2.20 "Calculating a factorial".
     */
    @Test(dataProvider = "factorial")
    public void factorial(final long input, final long expected) {
        Assert.assertEquals(factorial(input), expected);
    }

    /**
     * @param n root of factorial.
     * @return factorial of n.
     */
    private static long factorial(long n) {
        return LongStream.rangeClosed(2, n).reduce(1, (acc, val) -> acc *= val);
    }

    /**
     * Data provider for exercise 2.20.
     * @return long number and its factorial.
     */
    @DataProvider(name = "factorial")
    private Object[][] factorialProvider() {
        return new Object[][]{
            {0, 1},
            {1, 1},
            {2, 2},
            {5, 120},
            {10, 3628800}
        };
    }

    /**
     * Exercise 2.21 "The sum of odd numbers".
     */
    @Test(dataProvider = "oddsSum")
    public void oddsSumInRange(final long start, final long end, final long expected) {
        Assert.assertEquals(sumOfOddNumbersInRange(start, end), expected);
    }

    /**
     * @param start start of range.
     * @param end end of range inclusive.
     * @return sum of odds in range.
     */
    private static long sumOfOddNumbersInRange(long start, long end) {
        return LongStream.rangeClosed(start, end).filter(x -> x % 2 != 0).reduce(0, (sum, x) -> sum += x);
    }

    /**
     * Data provider for exercise 2.21.
     * @return start and end of the range and also expected result.
     */
    @DataProvider(name = "oddsSum")
    private Object[][] oddsSumProvider() {
        return new Object[][]{
            {0, 0, 0},
            {7, 9, 16},
            {21, 30, 125}
        };
    }

    /**
     * Exercise 2.22 "Understanding of flatMap together with stream creating".
     */
    @Test(dataProvider = "flatMap")
    public void flatMap(final UnaryOperator<List<Integer>> operator, final List<Integer> expected) {
        final List<Integer> numbers = Arrays.asList(1, 2, 3, 4, 5);

        final List<Integer> actual = operator.apply(numbers);

        Assert.assertEquals(actual.size(), expected.size());
        actual.forEach(n -> Assert.assertTrue(expected.contains(n)));
    }

    /**
     * Data provider for exercise 2.22.
     * @return unary operator that change incoming list of integers and expected result list.
     */
    @DataProvider(name = "flatMap")
    private Object[][] flatMapProvider() {
        final UnaryOperator<List<Integer>> operator1 = list -> list.stream()
                .flatMap(n -> Stream.generate(() -> n).limit(n))
                .collect(Collectors.toList());

        final UnaryOperator<List<Integer>> operator2 = list -> list.stream()
            .flatMapToInt(n -> IntStream.rangeClosed(1, n))
            .boxed()
            .collect(Collectors.toList());

        final UnaryOperator<List<Integer>> operator3 = list -> list.stream()
            .flatMapToInt(n -> IntStream.iterate(n, val -> val + 1).limit(n))
            .boxed()
            .collect(Collectors.toList());

        final UnaryOperator<List<Integer>> operator4 = list -> list.stream()
            .flatMap(Stream::of)
            .collect(Collectors.toList());

        return new Object[][]{
            {operator1, Arrays.asList(1, 2, 2, 3, 3, 3, 4, 4, 4, 4, 5, 5, 5, 5, 5)},
            {operator2, Arrays.asList(1, 1, 2, 1, 2, 3, 1, 2, 3, 4, 1, 2, 3, 4, 5)},
            {operator3, Arrays.asList(1, 2, 3, 3, 4, 5, 4, 5, 6, 7, 5, 6, 7, 8, 9)},
            {operator4, Arrays.asList(1, 2, 3, 4, 5)}
        };
    }

    /**
     * Exercise 2.23 "The general number of employees".
     */
    @Test
    public void flattingObjectsFields() {
        final Employee william = new Employee("William", 20_000L);
        final Employee sophia = new Employee("Sophia", 10_000L);
        final Employee john = new Employee("John", 50_000L);
        final Department dep1 = new Department("dep-1", "111-1", Arrays.asList(william, sophia));
        final Department dep2 = new Department("dep-2", "222-1", Collections.singletonList(john));
        final List<Department> departments = Arrays.asList(dep1, dep2);

        final long actual = calcNumberOfEmployees(departments, 20_000L);

        Assert.assertEquals(actual, 1L);
    }

    /**
     * @param departments list of departments.
     * @param threshold minimum amount of salary to filter employees.
     * @return count of employees which working in departments with code starts with "111-"
     * and with salary more or equals to threshold.
     */
    private static long calcNumberOfEmployees(List<Department> departments, long threshold) {
        return departments.stream()
            .filter(department -> department.getCode().startsWith("111-"))
            .map(Department::getEmployees)
            .flatMap(Collection::stream)
            .distinct()
            .filter(employee -> employee.getSalary() >= threshold)
            .count();
    }

    /**
     * Exercise 2.24 "The total sum of canceled transactions".
     */
    @Test
    public void transactionSum() {
        final Transaction transaction1 = new Transaction("774cedda", State.CANCELED, 1000L, new Date());
        final Account account1 = new Account("1001", 0L, Collections.singletonList(transaction1));
        final Transaction transaction2 = new Transaction("337868a7", State.FINISHED, 8000L, new Date());
        final Transaction transaction3 = new Transaction("f8047f86", State.CANCELED, 10000L, new Date());
        final Account account2 = new Account("1002", 8000L, Arrays.asList(transaction2, transaction3));

        final long actual = calcSumOfCancelledTransOnNonEmptyAccounts(Arrays.asList(account1, account2));

        Assert.assertEquals(actual, 10_000L);
    }

    /**
     * @param accounts list of {@link Account}.
     * @return sum of cancelled transactions on non empty accounts.
     */
    public static long calcSumOfCancelledTransOnNonEmptyAccounts(List<Account> accounts) {
        return accounts.stream()
            .filter(account -> account.getBalance() > 0)
            .map(Account::getTransactions)
            .flatMap(Collection::stream)
            .filter(transaction -> transaction.getState() == State.CANCELED)
            .map(Transaction::getSum)
            .reduce(0L, (acc, sum) -> acc += sum);
    }

    /**
     * Class for example of flatMap usage.
     */
    @Data
    private static class Book {

        private final String name;
        private final int year;
        private final List<String> authors;
    }

    /**
     * Class for exercise 2.23.
     */
    @Data
    private static class Employee {

        private final String name;
        private final Long salary;
    }

    /**
     * Class for exercise 2.23.
     */
    @Data
    private static class Department {

        private final String name;
        private final String code;
        private final List<Employee> employees;
    }

    /**
     * Class for exercise 2.24.
     */
    @Data
    static class Transaction {
        private final String uuid;
        private final State state;
        private final Long sum;
        private final Date created;
    }

    /**
     * Enum for exercise 2.24.
     */
    enum State {
        CANCELED, FINISHED
    }

    /**
     * Class for exercise 2.24.
     */
    @Data
    private static class Account {
        private final String number;
        private final Long balance;
        private final List<Transaction> transactions;
    }
}
