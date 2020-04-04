package dev.boiarshinov.stepik;

import lombok.Data;
import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * These tests are related to topic 1.9 - Monads and related things in Java 8.
 * Part of these tests are solutions of exercises 2.36-2.39.
 */
public class MonadsAndRelatedTest {

    /**
     * Exercise 2.38 "Optional in action".
     */
    @Test(dataProvider = "findUserByLogin")
    public void findUserByLogin(final String login,
                                final Set<User> users,
                                final boolean isExist,
                                final boolean isPositiveBalance)
    {
        final Optional<User> specificUser = findUserByLogin(login, users);
        final Optional<Long> balance = getBalanceIfNotEmpty(login, users);

        Assert.assertEquals(specificUser.isPresent(), isExist);
        specificUser.ifPresent(user -> {
            Assert.assertEquals(user.login, login);
            Assert.assertEquals(balance.isPresent(), isPositiveBalance);
        });
    }

    private Optional<User> findUserByLogin(final String login, final Set<User> users) {
        return users.stream()
            .filter(user -> user.getLogin().equals(login))
            .findAny();
    }

    private Optional<Long> getBalanceIfNotEmpty(final String userLogin, final Set<User> users) {
        return this.findUserByLogin(userLogin, users)
            .map(User::getAccount)
            .map(Account::getBalance)
            .filter(balance -> balance > 0);
    }

    /**
     * Data provider for exercise 2.38.
     * @return login, set of users and boolean value - is set contains user with such login.
     */
    @DataProvider
    private Object[][] findUserByLogin() {
        final long positiveBalance = Math.abs(new Random().nextLong()) + 1;

        final User johnDoe = new User("John Doe", new Account(UUID.randomUUID(), positiveBalance));
        final User judyDoe = new User("Judy Doe", new Account(UUID.randomUUID(), 0));
        final User oliverTwist = new User("Oliver Twist", null);

        final Set<User> users = Stream.of(johnDoe, judyDoe, oliverTwist).collect(Collectors.toSet());

        return new Object[][] {
            {"John Doe", users, true, true},
            {"James Hatfield", users, false, false},
            {"Judy Doe", users, true, false},
            {"Oliver Twist", users, true, false}
        };
    }

    /**
     * Class for exercise 2.38.
     */
    @Data
    public static class Account {
        private final UUID guid;
        private final long balance;
    }

    /**
     * Class for exercise 2.38.
     */
    @Data
    public static class User {
        private final String login;
        private final Account account;
    }
}
