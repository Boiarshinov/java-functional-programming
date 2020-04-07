package dev.boiarshinov.book;

import dev.boiarshinov.book.model.Album;
import dev.boiarshinov.book.model.Artist;
import dev.boiarshinov.book.model.Track;
import dev.boiarshinov.util.AssertCustomUtils;
import java.util.function.Function;
import java.util.function.Predicate;
import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Chapter 3 "Streams".
 */
public class Chapter3Streams {

    /**
     * Exercise 1. Task A. Numbers summation function.
     */
    @Test(dataProvider = "sumOfNumbersProvider")
    public void sumOfNumbers(final Collection<Integer> numbers,
                                          final int expectedSum)
    {
        final int actualSum = this.addUp(numbers.stream());

        Assert.assertEquals(actualSum, expectedSum);
    }

    private int addUp(Stream<Integer> numbers) {
        return numbers.reduce(0, Integer::sum);
    }

    @DataProvider
    private Object[][] sumOfNumbersProvider() {
        return new Object[][]{
            {Arrays.asList(-10, 0, 10), 0},
            {Arrays.asList(200, 20, 8), 228},
            {Collections.EMPTY_LIST, 0}
        };
    }

    /**
     * Exercise 1. Task B.
     */
    @Test(dataProvider = "artistsProvider")
    public void testGetArtistsOrigins(final Artist artist, final List<String> expectedOriginsInfo)
    {
        final List<String> originsInfo = this.getArtistsOrigins(artist);
        //fixme

        AssertCustomUtils.assertEqualsNoOrder(originsInfo, expectedOriginsInfo);
    }

    private List<String> getArtistsOrigins(Artist musician) {
        return Stream.of(musician)
            .flatMap(artist -> Stream.of(musician.getName(), musician.getOrigin()))
            .collect(Collectors.toList());
    }

    @DataProvider
    private Object[][] artistsProvider() {
        return new Object[][] {
            {new Artist("Andy Mckee", "USA"), Collections.singletonList("Andy Mckee: USA")}
        };
    }

    /**
     * Exercise 1. Task C. Albums filtering.
     */
    @Test(dataProvider = "littleAlbumsProvider")
    public void testGetLittleAlbums(final Collection<Album> albums,
                                final List<Album> expectedFilteredAlbums)
    {
        final List<Album> actualLittleAlbums = this.getLittleAlbums(albums);

        AssertCustomUtils.assertEqualsNoOrder(actualLittleAlbums, expectedFilteredAlbums);
    }

    private List<Album> getLittleAlbums(Collection<Album> albums) {
        return albums.stream()
            .filter(album -> album.getTracks().size() <= 3)
            .collect(Collectors.toList());
    }

    @DataProvider
    private Object[][] littleAlbumsProvider() {
        final List<Track> beyondYnthTracks = Arrays.asList(
            new Track("Title Song", 182),
            new Track("The Snow", 202)
        );
        final Album beyondYnthAlbum = new Album(
            "Beyond Ynth",
            beyondYnthTracks,
            Collections.singletonList(new Artist("FDG", "German"))
        );

        final List<Track> lineage2Tracks = Arrays.asList(
            new Track("Shepard's Flute", 124),
            new Track("Forest Calling", 135),
            new Track("Unicorn's Rest", 119),
            new Track("Crossroad at Dawn", 120)
        );
        final Album lineage2Album = new Album(
            "Lineage II: CC",
            lineage2Tracks,
            Collections.singletonList(new Artist("Bill Brown", "USA"))
        );

        return new Object[][] {
            {Arrays.asList(beyondYnthAlbum, lineage2Album), Collections.singletonList(beyondYnthAlbum)},
            {Collections.singletonList(lineage2Album), Collections.EMPTY_LIST},
            {Collections.EMPTY_LIST, Collections.EMPTY_LIST}
        };
    }

    /**
     * Exercise 2. Iteration.
     */
    @Test(dataProvider = "bandProvider")
    public void testIteration(final Collection<Artist> artists, int expectedCount) {
        final int totalMembers = (int) artists.stream()
            .map(Artist::getMembers)
            .mapToLong(Collection::size)
            .sum();

        Assert.assertEquals(totalMembers, expectedCount);
    }

    @DataProvider
    private Object[][] bandProvider() {
        final Artist drums = new Artist("Lars Ulrich", "Dutch");
        final Artist lead = new Artist("James Hatfield", "USA");
        final Artist solo = new Artist("Kirk Hammet", "USA");
        final Artist bass = new Artist("Robert Truhilio", "Mexico");

        final Collection<Artist> members = Arrays.asList(drums, lead, solo, bass);

        final Artist metallica = new Artist("Metallica", members, "USA");

        final Artist zakk = new Artist("Zakk Wylde", "USA");

        return new Object[][] {
            {Collections.singletonList(metallica), 4},
            {Collections.singletonList(zakk), 0}
        };
    }

    /**
     * Exercise 3. Terminal or intermediate.
     * Exercise 4. High-level methods.
     */
    @Test
    public void testDifferentMethodTypes() {
        //anyMatch() is terminal. Also anyMatch() - high-level method because it have a lambda argument.
        final int random = new Random().nextInt();
        final boolean hasEven = Stream.of(random, random + 1).anyMatch(x -> x % 2 == 0);

        Assert.assertTrue(hasEven);

        //limit() is intermediate. Also limit() - low-level method because it don't have a lambda argument.
        final Stream<String> twoLettersStream = Stream.of("a", "b", "c").limit(2);

        Assert.assertEquals(
            twoLettersStream.collect(Collectors.joining()),
            "ab"
        );
    }

    /**
     * Exercise 6. Count uppercase symbols.
     */
    @Test(dataProvider = "symbolsProvider")
    public void testUpperCaseCount(final String string, final int expectedCount) {
        final long actualCount = string.chars()
            .filter(Character::isUpperCase)
            .count();

        Assert.assertEquals(actualCount, expectedCount);
    }

    @DataProvider
    public static Object[][] symbolsProvider() {
        return new Object[][] {
            {"AazZ", 2},
            {"abc", 0}
        };
    }

    /**
     * Exercise 7. Find string with max count of uppercase letters.
     */
    @Test(dataProvider = "stringsProvider")
    public void testUpperCaseCount(final List<String> strings, final String expected) {
        Comparator<String> comparator = Comparator.comparingInt(this::countUpperCases);

        final Optional<String> actual = strings.stream().max(comparator);

        final Optional<String> expectedOptional = Optional.ofNullable(expected);
        Assert.assertEquals(actual, expectedOptional);
    }

    private int countUpperCases(String string) {
        return (int) string.chars().filter(Character::isUpperCase).count();
    }

    @DataProvider
    private Object[][] stringsProvider() {
        return new Object[][] {
            {Arrays.asList("no uppercases", "One uppercase", "A Lot Of"), "A Lot Of"},
            {Arrays.asList("no uppercases", "no uppercases 2"), "no uppercases"},
            {Collections.EMPTY_LIST, null}
        };
    }

    /**
     * Increased complexity exercise 1. Write map() using only reduce().
     */
    @Test
    public void testMapByReduce() {
        final Stream<String> inputStream = Stream.of("a", "ab", "abc");
        final Function<String, Integer> function = String::length;
        final List<Integer> expectedLengths = Arrays.asList(1, 2, 3);

        final Stream<Integer> mappedStream = this.mapByReduce(inputStream, function);

        final List<Integer> actualLengths = mappedStream.collect(Collectors.toList());

        Assert.assertEquals(actualLengths, expectedLengths);
    }

    private <T, R> Stream<R> mapByReduce(Stream<T> stream, Function<T, R> function) {
        return stream.reduce(
            new ArrayList<R>(),
            (list, element) -> {
                list.add(function.apply(element));
                return list;
            },
            (accList, listToAdd) -> {
                accList.addAll(listToAdd);
                return accList;
            })
            .stream();
    }

    /**
     * Increased complexity exercise 2. Write filter() using only reduce().
     */
    @Test
    public void testFilterByReduce() {
        final Stream<String> inputStream = Stream.of("a", "ab", "abc", "abcd");
        final Predicate<String> predicate = s -> s.length() > 2;
        final List<String> expectedLengths = Arrays.asList("abc", "abcd");

        final Stream<String> filteredStream = this.filterByReduce(inputStream, predicate);

        final List<String> actualLengths = filteredStream.collect(Collectors.toList());

        Assert.assertEquals(actualLengths, expectedLengths);
    }

    private <T> Stream<T> filterByReduce(Stream<T> stream, Predicate<T> predicate) {
        return stream.reduce(
            new ArrayList<T>(),
            (list, element) -> {
                if (predicate.test(element)) {
                    list.add(element);
                }
                return list;
            },
            (accList, listToAdd) -> {
                accList.addAll(listToAdd);
                return accList;
            })
            .stream();
    }
}
