# Что не так в Java 8, часть VII: Снова про стримы #

Перевод статьи [What's wrong in Java 8, Part VII: Streams again][OriginalLink].

В [третьей статье серии][Wrong-3-Ru] я писал, что существует множество проблем с параллельными стримами.
Главная проблема заключается в автоматической параллелизации - самой рекламируемой фичей стримов.
Дело было в том, что сколь бы эффектными бы ни были параллельные стримы, возможности использования их в
реальных бизнес-приложениях очень скудны.
В результате того, что много усилий разработчиков языка было брошено на параллелизацию стримов, сами стримы
оказались обделены многими возможностями функционального программирования.

## Использование стримов в функциональном программировании ##

Существует множество вариантов использования стримов, один из которых - замена циклов `for` и `while` на стримы, 
поддерживающие ленивые (отложенные) вычисления.  
Но зачем нам нужно менять цикл `for` на что-то еще?

Один из важных принципов Java: из закрытой области видимости можно получить доступ к членам, 
не входящим в эту область.
Например метод имеет доступ ко всем остальным методам класса и ко всем его переменным.
Точно так же в цикле `for` можно получить доступ ко всем членам класса и ко всем открытым статическим членам
любых других классов.

В примере ниже
```java
for(int i = 0; i < 10; i++) {
    System.out.println(i);
}
```
мы вызываем в цикле метод `println`, который объявлен в другом классе.
В следующем примере
```java
List<Integer> list = new ArrayList<>();
for(int i = 0; i < 10; i++) {
    list.add(i);
}
list.forEach(System.out::println);
```
переменная `list` объявлена снаружи цикла и используется внутри.
В определенной степени этот код хуже, потому что список изменяется внутри цикла.

Проблема с доступом в закрытой области видимости заключается в том, что цикл не может быть переиспользован.
Функциональное программирование предоставляет решение этой проблемы.

Предыдущий пример, переписанный в функциональном стиле будет выглядеть:
```java
IntStream.range(0, 10).forEach(System.out::println);
```
Здесь ресурс из внешней области видимости был передан в качестве аргумента.
Такой подход намного чище, уменьшает цикломатическую сложность кода, делает его менее склонным к ошибкам
и такой код гораздо проще поддерживать.
Таким образом стримы - это более абстрактный эквивалент цикла `for`.

У цикла `for each` также существует функциональный эквивалент.
Для примера, преобразуем следующий код:
```java
List<Integer> integerList = Arrays.asList(1, 2, 3, 4, 5);
List<String> stringList = new ArrayList<>();
for (Integer value : integerList) {
    stringList.add("Value = " + value);
}
```
с помощью функциональной парадигмы:
```java
List<Integer> integerList = Arrays.asList(1, 2, 3, 4, 5);
integerList.map(x -> "Value = " + x);
```
Несмотря на то, что все приведенные примеры примитивны и бесполезны, они показывают как функциональное 
программирование с использованием стримов может убрать множество управляющих конструкций из кода.

Конечно, существует множество других представлений циклов, к примеру:
```java
for (int i = 1; i < limit; i += 2) {
    //...
}
```
Этот цикл будет итерировать нечетные цифры до достижения указанного значения `limit`. 
А следующий пример бесконечно итерирует четные числа:
```java
for (int i = 0;; i += 2) {
    //...
}
```
Стримы можно использовать в качестве замены этих конструкций, сделав код намного чище.
К сожалению, в Java 8 отсутствуют две важных механики для этого: указание шага 
и ограничение стрима с помощью предиката.

## Ограничение стрима с помощью предиката ##

Давайте посмотрим как можно переписать с использованием стримов следующий фрагмент кода:
```java
public static boolean isPrime(final int n) {
    if (n < 2) return true;
    for(int i = 2; i * i <= n; i++) {
        if(n % i == 0) {
            return false;
        }
    }
    return true;
}
```
Этот (очень неэффективный) код вычисляет, является ли переданное число простым.
Его можно переписать в функционально стиле:
```java
public static IntPredicate isPrime = n -> n < 4 || !(n % 2 == 0 ||
    IntStream.range(2, (int) Math.sqrt(n) + 1)
        .filter(x -> x % 2 != 0 && n % x == 0)
        .count() != 0);
```
Эта реализация дает тот же результат.
Ее можно немного оптимизировать:
```java
public static IntPredicate isPrime = n -> n < 4 || !(n % 2 == 0 ||
    IntStream.range(2, (int) Math.sqrt(n) + 1)
        .filter(x -> x % 2 != 0 && n % x == 0)
        .findAny()
        .isPresent());
``` 
Замена `.count()` на `findAny().isPresent()` позволяет тому же коду выполниться за 663 мс вместо 2_760 мс при 
поиске всех простых чисел от 1 до 1_000_000. 
Так происходит, потому что `findAny()` прерывает вычисление стрима как только находится значение, 
удовлетворяющее предшествующему условию. Тем временем `count()` вычисляет все элементы стрима.
Еще более быстрым будет решение с использованием `anyMatch()`:
```java
public static IntPredicate isPrime = n -> n < 4 || !(n % 2 == 0 ||
    IntStream.range(2, (int) Math.sqrt(n) + 1)
        .filter(x -> x % 2 != 0 && n % x == 0)
        .anyMatch(x -> true);
```
Такой вариант на 10% быстрее, но `findAny().isPresent()` элегантнее, потому что указывает, что стрим содержит
хотя бы один элемент.

Заметьте, что мы испытываем сложности с ограничением стрима. В примере с использованием цикла ограничение 
было записано в условии
```java
i * i <= n
```  
Во многих функциональных языках существует возможность ограничить стрим одним из следующих способов:
```java
public static IntPredicate isPrime4 = n -> n < 4 || !(n % 2 == 0 ||
    IntStream.range(2, n)
        .takeWhile(x -> x * x <= n)
        .filter(x -> x % 2 != 0 && n % x == 0)
        .findAny()
        .isPresent());
```
Но в Java 8 нет ни метода `takeWhile()`, ни эквивалентного ему.
Нам приходится использовать этот уродливый трюк с корнем из _n_.

Еще одним возможным решением для генерации стрима является:
```java
IntStream.iterate(2, x -> x + 1)
```
но это сработает, только если мы ограничим количество элементов стрима с помощью метода `limit`, 
который не принимает предикатов.
Метод `filter()` не поможет, потому что он только бракует элементы, но не прерывает их генерации.
Чтобы убедиться в этом, попробуйте запустить следующий код:
```java
IntStream.iterate(2, x -> x + 1)
    .filter(x -> x < 10)
    .forEach(System.out::println);
```
Этот код выведет значения от 1 до 9, ненадолго задумается, а потом вывалит в консоль
```java
-2147483648
-2147483647
-2147483646
-2147483645
-2147483644
-2147483643
-2147483642
-2147483641
```
Так происходит, потому что стрим бесконечен, и функция, продуцирующая элементы, достигает верхней 
границы чисел `int` и начинает генерировать отрицательные значения, которые удовлетворяют предикату.
Следующее условие 
```java
IntStream.iterate(2, x -> x + 1)
    .filter(x -> x >= 0 && x < 10)
    .forEach(System.out::println);
```
не решает проблему. 
(Можно использовать метод `Math.addExact()`, но оно выбросит исключение при переполнении, что, конечно, 
лучше бесконечной генерации элементов, но использовании исключений для описания желаемого поведения программы
не является хорошей практикой)

Нам действительно не хватает тут метода `takeWhile()`.
Мы можем симулировать его с помощью следующего кода:
```java
// Это немного модифицированный код класса IntStream 
public static IntStream iterate(final int seed, final IntUnaryOperator f, IntPredicate p) { // изменения тут
    Objects.requireNonNull(f);
    final PrimitiveIterator.OfInt iterator = new PrimitiveIterator.OfInt() {
        int t = seed;

        @Override
        public boolean hasNext() {
            return p.test(t); // и тут
        }

        @Override
        public int nextInt() {
            int v = t;
            t = f.applyAsInt(t);
            return v;
        }
    };
    return StreamSupport.intStream(Spliterators.spliteratorUnknownSize(
        iterator,
        Spliterator.ORDERED | Spliterator.IMMUTABLE | Spliterator.NONNULL), false);
}
```
С помощью измененного класса `IntStream` наш пример можно записать следующим образом:
```java
public static IntPredicate isPrime = n -> n < 4 || !(n % 2 == 0 ||
    iterate(2, x -> x + 1, x-> x * x <= n)
        .filter(x -> x % 2 != 0 && n % x == 0)
        .findAny()
        .isPresent());
```
Очевидно, что такой способ генерации элементов стрима должен был быть предусмотрен в Stream API. 
Или метод `takeWhile()`.
Однако это возможно могло вызвать проблемы с параллельными стримами, и скорее всего поэтому такие возможности
не были добавлены в Java.
Если причина действительно в этом, то инженеры Java сделали неправильный выбор.

# Указание шага #

Итеративная версия метода, вычисляющего простые числа, может быть оптимизирована следующим образом:
```java
public static boolean isPrime(final int n) {
    if (n < 4) return true;
    if (n % 2 == 0) return false;
    for(int i = 3; i * i <= n; i += 2) {
        if(n % i==0) {
            return false;
        }
    }
    return true;
}
```
Оптимизация заключается в том, что мы увеличили шаг итерации до двух, чтобы осуществлять поиск только среди
нечетных чисел.
Можно ли сделать такое с помощью функционального подхода?
Мы не можем записать:
```java
IntStream.iterate(2, x -> x + 2)
```
У нас отсутствует возможность ограничить стрим с помощью предиката, и мы не знаем длину стрима 
и потому не можем использовать `limit()`.

Мы могли бы использовать написанную выше версию метода `iterate()` с дополнительным аргументом предикатом:
```java
public static IntPredicate isPrime = n -> n < 4 || !(n % 2 == 0 ||
    iterate(3, x -> x + 2, x-> x * x <= n)
        .filter(x -> n % x == 0)
        .findAny()
        .isPresent());
```
Другое решение с использованием методов из стандартного API:
```java
public static IntPredicate isPrime = n -> n < 4 || !(n % 2 == 0 ||
  IntStream.range(1, (int) Math.sqrt(n) / 2 + 1)
      .map(x -> x * 2 + 1)
      .filter(x -> n % x == 0)
      .findAny()
      .isPresent());
``` 
Эта версия создает стрим чисел с единичным шагом, а за тем отображает его с помощью функции `x -> x * 2 + 1`.

Мы можем создать стрим с шагом 1 и затем фильтровать все четные значения:
```java
public static IntPredicate isPrime = n -> n < 4 || !(n % 2 == 0 ||
  IntStream.range(2, (int) Math.sqrt(n) + 1)
      .filter(x -> x % 2 != 0 && n % x == 0)
      .findAny()
      .isPresent());
``` 
Или мы можем использовать модифицированную (опять) версию класса `RangeIntSpliterator`:
```java
static final class RangeIntSpliterator implements Spliterator.OfInt {

    private int from;
    private final int upTo;
    private final int step;
    private int last;

    RangeIntSpliterator(int from, int upTo, int step, boolean closed) { // изменено
        this(from, upTo, step, closed ? 1 : 0); // изменено
    }

    private RangeIntSpliterator(int from, int upTo, int step, int last) { // изменено
        this.from = from;
        this.upTo = upTo;
        this.step = Math.max(1, step); // добавлено
        this.last = last;
    }

    @Override
    public boolean tryAdvance(IntConsumer consumer) {
        Objects.requireNonNull(consumer);

        final int i = from;
        if (upTo - i >= step) {
            from += step; // изменено
            consumer.accept(i);
            return true;
        } else if (last > 0) {
            last = 0;
            consumer.accept(i);
            return true;
        }
        return false;
    }

    @Override
    public void forEachRemaining(IntConsumer consumer) {
        Objects.requireNonNull(consumer);

        int i = from;
        final int hUpTo = upTo;
        int hLast = last;
        from = upTo;
        last = 0;
        while (i < hUpTo) {
            consumer.accept(i);
            i += step; // изменено
        }
        if (hLast > 0) {
            // последний элемент закрытого интервала
            consumer.accept(i);
        }
    }

    @Override
    public long estimateSize() {
        // убеждаемся, что интервалы с размером, превышающим Integer.MAX_VALUE будут возвращать корректный размер
        return (((long) upTo) - from + last) / step;
    }

    @Override
    public int characteristics() {
        return Spliterator.ORDERED | Spliterator.SIZED | Spliterator.SUBSIZED |
            Spliterator.IMMUTABLE | Spliterator.NONNULL |
            Spliterator.DISTINCT | Spliterator.SORTED;
    }

    @Override
    public Comparator<? super Integer> getComparator() {
        return null;
    }

    @Override
    public Spliterator.OfInt trySplit() {
        long size = estimateSize();
        return size <= 1
            ? null
            // левое разбиение всегда имеет полуоткрытый интервал
            : new RangeIntSpliterator(from, from = from + splitPoint(size), step, 0);
    }

    private static final int BALANCED_SPLIT_THRESHOLD = 1 << 24;

    private static final int RIGHT_BALANCED_SPLIT_RATIO = 1 << 3;

    private int splitPoint(long size) {
        int d = (size < BALANCED_SPLIT_THRESHOLD) ? 2 : RIGHT_BALANCED_SPLIT_RATIO;
        return (int) (size / d);
    }
}
```
Этот класс можно использовать совместно со следующим методом (еще один модифицированный метод класса `IntStream`):
```java
public static IntStream rangeStep(int startInclusive, int endExclusive, int step) { // изменено
    if (startInclusive >= endExclusive) {
        return IntStream.empty();
    } else {
    return StreamSupport.intStream(
        new RangeIntSpliterator(startInclusive, endExclusive, step, false), false); // изменено
    }
}
```
В результате нашу задачу можно переписать следующим образом:
```java
public static IntPredicate isPrime = n -> n < 4 || !(n % 2 == 0 ||
    rangeStep(3, (int) Math.sqrt(n) + 2, 2)
         .filter(x -> n % x == 0)
         .findAny()
         .isPresent());
```

## Бенчмарк ##

Функциональная версия в любом случае медленнее, чем итеративная.
Ниже приведены замеры времени поиска 78_499 простых чисел в интервале от 1 до 1_000_000.
Все тесты были запущены несколько раз, чтобы прогреть компилятор.
(Это очень важно, если компилятор не прогреть, можно получить странные результаты.)
```java
Non optimized iterative: 78499 primes in 307 ms.
Optimized iterative: 78499 primes in 160 ms.
Functional with step 1 and mapping with x -> x * 2 + 1: 78499 primes in 763 ms.
Functional with step 2 limited by a predicate: 78499 primes in 746 ms.
Functional idem, tested with anyMatch: 78499 primes in 653 ms.
Functional with step 1 and filtering even values: 78499 primes in 1186 ms.
Functional with iterate, step 2 and limit with a predicate: 78499 primes in 760 ms.
```

## Вывод ##

Можно сделать вывод, что итеративная версия всегда работает быстрее версии с использованием стримов, а 
все функциональные версии вычисляются за примерно одинаковое время.

Преимуществом функциональной версии является низкая цикломатическая сложность и тот факт, что код лучше
отражает намерения разработчика, а не описывает как этих намерений нужно достигнуть (так как реализация 
сокрыта в недрах используемого API).

Но стримам не хватает (кроме большей производительности) выразительных средств для описания намерений
разработчика.
Другими словами, для примера с простыми числами, мы должны иметь возможность записать:
```java
public static IntPredicate isPrime = n -> n < 4 || (n % 2 != 0 &&
    IntStream.iterate(3, x -> x + 2, x-> x * x <= n)
        .filter(x -> n % x == 0)
        .isEmpty());
```
или
```java
public static IntPredicate isPrime2 = n -> n < 4 || (n % 2 != 0 &&
    IntStream.range(3, (int) Math.sqrt(n) + 2, 2)
        .filter(x -> n % x == 0)
        .isEmpty());
```
Существует еще много методов, которых не хватает в Stream API. 
Один из них - `zip` (и его противоположность - `unzip`), который принимает два стрима и возвращает
стрим кортежей (кортежи тоже отсутствуют в Java 8!).


[OriginalLink]: https://dzone.com/articles/whats-wrong-java-8-part-vii
[Wrong-3-Ru]: Wrong_in_Java_8_Part_3.md