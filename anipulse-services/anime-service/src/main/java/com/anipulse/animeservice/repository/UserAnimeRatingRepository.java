package com.anipulse.animeservice.repository;

import com.anipulse.animeservice.entity.UserAnimeRating;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Repository for UserAnimeRating entity
 * Manages detailed anime ratings for AI recommendation engine
 */
@Repository
public interface UserAnimeRatingRepository extends JpaRepository<UserAnimeRating, UserAnimeRating.UserAnimeRatingId> {

    /**
     * Find user's rating for specific anime
     */
    Optional<UserAnimeRating> findByUserIdAndAnimeId(String userId, Long animeId);

    /**
     * Check if user has rated anime
     */
    boolean existsByUserIdAndAnimeId(String userId, Long animeId);

    /**
     * Get all ratings by user with pagination
     */
    Page<UserAnimeRating> findByUserId(String userId, Pageable pageable);

    /**
     * Get all ratings for specific anime (for average calculation)
     */
    Page<UserAnimeRating> findByAnimeId(Long animeId, Pageable pageable);

    /**
     * Calculate average overall rating for anime
     */
    @Query("SELECT AVG(r.overallRating) FROM UserAnimeRating r WHERE r.animeId = :animeId")
    Double getAverageOverallRating(@Param("animeId") Long animeId);

    /**
     * Calculate average story rating for anime
     */
    @Query("SELECT AVG(r.storyRating) FROM UserAnimeRating r WHERE r.animeId = :animeId")
    Double getAverageStoryRating(@Param("animeId") Long animeId);

    /**
     * Calculate average animation rating for anime
     */
    @Query("SELECT AVG(r.animationRating) FROM UserAnimeRating r WHERE r.animeId = :animeId")
    Double getAverageAnimationRating(@Param("animeId") Long animeId);

    /**
     * Calculate average character rating for anime
     */
    @Query("SELECT AVG(r.characterRating) FROM UserAnimeRating r WHERE r.animeId = :animeId")
    Double getAverageCharacterRating(@Param("animeId") Long animeId);

    /**
     * Count total ratings for anime
     */
    long countByAnimeId(Long animeId);

    /**
     * Count total ratings by user
     */
    long countByUserId(String userId);

    /**
     * Get user's highest rated anime (for preference analysis)
     */
    @Query("SELECT r FROM UserAnimeRating r WHERE r.userId = :userId ORDER BY r.overallRating DESC")
    List<UserAnimeRating> findTopRatedByUser(@Param("userId") String userId, Pageable pageable);

    /**
     * Get recently rated anime (for recommendation training)
     */
    @Query("SELECT r FROM UserAnimeRating r WHERE r.ratedAt > :since ORDER BY r.ratedAt DESC")
    List<UserAnimeRating> findRecentlyRated(@Param("since") LocalDateTime since);

    /**
     * Get ratings with reviews
     */
    @Query("SELECT r FROM UserAnimeRating r WHERE r.animeId = :animeId AND r.reviewText IS NOT NULL ORDER BY r.ratedAt DESC")
    Page<UserAnimeRating> findRatingsWithReviews(@Param("animeId") Long animeId, Pageable pageable);

    /**
     * Find similar users based on rating patterns (for collaborative filtering)
     */
    @Query("SELECT r.userId, COUNT(*), AVG(ABS(r1.overallRating - r.overallRating)) " +
            "FROM UserAnimeRating r1 JOIN UserAnimeRating r ON r1.animeId = r.animeId " +
            "WHERE r1.userId = :userId AND r.userId != :userId " +
            "GROUP BY r.userId " +
            "HAVING COUNT(*) >= :minCommonRatings " +
            "ORDER BY AVG(ABS(r1.overallRating - r.overallRating)) ASC")
    List<Object[]> findSimilarUsers(@Param("userId") String userId, @Param("minCommonRatings") long minCommonRatings);

    /**
     * Delete user's rating
     */
    void deleteByUserIdAndAnimeId(String userId, Long animeId);

}
