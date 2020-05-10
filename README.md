
# About #

This project is my studying notes that I wrote while learning **functional programming in Java**.

## Notes ##

My notes about Java 8 new classes [here][Docs].
List of notes:
- [Optional][Optional] (RU)

[Docs]: /docs
[Optional]: /docs/Optional.md

## Stepik.org course "[Java. Functional programming][Stepik]"

Answers for hard exercises of the course are [here][StepikExercises].  
Grouped by theory lessons:
- [1.2 Lambda expressions and method references][Course-1.2];
- [1.3 Functions are objects][Course-1.3];
- [1.4 Introduction to streams][Course-1.4];
- [1.5 Learn more about map, reduce and forEach][Course-1.5];
- [1.6 Collectors][Course-1.6];
- [1.7 Parallel streams][Course-1.7];
- [1.8 Returning functions and currying][Course-1.8];
- [1.9 Monads and related things in Java 8][Course-1.9].

[StepikExercises]: /src/test/java/dev/boiarshinov/stepik
[Course-1.2]: /src/test/java/dev/boiarshinov/stepik/LambdaExpressionsTest.java
[Course-1.3]: /src/test/java/dev/boiarshinov/stepik/FunctionsAreObjectsTest.java
[Course-1.4]: /src/test/java/dev/boiarshinov/stepik/IntroductionToStreamsTest.java
[Course-1.5]: /src/test/java/dev/boiarshinov/stepik/MapReduceAndForEachTest.java
[Course-1.6]: /src/test/java/dev/boiarshinov/stepik/IntroductionToCollectorsTest.java
[Course-1.7]: /src/test/java/dev/boiarshinov/stepik/ParallelStreamsTest.java
[Course-1.8]: /src/test/java/dev/boiarshinov/stepik/ReturningFunctionsTest.java
[Course-1.9]: /src/test/java/dev/boiarshinov/stepik/MonadsAndRelatedTest.java

## Book "Richard Warburton - Java 8 Lambdas"

Exercise answers from the book are [here][BookExercises].  
Grouped by chapters:
- [Chapter 2. Lambdas][BookChapter2];
- [Chapter 3. Streams][BookChapter3];
- [Chapter 4. Libraries][BookChapter4];
- Chapter 5. Collections and Collectors;
- Chapter 6. Data parallelism.

Chapters 1, 7-10 have no exercises.

[BookExercises]: /src/test/java/dev/boiarshinov/book
[BookChapter2]: /src/test/java/dev/boiarshinov/book/Chapter2Lambdas.java
[BookChapter3]: /src/test/java/dev/boiarshinov/book/Chapter3Streams.java
[BookChapter4]: /src/test/java/dev/boiarshinov/book/Chapter4Libraries.java

## Test for Java 8 API ##

Also [there][TestClasses] is tests for new Java 8 classes.
List of tests:
- [Optional][TestOptional]
- [Predicate][TestPredicate]
- [Function][TestFunction]

[TestClasses]: /src/test/java/dev/boiarshinov/api
[TestOptional]: /src/test/java/dev/boiarshinov/api/util/OptionalTest.java
[TestPredicate]: /src/test/java/dev/boiarshinov/api/util/function/PredicateTest.java
[TestFunction]: /src/test/java/dev/boiarshinov/api/util/function/FunctionTest.java

## Custom functional API ##

Some standard java functional API improvements:
- [ImprovedBooleanSupplier][ImprovedBooleanSupplier] - standard BooleanSupplier with added logical 
functions (OR, AND, NEGATE). 

[ImprovedBooleanSupplier]: /src/test/java/dev/boiarshinov/util/ImprovedBooleanSupplier.java


# Additional info # 

Here is a list of additional materials to learn about Functional Programming in Java.

## Courses ##

- [x] Stepik.org - [Java. Functional programming][Stepik]

[Stepik]: https://stepik.org/course/1595/


## Books ##

- [x] Richard Warburton - Java 8 Lambdas. Functional programming for the masses: [En][Warburton-en], [Ru][Warburton-ru]. [GitHub][Warburton-git]  
- [ ] Pierre-Yves Saumont - Functional programming in Java: [En][Saumont]

[Warburton-en]: https://www.oreilly.com/library/view/java-8-lambdas/9781449370831/
[Warburton-ru]: https://dmkpress.com/catalog/computer/programming/functional/978-5-94074-919-6/
[Warburton-git]: https://github.com/RichardWarburton/java-8-lambdas-exercises
[Saumont]: https://www.manning.com/books/functional-programming-in-java


## Videos ##

English:
- [ ] null

Russian:  
- [x] Тагир Валеев - Stream API: рекомендации лучших собаководов (2016). [YouTube][Tagir-1]
- [ ] Тагир Валеев - Странности Stream API (2016). [YouTube][Tagir-2]
- [ ] Тагир Валеев - Причуды Stream API (2016). [YouTube][Tagir-3]
- [x] Сергей Куксенко - Stream API, часть 1. [YouTube][Kuksenko-1]
- [ ] Сергей Куксенко - Stream API, часть 2. [YouTube][Kuksenko-2]
- [ ] Андрей Родионов - От Java Threads к лямбдам (2014). [Youtube][Rodionov]

[Tagir-1]: https://www.youtube.com/watch?v=vxikpWnnnCU
[Tagir-2]: https://www.youtube.com/watch?v=TPHMyVyktsw&t=8s
[Tagir-3]: https://www.youtube.com/watch?v=1_Zj3gS_a3E 
[Kuksenko-1]: https://www.youtube.com/watch?v=O8oN4KSZEXE
[Kuksenko-2]: https://www.youtube.com/watch?v=i0Jr2l3jrDA
[Rodionov]: https://www.youtube.com/watch?v=W82D9eUn6q8


## Links ##

English:
- [ ] Java 8 tutorial. [HowToDoInJava][HowToDoInJava]
- [x] Java 8 Optional use-cases. [DZone][Optional]
- [x] What's wrong in Java 8, Part I: Currying vs Closures. [DZone][Wrong-1]. [Перевод][Wrong-1-Ru]
- [x] What's wrong in Java 8, Part II: Functions & Primitives. [DZone][Wrong-2]. [Перевод][Wrong-2-Ru]
- [x] What's wrong in Java 8, Part III: Streams and Parallel Streams. [DZone][Wrong-3]. [Перевод][Wrong-3-Ru]
- [x] What's wrong in Java 8, Part IV: Monads. [DZone][Wrong-4]. [Перевод][Wrong-4-Ru]
- [x] What's wrong in Java 8, Part V: Tuples. [DZone][Wrong-5]. [Перевод][Wrong-5-Ru]
- [ ] What's wrong in Java 8, Part VI: Strictness. [DZone][Wrong-6]
- [ ] What's wrong in Java 8, Part VII: Streams again. [DZone][Wrong-7]

Also:
* Look at the full list of Pierre-Yves Saumont articles: [DZone][Saumont]
* Look at the list of Tomasz Nurkiewicz articles (part of them is about Java 8): [DZone][Nurkiewicz]

Russian:
- [ ] Полное руководство с анимированными операциями. [Annimon][Animated]
- [ ] Шпаргалка Java программиста. Java Stream API (2015). [Хабр][Habr-Vedenin]
- [ ] Stream API: универсальная промежуточная операция (2016). [Хабр][Habr-Tagir]

[HowToDoInJava]: https://howtodoinjava.com/java-8-tutorial/
[Optional]: https://dzone.com/articles/java-8-optional-use-cases
[Wrong-1]: https://dzone.com/articles/whats-wrong-java-8-currying-vs
[Wrong-1-Ru]: docs/translations/Wrong_in_Java_8_Part_1.md
[Wrong-2]: https://dzone.com/articles/whats-wrong-java-8-part-ii
[Wrong-2-Ru]: docs/translations/Wrong_in_Java_8_Part_2.md
[Wrong-3]: https://dzone.com/articles/whats-wrong-java-8-part-iii
[Wrong-3-Ru]: docs/translations/Wrong_in_Java_8_Part_3.md
[Wrong-4]: https://dzone.com/articles/whats-wrong-java-8-part-iv
[Wrong-4-Ru]: docs/translations/Wrong_in_Java_8_Part_4.md
[Wrong-5]: https://dzone.com/articles/whats-wrong-java-8-part-v
[Wrong-5-Ru]: docs/translations/Wrong_in_Java_8_Part_5.md
[Wrong-6]: https://dzone.com/articles/whats-wrong-java-8-part-vi
[Wrong-7]: https://dzone.com/articles/whats-wrong-java-8-part-vii
[Saumont]: https://dzone.com/users/38615/ps24890.html
[Nurkiewicz]: https://dzone.com/users/370400/nurkiewicz.html
[Animated]: https://annimon.com/article/2778
[Habr-1]: https://habr.com/ru/company/luxoft/blog/270383/
[Habr-Tagir]: https://habr.com/ru/post/262139/
[Habr-Vedenin]: https://habr.com/ru/company/luxoft/blog/270383/


# Technologies #
Additional libraries used in this project:
- [Lombok][Lombok]
- [TestNG][TestNG]

[Lombok]: https://projectlombok.org/
[TestNG]: https://testng.org/
