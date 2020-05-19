# Что не так в Java 8, часть VI: Строгость #

Перевод статьи [What's wrong in Java 8, Part VI: Strictness][OriginalLink].

_Примечание переводчика_ - первая половина статьи тяжело воспринимается и с трудом приходит понимание
посыла автора, но во второй половине все становится на свои места, и задумка автора становится ясна.

В [предыдущей статье][Wrong-5-Ru] утверждалось, что не существует функций нескольких аргументов.
Другими словами арность функций всегда равна единице.
С этим, конечно, можно поспорить.
Также утверждалось, что написание функций нескольких аргументов - это всего лишь синтаксический сахар
для:
- функций с одним аргументом-кортежем;
- функций, возвращающих функции.
 
Если быть точным, то функция с арностью _n_ является одним из двух:
- функцией одного аргумента, который является кортежем<sub>n</sub>;
- функцией одного аргумента, возвращающей функцию с арностью _n - 1_.

Во втором случае с помощью рекурсии мы можем убедиться, что функция с арностью _n_ может быть
преобразована в функцию с арностью _1_. 
И даже в нульарную функцию (т.е. в константную функцию), что соответствует определению функций.

Так в чем же разница между функциями кортежей и функциями, возвращающими функции?
Разница состоит в моменте времени вычисления аргументов.

В случае с функцией кортежа существует только один аргумент.
Это значит, что все элементы кортежа будут вычислены в один момент времени.

Для контраста, если мы представим функцию с арностью _n_ как функцию от функции арности _n - 1_
мы получим всего один аргумент.
Таким образом этот аргумент будет вычислен, но остальные могут быть еще не вычислены.
Другими словами:
```java
Integer f(Integer a, Integer b)
```
может быть представлено как функция приведения произведения _Integer * Integer_, что является
множеством всех пар _(a, b)_, к одному целому числу.
Другими словами тип аргумента функции f - _Integer * Integer_, а тип возвращаемого значения - _Integer_.

Или это можно представить как функцию приведения множества целых чисел к множеству функций приведения
целого числа к целому числу.

То есть то, что мы видели в прошлой статье:
```java
(a, b) -> ...
``` 
может быть заменено на 
```java
a -> b -> ...
```
Мы получили это с помощью каррирования (см. [первую статью][Wrong-1-Ru] цикла).

Заметьте, что несмотря на то, что первая функция может быть переписана как вторая, не означает, 
что они эквивалентны.
Другими словами
```
f(a, b)
```
не то же самое, что
```
(f a) b
```
даже несмотря на то, что иногда они могут возвращать одинаковый результат.

Так в чем же разница с точки зрения разработчика?
Разница заключается в моменте времени вычисления параметров.


## Java - это (почти) строгий язык ##

В Java выражения обычно вычисляются в момент их вызова.
Это означает, что некоторые элементы будут вычислены даже если они затем не используются.
В приведенном ниже примере
```java
Integer compute(Integer a, Integer b) {
    Integer result = ... // method implementation
    return result;
}
``` 
оба параметра _a_ и _b_ будут вычислены до того как будет выполнен метод.
В данном случае это не кажется большой проблемой, по сравнению со следующим примером:
```java
public static Integer param() {
    return 9;
}

public static Optional<Integer> compute(Integer a, Integer b) {
    if (b == 0) {
        return Optional.empty();
    } else {
        return Optional.ofNullable(a / b);
    }
}

public static void main(String... args) {
    compute(param(), 3).ifPresent(System.out::println);
}
```
В консоль будет выведено:
```java
3
```
Теперь изменим реализацию метода `param` на
```java
public static Integer param() {
    throw new RuntimeException();
}
```
и изменим метод `main` на
```java
compute(param(), 0).ifPresent(System.out::println);
```
Если мы попробуем выполнить программу, то получим:
```
Exception in thread "main" java.lang.RuntimeException
```
Несмотря на то, что код не использовал первый параметр (потому что программа вошла внутрь `if`), 
это параметр был вычислен, и потому было выброшено исключение.

В Java у нас нет выбора.
Аргументы метода всегда вычисляются до входа в метод.
В некоторых языках, особенно в функциональных, параметры вычисляются по требованию и это называется
"ленивым" выполнением.


## Бывает ли Java ленивой? ##

Как и все языки, Java иногда бывает ленивой.
Писать программы на полностью строгих языках было бы намного сложнее.

Мы все знаем такие ленивые операторы в Java, как `&&` и `||`.
В отличие от своих младших братьев `&` и `|`, которые являются строгими, параметры `&&` и `||` 
вычисляются при необходимости.
Но в Java они называются не ленивыми, а коротко-замкнутыми операторами.

Но сымитировать поведение операторов `&&` и `||` с помощью обычного метода в Java невозможно
из-за строгого вычисления аргументов.
Если вам не верится, то просто попробуйте.
Следующая простейшая попытка реализации не работает:
```java
public boolean or(boolean a, boolean b) {
    return a || b;
}
```

В Java существует несколько ленивых конструкций, например:
- тернарный оператор `? :`
- условный оператор `if ... else`
- цикл `for`
- цикл `while`
- `Stream`
- `Optional`

Стримы являются ленивыми.
Основная идея стримов заключается в том, что они не вычисляются до тех пор, пока не вызвана 
терминальная операция.
Подробнее об этом в одной из предыдущих статей цикла про [Стримы и параллельные стримы][Wrong-3-Ru].

`Optional` также является ленивым и вычисляется только когда на нем вызвана терминальная 
операция (хотя в Java 8 по отношению к `Optional` обычно не употребляют словосочетание
"терминальная операция").
В [еще одной статье][Wrong-4-Ru] данного цикла, посвященной монадам, упоминалось, что `Optional`,
как и `Stream`, является монадой.
`Optional` похож на `Stream` только с количеством элементов от нуля до одного.
Вот почему в некоторых функциональных библиотеках к Java и функциональных языках программирования
`Optional` (или эквивалентный класс с именем `Option` или `Maybe`) вместо `ifPresent` используется
название метода `forEach`.
Инженеры Oracle, вероятно, дали методу название `ifPresent`, потому что посчитали, что название 
`forEach` подходит только при наличии минимум двух элементов.

`if ... else` является ленивым, потому что будет вычислена только одна ветвь в зависимости от условия.
Поведение `if ... else` невозможно эмулировать с помощью метода:
```java
T ifThenElse(boolean condition, U if, V else)
``` 
Потому что все три аргумента (включая `if` И `else`) будут вычислены до входа в метод.

Возможно это не очевидно, но циклы в Java тоже являются ленивыми.
Поразмыслите: это
```java
for (int i = 0; i < 10; i++) {
    System.out.println(i);
}
```
эквивалентно этому
```java
IntStream.range(0, 10).forEach(System.out::println);
```
Для того чтобы стало предельно ясно, что цикл это лениво вычисляемая структура, 
давайте перепишем его следующим образом:
```java
for (int i = 0;; i++) {
    if (i < 10) System.out.println(i); else break;
}
```
Что эквивалентно этому:
```java
IntStream.range(0, Integer.MAX_VALUE).filter(x -> x < 10).forEach(System.out::println);
```
Основная разница заключается в том, что для цикла `for` вычисление последовательности `int`
происходит одновременно с применением операции к каждому `int`.
В случае стримов вычисление последовательности чисел и применение операции к каждому элементу
происходит раздельно.
Но в обоих случаях вычисления ленивые.
Возможность создания бесконечных стримов и циклов обусловлена как раз ленивостью их вычисления.
Без ленивых вычислений у нас было бы намного больше проблем.

Вопрос заключается в следующем: почему нельзя сделать все вычисления в Java ленивыми?
Если Java устремлена в сторону функциольного программирования, то нам нужен механизм, 
позволяющий выбирать между строгим и ленивым вычислением аргументов методов.

Примечание - возможно вы заметили, что `IntStream.range(1, Integer.MAX_VALUE)` не совсем является
эквивалентом цикла, потому что цикл бесконечен, а приведенный стрим - нет.
Существует возможность создать эквивалентный стрим, но придется использовать немного более сложную
конструкцию.


## Что дальше? ##

В следующей статье мы рассмотрим использование правильных типов и увидим, 
что примитивов следует избегать.
По крайней мере открытая часть нашего API не должна выставлять их.


_Примечание переводчика_ - автор сознательно опускает возможность осуществления ленивых вычислений
с помощью использования вместо аргументов метода эквивалентных им поставщиков.
В комментариях к статье он объясняет это тем, что использование `Supplier` и его подвидов для
примитивных значений изменяет сигнатуру метода и потому такой прием не является решением
поставленной задачи.


[OriginalLink]: https://dzone.com/articles/whats-wrong-java-8-part-vi
[Wrong-1-Ru]: Wrong_in_Java_8_Part_1.md
[Wrong-3-Ru]: Wrong_in_Java_8_Part_3.md
[Wrong-4-Ru]: Wrong_in_Java_8_Part_4.md
[Wrong-5-Ru]: Wrong_in_Java_8_Part_5.md