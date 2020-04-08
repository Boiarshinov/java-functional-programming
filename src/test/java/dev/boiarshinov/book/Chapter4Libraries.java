package dev.boiarshinov.book;

import dev.boiarshinov.book.model.Artist;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;
import lombok.RequiredArgsConstructor;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * Chapter 4 "Libraries".
 */
public class Chapter4Libraries {

    /**
     * Exercise 1. FlatMapping.
     */
    public interface Performance {

        String getName();

        Stream<Artist> getMusicians();

        default Stream<Artist> getAllMusicians() {
            return this.getMusicians()
                .flatMap(artist -> Stream.concat(Stream.of(artist), artist.getMembers().stream()));
        }
    }

    /**
     * Exercise 2. Equals and hashcode overriding.
     */
    interface Overridable {

        //Not compile
//        default boolean equals(Object o) {
//            return true;
//        }

        //Not compile
//        default int hashCode() {
//            return 5;
//        }
    }

    /**
     * Exercise 3. Optional.
     */
    @RequiredArgsConstructor
    public static class Artists {

        private final List<Artist> artists;

        public Optional<Artist> getArtist(int index) {
            if (index < 0 || index >= artists.size()) {
                return Optional.empty();
            }
            return Optional.of(artists.get(index));
        }

        public String getArtistName(int index) {
            return this.getArtist(index)
                .map(Artist::getName)
                .orElse("unknown");
        }
    }

    @Test
    public void artists() {
        final List<Artist> artistsList = Arrays.asList(
            new Artist("Simon", "USA"),
            new Artist("Garfunkel", "USA")
        );
        final Artists artists = new Artists(artistsList);

        Assert.assertEquals(artists.getArtistName(0), "Simon");
        Assert.assertEquals(artists.getArtistName(2), "unknown");
    }
}
