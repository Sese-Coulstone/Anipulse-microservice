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
 * Entity for detailed anime ratings
 * Provides granular rating data for AI recommendation engine
 * Separates ratings into components: story, animation, characters
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "tbl_user_anime_rating", indexes = {
    @Index(name = "idx_user_rating", columnList = "userId"),
    @Index(name = "idx_anime_rating", columnList = "animeId"),
    @Index(name = "idx_overall_rating", columnList = "overallRating"),
    @Index(name = "idx_rated_at", columnList = "ratedAt")
})
@IdClass(UserAnimeRating.UserAnimeRatingId.class)
public class UserAnimeRating {

    /**
     * User ID from user-service
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
     * Story/plot rating (1-10)
     */
    @Column(nullable = false)
    private Integer storyRating;

    /**
     * Animation quality rating (1-10)
     */
    @Column(nullable = false)
    private Integer animationRating;

    /**
     * Character development rating (1-10)
     */
    @Column(nullable = false)
    private Integer characterRating;

    /**
     * Overall rating (1-10)
     * Can be calculated average or user's independent overall score
     */
    @Column(nullable = false)
    private Integer overallRating;

    /**
     * Optional text review
     */
    @Column(columnDefinition = "TEXT")
    private String reviewText;

    @Column(updatable = false)
    @CreationTimestamp
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime ratedAt;

    /**
     * Composite primary key class for UserAnimeRating
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UserAnimeRatingId implements Serializable {
        private String userId;
        private Long animeId;
    }
}
