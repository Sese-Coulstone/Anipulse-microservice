package com.anipulse.animeservice.repository;

import com.anipulse.animeservice.entity.UserAnimeList;
import com.anipulse.animeservice.entity.WatchStatus;
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
 * Repository for UserAnimeList entity
 * Manages user's anime watch lists and progress tracking
 * Critical for recommendation service data collection
 */
@Repository
public interface UserAnimeListRepository extends JpaRepository<UserAnimeList, UserAnimeList.UserAnimeListId> {

    /**
     * Find user's anime list entry
     */
    Optional<UserAnimeList> findByUserIdAndAnimeId(String userId, Long animeId);

    boolean existsByUserIdAndAnimeId(String userId, Long animeId);

    /**
     * Get user's complete anime list with pagination
     */
    Page<UserAnimeList> findByUserId(String userId, Pageable pageable);

    /**
     * Get user's anime list filtered by watch status
     */
    Page<UserAnimeList> findByUserIdAndWatchStatus(String userId, WatchStatus status, Pageable pageable);

    /**
     * Count anime by watch status for a user
     */
    long countByUserIdAndWatchStatus(String userId, WatchStatus status);

    /**
     * Get user's rated anime (for recommendations)
     */
    @Query("SELECT u FROM UserAnimeList u WHERE u.userId = :userId AND u.rating IS NOT NULL ORDER BY u.rating DESC")
    List<UserAnimeList> findByUserIdWithRatings(@Param("userId") String userId);

    /**
     * Get user's completed anime (for recommendations)
     */
    @Query("SELECT u FROM UserAnimeList u WHERE u.userId = :userId AND u.watchStatus = 'COMPLETED' ORDER BY u.completedAt DESC")
    List<UserAnimeList> findCompletedAnimeByUserId(@Param("userId") String userId);

    /**
     * Get recently updated user lists (for recommendation model training)
     */
    @Query("SELECT u FROM UserAnimeList u WHERE u.updatedAt > :since ORDER BY u.updatedAt DESC")
    List<UserAnimeList> findRecentlyUpdated(@Param("since") LocalDateTime since);

    /**
     * Get all anime IDs in user's list (for filtering recommendations)
     */
    @Query("SELECT u.animeId FROM UserAnimeList u WHERE u.userId = :userId")
    List<Long> findAnimeIdsByUserId(@Param("userId") String userId);

    /**
     * Find users who watched specific anime (for collaborative filtering)
     */
    @Query("SELECT u.userId FROM UserAnimeList u WHERE u.animeId = :animeId AND u.watchStatus = 'COMPLETED'")
    List<String> findUserIdsWhoWatchedAnime(@Param("animeId") Long animeId);

    /**
     * Get user's anime by genre (for preference analysis)
     */
    @Query("SELECT u FROM UserAnimeList u JOIN u.anime a JOIN a.genres g " +
            "WHERE u.userId = :userId AND g.name = :genreName AND u.watchStatus = 'COMPLETED'")
    List<UserAnimeList> findByUserIdAndGenre(@Param("userId") String userId, @Param("genreName") String genreName);

    /**
     * Count total anime in user's list
     */
    long countByUserId(String userId);

    /**
     * Delete anime from user's list
     */
    void deleteByUserIdAndAnimeId(String userId, Long animeId);
}
