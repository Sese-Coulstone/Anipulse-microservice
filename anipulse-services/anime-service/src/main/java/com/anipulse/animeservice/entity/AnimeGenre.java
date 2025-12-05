package com.anipulse.animeservice.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


/**
 * Entity representing anime genres
 * Maintains a many-to-many relationship with Anime
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "genre", indexes = {
        @Index(name = "idx_mal_genre_id", columnList = "mal_genre_id", unique = true)
})
public class AnimeGenre {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Genre ID from MyAnimeList/JIKAN API
     */
    @Column(name = "mal_genre_id", nullable = false, unique = true)
    private Long malGenreId;

    @Column(nullable = false)
    private String name;

    @Column(length = 50)
    private String type; // genre, theme, demographic
}
