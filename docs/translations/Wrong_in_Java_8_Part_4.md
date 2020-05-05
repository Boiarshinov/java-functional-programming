# Что не так в Java 8, часть IV: Монады #

Перевод статьи [What's wrong in Java 8, Part IV: Monads][OriginalLink].

Монады - это центральная идея функционального программирования.
Но по большей части они проигнорированы в императивном программировании.
Программисты, пишущие на императивных языках, зачастую побаиваются монад.
Большинство Java-разработчиков даже не знают что такое монады, либо громко протестуют против
введения монад в Java.

Но, хотя об этом сильно не трубили, в Java 8 монады появились.

В своей [статье][OptionalPoint] Hugues Johnson спрашивает о классе `Optional`, появившемся в Java 8:
"В чем смысл?".

Пример, который он приводит, близок к следующему:
```java

String getString() {
  //method returning a String or null, such as get on a Map<String, String>
}
Option<String> optionalString = Optional.ofNullable(getString());
optionalString.ifPresent(System.out::toString);
```
Он спрашивает в чем смысл использования `Optional`, если то же самое можно сделать и без него:
```java
String getString() {
  //method returning a String or null, such as get on a Map<String, String>
}
String string = getString();
if (string != null) System.out.println(string);
```
Он упускает главное.
Объект `Optional` - это монада, и он должен использоваться как монада.

## Что такое монада? ##

Не углубляясь в теорию категорий, монада - это очень простой, но эффективный инструмент.
Монада - это совокупность трех вещей:
- параметризованный тип `M<T>`
- функция `unit`, преобразующая `T -> M<T>`
- операция `bind`, связывающая `M<T>` с функцией `T -> M<U>`, получающая в результате `M<U>`

Это может показаться излишне сложным, поэтому попробуем разобраться на примере класса `Optional`:
- параметризованный тип `Optional<T>`
- unit: `Optional.of()`
- bind: `Optional.flatMap()`

Перед тем как показать как использовать монады, хочу предупредить: я не знаю в чем именно
была задумка разработчиков Java 8, но они точно добавили `Optional` не просто так.
Если они добавили монады, то они сделали это с какой-то целью.
И возможно они умышленно не употребляли термина "монада", чтобы не пугать сторонников
императивного подхода.

## Как использовать монаду Optional ##

Монада `Optional` предназначена для того, чтобы объединять функции, которые могут возвращать 
ссылку на `null` и для которых отсутствие возвращаемого значения не является ошибкой. 
Самый простой пример - поиск по ключу в словаре (map). 
```java
Person person = personMap.get("Name");
process(person.getAddress().getCity())
```
Здесь мы ищем человека по его имени в словаре, затем получаем его адрес, а из адреса город. 
Город мы передаем в метод `process`.
Здесь может быть несколько вариантов развития событий:
- В словаре может не существовать человека с именем "Name" и переменной `person` будет присвоена
ссылка на `null`.
- Даже если в словаре существует значение для указанного ключа, то `null` может 
вернуть `person.getAddress`.
- Если `address` не `null`, то `city` может быть `null`.
- Ну и наконец все объекты могут существовать безо всяких ссылок на `null`.

(Мы не рассматриваем случай, когда `null` передается в качестве ключа в метод `get` словаря.)

В первых трех случаях мы получим NPE.
Для того чтобы приложение не упало с исключением необходимо сделать следующее:
```java
Person person = personMap.get("Name");
if (person != null) {
    Adress address = person.getAddress();
    if (address != null) {
        City city = address.getCity();
        if (city != null) {
            process(city)
        }
    }
}
```
Как мы можем использовать `Optional`, чтобы расчистить это загромождение?
Никак.
Для того чтобы использовать `Optional`, нужно сначала изменить классы `Map`, `Person` и `Address`
так, чтобы их методы возвращали `Optional`. 
Затем нужно изменить наш код следующим образом:
```java
Optional<Person> person = personMap.get("Name");
if (person.isPresent()) {
    Optional<Adress> address = person.getAddress();
    if (address.isPresent()) {
        Optiona<City> city = address.getCity();
        if (city.isPresent()) {
            process(city)
        }
    }
}
```
Но это пример того как **не нужно** пользоваться `Optional`. 
`Optional` - это монада и им нужно пользоваться в соответствующем стиле:
```java
personMap.find("Name")
   .flatMap(Person::getAddress)
   .flatMap(Address::getCity)
   .ifPresent(ThisClass::process);
```
Подразумевается, что мы используем модифицированную версию `Map`:
```java
public static class Map<T, U> extends HashMap<T, U> {
    public Optional<U> find(T key) {
        return Optional.ofNullable(super.get(key));
    }
}
```
а методы `getAddress` и `getCity` возвращают `Optional<Address>` и `Optional<City>` соответственно.

Примечание 1: в примере выше мы использовали ссылки на методы, чтобы сделать код немного чище.
Кто-то может сказать, что ссылки на методы не являются функциями.
Хотя по сути ссылка на метод - это синтаксический сахар поверх:
```java
personMap.find("Name")
  .flatMap(x -> x.getAddress())
  .flatMap(x -> x.getCity())
  .ifPresent(() -> process(x));
``` 
Здесь `x -> x.getAddress()` - это функция преобразования `T -> M<U>`.

Примечание 2: заметьте, что `ifPresent` - это то же самое, что `forEach` для коллекций и стримов.
Этот метод должен был быть назван `forEach`, несмотря на то, что в `Optional` содержится не более
одного элемента.
Тогда связь между монадой `Optional` и монадой `List` была бы более очевидной.
К сожалению `List` не является монадой в Java 8, но он может быть легко преобразован в `Stream`.

Конечно, некоторые из этих методов могут всегда возвращать значение (и никогда ссылку на `null`).
Например, для приведенного ниже класса:
```java
public static class Address {

    public final City city;

    public Address(City city) {
        this.city = city;
    }

    public City getCity() {
        return city;
    }
}
```
нам потребуется изменить код следующим образом:
```java
personMap.find("Name")
    .flatMap(Person::getAddress)
    .map(Address::getCity)
    .ifPresent(ThisClass::process);
```
Именно так нужно использовать `Optional`.

## (Отсутствующая) монада Try ##

Мы узнали, что в Java 8 существуют монады.
`Stream` тоже является монадой.
Мы должны использовать монады в функциональном стиле,
а тот, кто боится монад может и дальше игнорировать их и продолжать бороться с NPE.

Так все прекрасно?
Нет.
Главная проблема заключается в том, что отсутствует много всего, что должно способствовать 
использованию монад.
В примере выше метод `get` интерфейса `Map` необходимо было изменить, так же как и все методы,
которые могли вернуть `null`.
Так как нельзя нарушать обратную совместимость, предыдущие версии методов должны быть объявлены
устаревшими (deprecated), и должны быть добавлены новые методы, как например `find` в словаре.
(Я не говорю о методах, принимающих `null` в качестве аргумента - таких методов не должно быть
вовсе).

Другая важная проблема заключается в том, что монада `Optional` хороша только в тех случаях,
когда отсутствие значения не является ошибкой.
В нашем случае, когда все методы должны возвращать значение, либо кидать исключение, мы
попадаем в затруднительную ситацию, поскольку у нас нет монады для этого.

Вот что мы могли бы сделать в императивном стиле программирования:
```java
Person person = personMap.get("Name");
if (person != null) {
    Adress address = person.getAddress();
    if (address != null) {
        City city = address.getCity();
        if (city != null) {
            process(city)
        } else {
            throw new IllegalStateException("Address as no city");
        }
    } else {
        throw new IllegalStateException("Person has no address");
    }
} else {
  throw new IllegalStateException("Name not found in map");
}
```
Заметьте, что пробрасывание исключений - это что-то вроде современной формы оператора `goto`, 
с разницей в том, что с исключениями мы не знаем, куда направимся.

Для того чтобы воспользоваться тем же стилем программирования, что и с `Optional`, нам 
необходима еще одна монада, которую часто называют `Try`.
Но в Java 8 такой монады нет.
Мы можем написать ее самостоятельно, это не слишком сложно:
```java
public abstract class Try<V> {

    private Try() { }

    public abstract Boolean isSuccess();

    public abstract Boolean isFailure();

    public abstract void throwException();

    public static <V> Try<V> failure(String message) {
        return new Failure<>(message);
    }

    public static <V> Try<V> failure(String message, Exception e) {
        return new Failure<>(message, e);
    }

    public static <V> Try<V> failure(Exception e) {
        return new Failure<>(e);
    }

    public static <V> Try<V> success(V value) {
        return new Success<>(value);
    }

    private static class Failure<V> extends Try<V> {

        private RuntimeException exception;

        public Failure(String message) {
            super();
            this.exception = new IllegalStateException(message);
        }

        public Failure(String message, Exception e) {
            super();
            this.exception = new IllegalStateException(message, e);
        }

        public Failure(Exception e) {
            super();
            this.exception = new IllegalStateException(e);
        }

        @Override
        public Boolean isSuccess() {
            return false;
        }

        @Override
        public Boolean isFailure() {
            return true;
        }

        @Override
        public void throwException() {
            throw this.exception;
        }
    }
    private static class Success<V> extends Try<V> {

        private V value;

        public Success(V value) {
            super();
            this.value = value;
        }

        @Override
        public Boolean isSuccess() {
            return true;
        }

        @Override
        public Boolean isFailure() {
            return false;
        }

        @Override
        public void throwException() {
            //log.error("Method throwException() called on a Success instance");
        }
    }

    // various method such as map an flatMap
}
```
Подобный класс может заменить `Optional`.
Главная разница между ними в том, что если на каком-либо этапе выполения программы один из
компонентов вернет `Failure` вместо `Success`, программа продолжит выполняться со следующей строчки.
Например, если методы `Map.find()`, `Person.getAddress` и `Address.getCity()` будут возвращать
`Try<Something>`, то мы можем переписать предыдущий пример следующим образом:
```java
personMap.find("Name")
    .flatMap(Person::getAddress)
    .flatMap(Address::getCity)
    .ifPresent(This.class::process);
``` 
Да, это тот же самый код, который мы написали с использованием класса `Optional`.
Разница заключается в используемых классах:
```java

public static class Map<T, U> extends HashMap<T, U> {
    public Try<U> find(T key) {
        U value = super.get(key);
        if (value == null) {
            return Try.failure("Key " + key + " not found in map");
        }
        else {
            return Try.success(value);
        }
    }
}
```
Так как Java не позволяет перегружать методы, которые отличаются только типом возвращаемого значения, 
нам придется выбрать различные имена методам, возвращающим `Optional` и `Try`.
Но подбирать имена методам необязательно, потому что `Try` может полностью заменить собой `Optional`.
Единственное отличие заключается в том, что если мы хотим обработать исключение, нам нужен
специальные методы в классе `Try` для этого:
```java
public void ifPresent(Consumer c) {
    if (isSuccess()) {
        c.accept(successValue());
    }
}

public void ifPresentOrThrow(Consumer<V> c) {
    if (isSuccess()) {
        c.accept(successValue());
    } else {
        throw ((Failure<V>) this).exception;
    }
}

public Try<RuntimeException> ifPresentOrFail(Consumer<V> c) {
    if (isSuccess()) {
        c.accept(successValue());
        return failure("Failed to fail!");
    } else {
        return success(failureValue());
    }
}
```
Это дает пользователю (разработчику бизнес-приложений) выбор.
Он может использовать `ifPresent` для того чтобы получить с помощью `Try` тот же результат, что
и с `Optional` и игнорировать все исключения.
Или он может использовать `ifPresentOrThrow`, чтобы выбросить исключение, если оно есть.
Или он может использовать `ifPresentOrFail`, если хочет обработать исключение каким-либо другим
образом, например как во фрагменте ниже:
```java
personMap.find("Name")
    .flatMap(Person::getAddress)
    .flatMap(Address::getCity)
    .ifPresentOrFail(TryTest::process)
    .ifPresent(e -> Logger.getGlobal().info(e.getMessage()));
```
Заметьте, что исключение, которое мы получаем в последней строчке, могло возникнуть в любом из 
предыдущих методов.
Оно просто передается из одной функции в другую.
Благодаря этом разработчику API не нужно заботиться, что делать с исключением.
Результат такой же, как в случае, если пользователь самостоятельно выбрасывает исключение и 
перехватывает его, без необходимости использования `try-catch` блока и без риска забыть 
обработать непроверяемое исключение.

## Другие полезные монады ##

Множество других монад могут быть очень полезны.
Мы можем использовать монады, чтобы обращаться с неопределенностями (функциями, которые
обычно возвращают различные значения, как например дату/время или генераторы
случайных чисел).
Мы можем использовать монады для обработки функций, возвращающих несколько значений.
`List` не является монадой в Java, но мы можем с легкостью написать свою монаду-список, либо
мы можем преобразовать список в `Stream`, а `Stream` уже является монадой.
Также мы можем использовать монады для обработки данных с отложенным вычислением (future values)
и даже для оберток над примитивами.

В Java 8 метод `Collection.stream()` позволяет преобразовать коллекцию в монаду.

## Так что не так? ##

Если написать собственную монаду так просто, то что не так?
Тому есть три причины.

Первая из них заключается в том, что написав свои монады, мы не сможем использовать их в 
открытом API, потому что каждое новое API будет иметь свою собственную реализацию, несовместимую
с нашей.
Нам нужна стандартная реализация, которой все могли бы пользоваться совместно.

Вторая причина состоит в том, что Java API должно быть модернизировано с учетом этих монад.
Java с выходом восьмой версии должна была быть модернизирована хотя бы с учетом появления 
`Optional`.

И последняя причина касается примитивных типов.
`Optional` не может обрабатывать примитивные типы, поэтому были добавлены отдельные реализации
для `int`, `double` и `long` (`OptionalInt`, `OptionalDouble` и `OptionalLong`). 
Недостаткам использования примитивных типов в функциональном подходе была посвящена 
вторая [статья][Wrong-2-Ru] данной серии.    




[OriginalLink]: https://dzone.com/articles/whats-wrong-java-8-part-iv
[OptionalPoint]: https://dzone.com/articles/java-8-optional-whats-point
[Wrong-2-Ru]: Wrong_in_Java_8_Part_2.md