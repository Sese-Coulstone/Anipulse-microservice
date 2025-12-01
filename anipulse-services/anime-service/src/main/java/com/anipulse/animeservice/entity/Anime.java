package com.anipulse.animeservice.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

/**
 * Entity representing anime metadata from JIKAN API (MyAnimeList)
 * Stores core anime information for local caching and reference
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "tbl_anime", indexes = {
    @Index(name = "idx_mal_id", columnList = "malId"),
    @Index(name = "idx_title", columnList = "title"),
    @Index(name = "idx_type", columnList = "type"),
    @Index(name = "idx_score", columnList = "score")
})
public class Anime {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * MyAnimeList ID - unique identifier from JIKAN API
     */
    @Column(unique = true, nullable = false)
    private Long malId;

    @Column(nullable = false, length = 500)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String synopsis;

    @ManyToMany(fetch = FetchType.LAZY, cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinTable(
        name = "tbl_anime_genre",
        joinColumns = @JoinColumn(name = "anime_id"),
        inverseJoinColumns = @JoinColumn(name = "genre_id")
    )
    @Builder.Default
    private Set<AnimeGenre> genres = new HashSet<>();

    private Integer episodes;

    @Column(precision = 3, scale = 2)
    private Double score;

    @Column(length = 1000)
    private String imageUrl;

    /**
     * Type: TV, Movie, OVA, Special, ONA, Music
     */
    @Column(length = 50)
    private String type;

    /**
     * Status: Finished Airing, Currently Airing, Not yet aired
     */
    @Column(length = 50)
    private String status;

    private LocalDate airedFrom;

    private LocalDate airedTo;

    /**
     * Number of users who have this anime in their list (for popularity ranking)
     */
    private Integer members;

    /**
     * Content rating: G, PG, PG-13, R, R+, Rx
     */
    @Column(length = 10)
    private String rating;

    @Column(updatable = false)
    @CreationTimestamp
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

    /**
     * Last time data was synced from JIKAN API
     */
    private LocalDateTime lastSyncedAt;
}
