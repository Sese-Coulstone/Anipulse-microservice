package com.anipulse.animeservice.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashSet;
import java.util.Set;

/**
 * Entity representing anime genres
 * Maintains a many-to-many relationship with Anime
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "tbl_genre", indexes = {
    @Index(name = "idx_mal_genre_id", columnList = "malGenreId"),
    @Index(name = "idx_genre_name", columnList = "name")
})
public class AnimeGenre {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Genre ID from MyAnimeList/JIKAN API
     */
    @Column(unique = true, nullable = false)
    private Integer malGenreId;

    @Column(unique = true, nullable = false, length = 100)
    private String name;

    @ManyToMany(mappedBy = "genres", fetch = FetchType.LAZY)
    @Builder.Default
    private Set<Anime> animes = new HashSet<>();

    /**
     * Constructor for creating genre with just name and MAL ID
     */
    public AnimeGenre(Integer malGenreId, String name) {
        this.malGenreId = malGenreId;
        this.name = name;
    }
}
