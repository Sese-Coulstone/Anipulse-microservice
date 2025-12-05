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
@Table(name = "anime", indexes = {
        @Index(name = "idx_mal_id", columnList = "mal_id", unique = true),
        @Index(name = "idx_title", columnList = "title"),
        @Index(name = "idx_score", columnList = "score")
})
public class Anime {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * MyAnimeList ID - unique identifier from JIKAN API
     */
    @Column(name = "mal_id", unique = true, nullable = false)
    private Long malId;

    @Column(nullable = false, length = 500)
    private String title;

    @Column(name = "title_english", length = 500)
    private String titleEnglish;

    @Column(columnDefinition = "TEXT")
    private String synopsis;

    private Integer episodes;

    private Double score;

    @Column(name = "scored_by")
    private Integer scoredBy;

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

    @Column(name = "aired_from")
    private LocalDate airedFrom;

    @Column(name = "aired_to")
    private LocalDate airedTo;

    @Column(name = "image_url", length = 500)
    private String imageUrl;

    /**
     * Content rating: G, PG, PG-13, R, R+, Rx
     */
    @Column(length = 50)
    private String rating;

    @Column(name = "anime_rank")
    private Integer animeRank;

    private Integer popularity;

    @ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinTable(
            name = "anime_genre",
            joinColumns = @JoinColumn(name = "anime_id"),
            inverseJoinColumns = @JoinColumn(name = "genre_id")
    )
    @Builder.Default
    private Set<AnimeGenre> genres = new HashSet<>();

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    /**
     * Last time data was synced from JIKAN API
     */
    @Column(name = "last_synced_at")
    private LocalDateTime lastSyncedAt;
}
