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
 * Provides data access methods for user's anime watch lists
 * Critical for recommendation service to analyze user preferences
 */
@Repository
public interface UserAnimeListRepository extends JpaRepository<UserAnimeList, UserAnimeList.UserAnimeListId> {
    
    /**
     * Find a specific entry in user's anime list
     * @param userId the user ID
     * @param animeId the anime ID
     * @return Optional containing the list entry if found
     */
    Optional<UserAnimeList> findByUserIdAndAnimeId(String userId, Long animeId);
    
    /**
     * Get all anime in a user's list
     * @param userId the user ID
     * @param pageable pagination info
     * @return page of user's anime list entries
     */
    Page<UserAnimeList> findByUserId(String userId, Pageable pageable);
    
    /**
     * Get user's anime list filtered by watch status
     * @param userId the user ID
     * @param status the watch status
     * @param pageable pagination info
     * @return page of anime with specified status
     */
    Page<UserAnimeList> findByUserIdAndWatchStatus(String userId, WatchStatus status, Pageable pageable);
    
    /**
     * Get user's rated anime (for recommendation engine)
     * @param userId the user ID
     * @return list of anime the user has rated
     */
    @Query("SELECT ual FROM UserAnimeList ual WHERE ual.userId = :userId AND ual.rating IS NOT NULL ORDER BY ual.rating DESC")
    List<UserAnimeList> findRatedAnimeByUserId(@Param("userId") String userId);
    
    /**
     * Get user's completed anime (for recommendation engine)
     * @param userId the user ID
     * @return list of completed anime
     */
    List<UserAnimeList> findByUserIdAndWatchStatus(String userId, WatchStatus status);
    
    /**
     * Get user's recently updated anime
     * @param userId the user ID
     * @param since timestamp to filter from
     * @return list of recently updated anime
     */
    @Query("SELECT ual FROM UserAnimeList ual WHERE ual.userId = :userId AND ual.updatedAt >= :since ORDER BY ual.updatedAt DESC")
    List<UserAnimeList> findRecentlyUpdated(@Param("userId") String userId, @Param("since") LocalDateTime since);
    
    /**
     * Count total anime in user's list
     * @param userId the user ID
     * @return count of anime
     */
    long countByUserId(String userId);
    
    /**
     * Count anime by watch status for a user
     * @param userId the user ID
     * @param status the watch status
     * @return count of anime with the status
     */
    long countByUserIdAndWatchStatus(String userId, WatchStatus status);
    
    /**
     * Check if user has anime in their list
     * @param userId the user ID
     * @param animeId the anime ID
     * @return true if anime is in user's list
     */
    boolean existsByUserIdAndAnimeId(String userId, Long animeId);
    
    /**
     * Get all users who have watched a specific anime (for collaborative filtering)
     * @param animeId the anime ID
     * @return list of user anime list entries
     */
    List<UserAnimeList> findByAnimeId(Long animeId);
    
    /**
     * Get users who completed and rated an anime highly (for collaborative filtering)
     * @param animeId the anime ID
     * @param minRating minimum rating threshold
     * @return list of user IDs who liked this anime
     */
    @Query("SELECT ual.userId FROM UserAnimeList ual WHERE ual.animeId = :animeId AND ual.rating >= :minRating")
    List<String> findUserIdsWhoLikedAnime(@Param("animeId") Long animeId, @Param("minRating") Double minRating);
}

