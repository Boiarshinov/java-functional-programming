package dev.boiarshinov.book.model;

import lombok.Data;

import java.util.Collection;

@Data
public class Album {
    private final String name;
    private final Collection<Track> tracks;
    private final Collection<Artist> musicians;
}
