package dev.boiarshinov.book.model;

import lombok.Data;

import java.util.ArrayList;
import java.util.Collection;

@Data
public class Artist {
    private final String name;
    private final Collection<Artist> members;
    private final String origin;

    public Artist(String name, String origin) {
        this(name, new ArrayList<>(), origin);
    }

    public Artist(String name, Collection<Artist> members, String origin) {
        this.name = name;
        this.members = members;
        this.origin = origin;
    }
}
