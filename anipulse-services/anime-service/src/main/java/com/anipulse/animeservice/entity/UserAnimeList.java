package com.anipulse.animeservice.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * Entity tracking user's anime watch lists and progress
 * Used for recommendation service to analyze user preferences and viewing patterns
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "tbl_user_anime_list", indexes = {
    @Index(name = "idx_user_id", columnList = "userId"),
    @Index(name = "idx_anime_id", columnList = "animeId"),
    @Index(name = "idx_rating", columnList = "rating"),
    @Index(name = "idx_watch_status", columnList = "watchStatus"),
    @Index(name = "idx_updated_at", columnList = "updatedAt")
})
@IdClass(UserAnimeList.UserAnimeListId.class)
public class UserAnimeList {

    /**
     * User ID from user-service (not FK due to microservice architecture)
     */
    @Id
    @Column(nullable = false, length = 100)
    private String userId;

    /**
     * Foreign key to Anime entity
     */
    @Id
    @Column(nullable = false)
    private Long animeId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "animeId", insertable = false, updatable = false)
    private Anime anime;

    /**
     * Current watch status of the anime for this user
     */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private WatchStatus watchStatus;

    /**
     * Number of episodes watched (0 to total episodes)
     */
    @Column(nullable = false)
    @Builder.Default
    private Integer progress = 0;

    /**
     * User's rating (1.0 to 10.0, nullable if not rated yet)
     */
    @Column(precision = 3, scale = 1)
    private Double rating;

    /**
     * Personal notes about this anime
     */
    @Column(columnDefinition = "TEXT")
    private String notes;

    @Column(updatable = false)
    @CreationTimestamp
    private LocalDateTime addedAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

    /**
     * When the user completed watching this anime (for recommendation timing)
     */
    private LocalDateTime completedAt;

    /**
     * Composite primary key class for UserAnimeList
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UserAnimeListId implements Serializable {
        private String userId;
        private Long animeId;
    }
}
