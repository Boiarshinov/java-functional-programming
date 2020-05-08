# Подводные камни бурных потоков (параллельных стримов) #

Статья посвящена опасностям, которые таят параллельные стримы для разработчиков веб-приложений.

## Введение ##

При выполнении очередной задачи на проекте, я столкнулся с небольшой проблемой:
мне нужно было запросить у стороннего сервиса данные для некоторой группы элементов.
У этого сервиса существовал endPoint, который позволял получать данные только для одного элемента.
Отправить в удаленный сервис сразу всю группу было невозможно, поэтому пришлось отправлять их по одному.

Естественно между отправкой запроса в сервис и получением ответа существовала некоторая задержка.
Для использования в примерах данной статьи, давайте сымитируем запрос в удаленный сервис
следующим образом (я сделал метод обобщенным, чтобы можно было использовать ссылку на метод):
```java
private <E> E callRemoteService(E element) {
    try {
        Thread.sleep(DELAY);
    } catch (InterruptedException ignore) { }

    return element;
}
```
Пусть значение этой задержки составит 50 миллисекунд, хотя точное значение я не замерял:
```java
private final static long DELAY = 50L;
``` 
В качестве типа элементов будем использовать строки (и чуть позже дополнительный формат данных).
Давайте создадим стрим из элементов, количество элементов возьмем с потолка - 10:
```java
private final static int AMOUNT_OF_ELEMENTS = 10;

private Stream<String> generateElements() {
    return Stream.iterate(1, i -> i + 1)
        .limit(AMOUNT_OF_ELEMENTS)
        .map(num -> "el-" + num);
}
```
Давайте убедимся, что время обработки всех элементов пропорционально их количеству:
```java
@Test
public void callRemoteServiceSequentially() {
    System.out.println("El\t\tDelay\n");

    this.generateElements()
        .map(this::callRemoteService)
        .peek(this::printDelayForElement)
        .forEach(this::doNothing);
}
```
В результате получим:
```
El      Delay

el-1	53
el-2	103
el-3	153
el-4	204
el-5	254
el-6	304
el-7	354
el-8	405
el-9	455
el-10	505
```
Как и ожидалось, общее время, потраченное на получение ответа от удаленного сервера для 10 элементов
составило около 500 миллисекунд.
Все элементы стрима были обработаны одной нитью, которая при этом большую часть времени простаивала.
Схематично можно изобразить обработку элементов стрима следующим образом:
![Sequential Stream](images/01.%20Sequential%20Stream.png)

Код методов `printDelayForElement` и `doNothing` не приведен умышленно, потому что их функционал 
очевиден из их названия. Далее в этой статье код элементарных методов также не будет приведен.


## Как превратить стрим в параллельный ##

Несложно заметить, что большую часть времени наша программа простаивает, ожидая ответа от удаленного
сервера.
Уменьшить время ожидания можно, отправив запрос для каждого элемента, в отдельном потоке.
Тем более что распараллелить стримы так легко!

В интерфейсе `Stream` объявлен метод `parallel()`, который распараллеливает стрим.
В противоположность ему объявлен метод `sequential()`, который параллельный стрим делает 
последовательным.

К сожалению, часть операций над данными стрима обработать параллельно, а часть последовательно 
не получится. 
```java
list.stream()
    .parallel() // Ничего не выйдет, стрим будет последовательным.
    .map(Element::getSomething)
    .sequential()
    .forEach(Something::doSomethigSequentially)
```
Стрим на всем протяжении своей жизни может быть только в одном состоянии.
Каким будет стрим зависит от того, какой из двух методов `parallel()` или `sequential()` был
вызван последним.

Также у интерфейса `Collection` вместе с методом `stream()` объявлен метод `parallelStream`, 
который позволяет получить из коллекции параллельный стрим без необходимости последующего
вызова метода `parallel()`. 


## ForkJoinPool ##

Параллельное выполнение стримов основано на пуле нитей `ForkJoinPool`, разработанном большим авторитетом
в области параллельных и конкурентных вычислений [Дагом Ли][DougLea]. 

При использовании параллельных стримов стратегию распараллеливания настроить невозможно.
Это значит, что существует какой-то дефолтный пул `ForkJoinPool`, который используется для вычисления
стримов.
Для создания дефолтного пула нитей в классе `ForkJoinPool` определен статический метод `commonPool()`.
А выяснить количество нитей в нем можно с помощью метода `getCommonPoolParallelism()`.
```java
@Test
public void printPoolSize() {
    int defaultNumberOfThreads = ForkJoinPool.getCommonPoolParallelism();
    System.out.println("Size of default thread pool: " + defaultNumberOfThreads);
}
```
Для моего рабочего компьютера с 12-ядерным процессором размер пула составил
```java
11
```
что неудивительно, потому что `ForkJoinPool` обычно создает количество нитей равное количеству ядер - 1.
Так сделано, потому что задачи будут выполняться не только в нитях созданных `ForkJoinPool`, но
и в той нити, в которой этот пул был запущен.
В случае наших тестов этой нитью будет являться `main`.

Следует уточнить, что для выполнения параллельных стримов не всегда будет использован пул с дефолтным
значением параллелизма.
При обработке параллельных стримов размер пула нитей выбирается в зависимости от нагрузки на 
ядра процессора и может быть меньше количества ядер -1.
Но в тестовой среде, когда нагрузка на процессор незначительна, количество нитей в пуле почти всегда 
будет равно 12 (с учетом `main`).

[DougLea]: https://ru.wikipedia.org/wiki/%D0%9B%D0%B8,_%D0%94%D0%B0%D0%B3 


## Распараллеливание запросов ##

Давайте попробуем распараллелить стрим и посмотреть, уменьшится ли время выполнения:
```java
@Test
public void callRemoteServiceInParallel() {
    final String template = "%-40s\t%4s\t%4s\n";
    System.out.println(String.format(template, "Thread name", "start", "finish"));

    this.generateElements()
        .parallel()
        .map(this::convertToThreadInfo)
        .map(this::callRemoteService)
        .map(this::updateFinishTime)
        .forEach(ThreadInfo::printLifecycleInfo);
}
```
Воспользуемся здесь в качестве элемента дополнительным типом данных, инкапсулирующим в себе имя нити, 
в которой обрабатывается элемент, а также времена начала и завершения обработки элемента:
```java
@Data
private static class ThreadInfo {

    private String threadName;
    private long start;
    private long finish;

    private final static String template = "%-40s\t%4d\t%4d";

    public void printLifecycleInfo() {
        System.out.println(String.format(template, threadName, start, finish));
    }
}
```
Метод `convertToThreadInfo()` преобразует строковые элементы в `ThreadInfo`, а метод `updateFinishTime`
записывает в поле `finish` элемента `ThreadInfo` время с начала работы программы.

В консоль будет выведено:
```
Thread name                             	start	finish

ForkJoinPool.commonPool-worker-2        	  12	  62
main                                    	  12	  62
ForkJoinPool.commonPool-worker-13       	  12	  62
ForkJoinPool.commonPool-worker-6        	  13	  63
ForkJoinPool.commonPool-worker-9        	  12	  62
ForkJoinPool.commonPool-worker-15       	  13	  63
ForkJoinPool.commonPool-worker-1        	  13	  63
ForkJoinPool.commonPool-worker-11       	  12	  63
ForkJoinPool.commonPool-worker-4        	  13	  63
ForkJoinPool.commonPool-worker-8        	  13	  63
```
Заметьте, что часть данных обрабатывается нитью `main`.

Время выполнения уменьшилось почти в 10 раз.
Еще порядка 10 миллисекунд ушло на то, чтобы подготовить пул нитей.
Увеличение производительности было достигнуто за счет распределения задач по различным ядрам процессора.
Графически это можно изобразить следующим образом:
![Parallel Stream](images/02.%20Parallel%20Stream.png)

Что же произойдет, если количество элементов превысит количество ядер?
Для того чтобы узнать это попробуем подсчитать количество нитей, в которых обрабатываются
элементы:
```java
@Test
public void countThreadAmount() {
    final int amountOfElements = 100;

    final long threadsCount = this.generateElements(amountOfElements)
        .parallel()
        .map(this::convertToThreadInfo)
        .map(this::callRemoteService)
        .map(ThreadInfo::getThreadName)
        .distinct()
        .count();

    System.out.println("Count of threads for " + amountOfElements
        + " elements: " + threadsCount);
}
```
В консоль будет выведено:
```
Count of threads for 100 elements: 12
```
Количество нитей выделенных для обработки данных в параллельных не изменилось - 
оно соответствует количеству ядер процессора.
В нити загружаются элементы, проходят полный цикл обработки, затем в нить запускается новый элемент.
Графически это можно изобразить следующим образом:
![Parallel Streams with many elements](images/03.%20Parallel%20Stream.%20Many%20elements.png) 

Ну что же, можно коммитить и отправлять код в продакшен. 
Или пока еще рано?

Давайте не будем торопиться и посмотрим как сработает наш код в боевых условиях, когда
нашим веб-приложением одновременно пользуется множество пользователей. 


## Распараллеливание запросов в контейнере сервлетов ##

Пусть тем методом, который отправляет запрос в удаленный сервис, решило воспользоваться одновременно
10 пользователей:
```java
private final static int AMOUNT_OF_USERS = 10;
```
Это число как раз соответствует минимальному количеству нитей в пуле контейнера сервлетов Jetty.

Вынесем создание нити, обрабатывающей группу элементов, в отдельный метод:
```java
private Callable<List<ThreadInfo>> generateParallelCallable() {
    return () -> this.generateElements()
        .parallel()
        .map(this::convertToThreadInfo)
        .map(this::callRemoteService)
        .map(this::updateFinishTime)
        .collect(Collectors.toList());
}
``` 
и затем запустим эти нити одновременно:
```java
@Test
public void callRemoteServiceInContainer() {
    final String template = "%-40s\t%4s\t%4s\n";
    System.out.println(
        String.format(template, "Thread name", "start", "finish"));

    Stream.generate(this::generateParallelCallable)
        .limit(AMOUNT_OF_USERS)
        .map(this::call)
        .flatMap(Collection::stream)
        .forEach(ThreadInfo::printLifecycleInfo);
}
```
Выполнив этот тест, получим:
```
Thread name                             	start	finish

ForkJoinPool.commonPool-worker-13       	  13	  63
ForkJoinPool.commonPool-worker-2        	  14	  64
ForkJoinPool.commonPool-worker-4        	  14	  64
ForkJoinPool.commonPool-worker-15       	  14	  64
ForkJoinPool.commonPool-worker-8        	  14	  64
ForkJoinPool.commonPool-worker-9        	  13	  63
main                                    	  13	  63
ForkJoinPool.commonPool-worker-6        	  14	  64
ForkJoinPool.commonPool-worker-11       	  14	  64
ForkJoinPool.commonPool-worker-1        	  14	  64
ForkJoinPool.commonPool-worker-15       	  69	 119
ForkJoinPool.commonPool-worker-11       	  69	 119
ForkJoinPool.commonPool-worker-6        	  69	 119
ForkJoinPool.commonPool-worker-4        	  69	 119
...
ForkJoinPool.commonPool-worker-11       	 500	 550
ForkJoinPool.commonPool-worker-8        	 500	 550
ForkJoinPool.commonPool-worker-1        	 500	 550
```
В чем же дело? 
Почему общее время выполнение такое, как будто для каждого пользователя группа элементов
обрабатывалась последовательно? 
Может быть, стрим стал последовательным?
Нет, дело не в этом.

Обратите внимание на названия нитей - в их названии все так же присутствует `common-Pool`. 
Дело в том, что **для обработки данных всех параллельных стримов используется только один `ForkJoinPool`
на все приложение**!

Давайте проверим количество нитей, задействованных для обработки пользовательских запросов:
```java
@Test
public void countThreadsInContainer() {
    final long count = Stream.generate(this::generateParallelCallable)
        .limit(AMOUNT_OF_USERS)
        .map(this::call)
        .flatMap(Collection::stream)
        .map(ThreadInfo::getThreadName)
        .distinct()
        .count();

    System.out.println("Number of threads: " + count);
}
```
В консоль будет выведено:
```
Number of threads: 12
```

Это точно не то, чего мы хотели достигнуть.
Чего мы хотели достичь, так это уменьшить время обработки пользовательских запросов за счет 
конкурирования между нитями за процессорное время. 
Желаемый результат можно изобразить следующим образом:
![Ideal Stream](images/04.%20Ideal%20Stream.png)


## Ускорение параллельных стримов ##
Для того чтобы достичь желаемого результата, нам необходимо каждый параллельный стрим запускать в
своем пуле нитей.
```java
private Thread generateThreadWithNewPool() {
    final Runnable runnable = () -> {
        final ForkJoinPool forkJoinPool = new ForkJoinPool();
        final Callable<List<ThreadInfo>> task =
            this.generateParallelCallable();
        final List<ThreadInfo> threadInfoList =
            this.callInParallel(task, forkJoinPool);
        this.printPoolInfo(threadInfoList);
    };

    return new Thread(runnable);
}
```
Сымитируем одновременное обращение нескольких пользователей:
```java
@Test
public void parallelCallWithNewPoolForEach() {
    final String template = "%-20s\t%5s\t%5s\n";
    System.out.println(
        String.format(template, "Pool name", "start", "finish"));

    Stream.generate(this::generateThreadWithNewPool)
        .limit(AMOUNT_OF_USERS)
        .forEach(Thread::start);

    this.waitUntilAllThreadsDie();
}
``` 
В консоли получим:
```
Pool name           	start	finish

ForkJoinPool-5      	   24	  110
ForkJoinPool-4      	   23	  111
ForkJoinPool-10     	   42	  117
ForkJoinPool-8      	   53	  120
ForkJoinPool-7      	   23	  122
ForkJoinPool-3      	   40	  125
ForkJoinPool-9      	   52	  125
ForkJoinPool-6      	   70	  127
ForkJoinPool-1      	   67	  127
ForkJoinPool-2      	   73	  127
```
Идеально! 
100 задач по 50 миллисекунд каждая выполнились в сумме меньше чем за 150 миллисекунд!
Магия, не иначе!

Но в нашем приложении кроме данного запроса, наверняка есть и другие методы, в которых мы хотим 
использовать параллельные стримы. 
И создавать для каждого из них отдельный пул нитей, который затем выкидывать на помойку, кажется 
расточительным.
Было бы неплохо ограничить количество создаваемых пулов для обработки параллельных стримов.
Вот мы и пришли к идее о создании пула пулов нитей. 

Создание пула пулов нитей потребует введения нового слоя в логику приложения.
Готовы ли вы заниматься этим или лучше оставить контейнер сервлетов заниматься конкурентностю 
в одиночку - решать вам.


## Другие подводные камни параллельных стримов (ForkJoinPool) ##

// Про другие подводные камни параллельности (reduce и пр.)

Факторы влияющие на производительность параллельных стримов:
- Объем данных. Чем больше данных, тем больше выигрыш от использования параллельных стримов.
- Структура исходных данных. ArrayList хорошо параллелится, LinkedList - очень плохо.
- Упаковка. Примитивные типы обрабатываются быстрее (но кто их использует для полей бизнес-объектов?).
- Число ядер. Чем больше ядер в момент выполнение, тем быстрее обработается стрим.
- Стоимость обработки элемента. Чем дольше обрабатывается элемент, тем больше выигрыш.


## Выводы ##

// Наезд на разработчиков Java - почему они не упомянули обо все этом в javadoc'ах.
Удивительно, что разработчики Java так сильно пиарили легкодостижимое распараллеливание стримов

Вот такой информативный javadoc представлен в Stream API на метод `parallel()`:
![parallel javadoc](images/parallel%20method%20javadoc.png)
И вот такой на метод `parallelStream()`:
![parallelStream javadoc](images/parallelStream%20method%20javadoc.png)


## Источники ##

- What's wrong in Java 8, Part III: Streams and Parallel Streams. [DZone][Wrong-3]. [Перевод][Wrong-3-Ru]
- Ричард Уорбэртон. Лямбда-выражения в Java 8.
- Сергей Куксенко - Stream API, часть 1. [YouTube][Kuksenko-1]

[Wrong-3]: https://dzone.com/articles/whats-wrong-java-8-part-iii
[Wrong-3-Ru]: docs/translations/Wrong_in_Java_8_Part_3.md
[Kuksenko-1]: https://www.youtube.com/watch?v=O8oN4KSZEXE