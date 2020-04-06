package dev.boiarshinov.book;

import org.testng.annotations.Test;

import javax.swing.*;
import javax.swing.text.DateFormatter;
import java.util.Random;
import java.util.function.Function;
import java.util.function.Predicate;

/**
 * Chapter 2 "Lambda-expressions".
 */
public class Chapter2Lambdas {

    /**
     * Exercise 1.
     */
    @Test
    public void functions() {
        //Task A.
        // T -> Function -> R

        //Task B.
        //For trigonometrical, convert functions etc.:
        Function<Double, Double> sin = Math::sin;
        Function<Double, Double> radToDegree = x -> x * 180 / Math.PI;

        //Task C.
        //First example can be a Long to Long Function, two others not.
        final Function<Long, Long> function = x -> x + 1;
//        final Function<Long, Long> function = (x, y) -> x + 1;
//        final Function<Long, Long> function = x -> x == 1;
    }

    /**
     * Exercise 2.
     */
    @Test
    public void threadLocal() {
        final ThreadLocal<DateFormatter> localDateFormatter = ThreadLocal.withInitial(DateFormatter::new);
    }

    /**
     * Exercise 3.
     */
    @Test
    public void lambdaTypeDetermination() {
        //variant a - true
        final Runnable helloWorld = () -> System.out.println("HelloWorld");

        //variant b - true
        JButton button = new JButton();
        button.addActionListener(event -> System.out.println(event.getActionCommand()));

        //variant c - false
        //final boolean isOk = check(x -> x > 5);
    }

    private static boolean check(Predicate<Integer> predicate) {
        return new Random().nextBoolean();
    }

    private static boolean check(IntPred predicate) {
        return new Random().nextBoolean();
    }

    @FunctionalInterface
    interface IntPred {
        boolean test(Integer value);
    }
}
