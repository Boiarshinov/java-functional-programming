package dev.boiarshinov.stepik;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import lombok.Data;
import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

/**
 * These tests are related to topic 1.6 - Collectors.
 * Part of these tests are solutions of exercises 2.25-2.29.
 */
public class IntroductionToCollectorsTest {

    /**
     * Exercise 2.26 "Collectors in practice: the product of squares".
     */
    @Test(dataProvider = "productionOfSquares")
    public void productOfSquares(List<Integer> numbers, Integer expected) {
        final Integer actual = numbers.stream().collect(Collectors.reducing(1, (acc, n) -> acc *= n * n));

        Assert.assertEquals(actual, expected);
    }

    /**
     * Data provider for exercise 2.26.
     * @return list of numbers and expected result.
     */
    @DataProvider
    public static Object[][] productionOfSquares() {
        return new Object[][]{
            {Arrays.asList(1, 2, 3, 4, 5), 14400},
            {Arrays.asList(0, 1, 2), 0},
            {Arrays.asList(1, 2), 4},
        };
    }

    /**
     * Exercise 2.27 "Collectors in practice: partitioning words into groups".
     */
    @Test(dataProvider = "palindromeProvider")
    public void palindromes(List<String> words, Map<Boolean, List<String>> expected) {
        final Map<Boolean, List<String>> actual = words.stream()
            .collect(Collectors.partitioningBy(word -> new StringBuilder(word).reverse().toString().equals(word)));

        Assert.assertEquals(actual, expected);
    }

    /**
     * Data provider for exercise 2.27.
     * @return list of words and expected result.
     */
    @DataProvider
    public static Object[][] palindromeProvider() {
        final List<String> input1 = Arrays.asList("aaaa", "aaa", "a", "aa");
        final List<String> input2 = Arrays.asList("level", "bbaa", "ac");

        final HashMap<Boolean, List<String>> expected1 = new HashMap<>();
        expected1.put(false, new ArrayList<String>());
        expected1.put(true, Arrays.asList("aaaa", "aaa", "a", "aa"));

        final HashMap<Boolean, List<String>> expected2 = new HashMap<>();
        expected2.put(false, Arrays.asList("bbaa", "ac"));
        expected2.put(true, Stream.of("level").collect(Collectors.toList()));

        return new Object[][]{
            {input1, expected1},
            {input2, expected2}
        };
    }

    /**
     * Exercise 2.28 "Almost like a SQL: the total sum of transactions by each account".
     */
    @Test
    public void sumOfTransactions() {
        final Account acc1 = new Account("01", 100L);
        final Account acc2 = new Account("02", 500L);

        final Transaction transaction1 = new Transaction("0001", 10L, acc1);
        final Transaction transaction2 = new Transaction("0002", 100L, acc2);
        final Transaction transaction3 = new Transaction("0003", 200L, acc2);

        final List<Transaction> transactions = Stream.of(transaction1, transaction2, transaction3)
            .collect(Collectors.toList());

        final HashMap<String, Long> expectedTotals = new HashMap<>();
        expectedTotals.put("01", 10L);
        expectedTotals.put("02", 300L);

        final Map<String, Long> actualTotals = transactions.stream()
            .collect(Collectors.groupingBy(
                transaction -> transaction.getAccount().getNumber(),
                Collectors.reducing(
                    0L,
                    Transaction::getSum,
                    (acc, balance) -> acc += balance)
            ));

        //Better solution from Stepik.
        final Map<String, Long> actualTotals2 = transactions.stream()
            .collect(Collectors.groupingBy(
                transaction -> transaction.getAccount().getNumber(),
                Collectors.summingLong(Transaction::getSum))
            );

        Assert.assertEquals(actualTotals, expectedTotals);
        Assert.assertEquals(actualTotals2, expectedTotals);
    }

    /**
     * Exercise 2.29 "Almost like a SQL: click count".
     */
    @Test
    public void clickCount() {
        final LogEntry entry1 = new LogEntry(new Date(), "John Doe", "/cats/info");
        final LogEntry entry2 = new LogEntry(new Date(), "Judy Doe", "/cats/info");
        final LogEntry entry3 = new LogEntry(new Date(), "John Doe", "/dogs/info");

        final List<LogEntry> logs = Stream.of(entry1, entry2, entry3).collect(Collectors.toList());

        final HashMap<String, Long> expectedClickCount = new HashMap<>();
        expectedClickCount.put("/cats/info", 2L);
        expectedClickCount.put("/dogs/info", 1L);

        final Map<String, Long> actualClickCount = logs.stream()
            .collect(Collectors.groupingBy(LogEntry::getUrl, Collectors.counting()));

        Assert.assertEquals(actualClickCount, expectedClickCount);
    }

    /**
     * Class for exercise 2.28.
     */
    @Data
    private static class Account {
        private final String number;
        private final Long balance;
    }

    /**
     * Class for exercise 2.28.
     */
    @Data
    private static class Transaction {
        private final String uuid;
        private final Long sum;
        private final Account account;
    }

    /**
     * Class for exercise 2.29.
     */
    @Data
    private static class LogEntry {
        private final Date created;
        private final String login;
        private final String url;
    }
}
