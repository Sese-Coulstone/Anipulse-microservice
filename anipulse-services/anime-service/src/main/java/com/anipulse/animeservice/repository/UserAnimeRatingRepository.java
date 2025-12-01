package com.anipulse.animeservice.repository;

import com.anipulse.animeservice.entity.UserAnimeRating;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository for UserAnimeRating entity
 * Provides data access methods for detailed anime ratings
 * Essential for AI recommendation engine to understand user preferences
 */
@Repository
public interface UserAnimeRatingRepository extends JpaRepository<UserAnimeRating, UserAnimeRating.UserAnimeRatingId> {
    
    /**
     * Find a specific rating by user and anime
     * @param userId the user ID
     * @param animeId the anime ID
     * @return Optional containing the rating if found
     */
    Optional<UserAnimeRating> findByUserIdAndAnimeId(String userId, Long animeId);
    
    /**
     * Get all ratings by a user
     * @param userId the user ID
     * @param pageable pagination info
     * @return page of user's ratings
     */
    Page<UserAnimeRating> findByUserId(String userId, Pageable pageable);
    
    /**
     * Get all ratings for a specific anime
     * @param animeId the anime ID
     * @param pageable pagination info
     * @return page of ratings for the anime
     */
    Page<UserAnimeRating> findByAnimeId(Long animeId, Pageable pageable);
    
    /**
     * Get all ratings by a user (for recommendation engine)
     * @param userId the user ID
     * @return list of all user's ratings
     */
    List<UserAnimeRating> findByUserId(String userId);
    
    /**
     * Get user's top-rated anime
     * @param userId the user ID
     * @param minRating minimum overall rating
     * @return list of highly-rated anime
     */
    @Query("SELECT uar FROM UserAnimeRating uar WHERE uar.userId = :userId AND uar.overallRating >= :minRating ORDER BY uar.overallRating DESC")
    List<UserAnimeRating> findTopRatedByUser(@Param("userId") String userId, @Param("minRating") Integer minRating);
    
    /**
     * Calculate average ratings for an anime
     * @param animeId the anime ID
     * @return array of averages [story, animation, character, overall]
     */
    @Query("SELECT AVG(uar.storyRating), AVG(uar.animationRating), AVG(uar.characterRating), AVG(uar.overallRating) FROM UserAnimeRating uar WHERE uar.animeId = :animeId")
    Object[] getAverageRatingsByAnimeId(@Param("animeId") Long animeId);
    
    /**
     * Count total ratings for an anime
     * @param animeId the anime ID
     * @return count of ratings
     */
    long countByAnimeId(Long animeId);
    
    /**
     * Check if user has rated an anime
     * @param userId the user ID
     * @param animeId the anime ID
     * @return true if rating exists
     */
    boolean existsByUserIdAndAnimeId(String userId, Long animeId);
    
    /**
     * Get users who gave high ratings to an anime (collaborative filtering)
     * @param animeId the anime ID
     * @param minRating minimum overall rating
     * @return list of user IDs who rated highly
     */
    @Query("SELECT uar.userId FROM UserAnimeRating uar WHERE uar.animeId = :animeId AND uar.overallRating >= :minRating")
    List<String> findUserIdsWithHighRating(@Param("animeId") Long animeId, @Param("minRating") Integer minRating);
    
    /**
     * Get user's rating distribution (for preference analysis)
     * @param userId the user ID
     * @return count of ratings grouped by score
     */
    @Query("SELECT uar.overallRating, COUNT(uar) FROM UserAnimeRating uar WHERE uar.userId = :userId GROUP BY uar.overallRating ORDER BY uar.overallRating DESC")
    List<Object[]> getRatingDistributionByUser(@Param("userId") String userId);
}

