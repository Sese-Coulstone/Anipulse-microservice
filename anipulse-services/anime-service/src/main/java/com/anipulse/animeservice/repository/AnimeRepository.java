package com.anipulse.animeservice.repository;

import com.anipulse.animeservice.entity.Anime;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repository for Anime entity
 * Provides data access methods for anime metadata
 */
@Repository
public interface AnimeRepository extends JpaRepository<Anime, Long> {
    
    /**
     * Find anime by MyAnimeList ID
     * @param malId the MAL ID from JIKAN API
     * @return Optional containing the anime if found
     */
    Optional<Anime> findByMalId(Long malId);
    
    /**
     * Check if anime exists by MAL ID
     * @param malId the MAL ID from JIKAN API
     * @return true if anime exists
     */
    boolean existsByMalId(Long malId);
    
    /**
     * Search anime by title (case-insensitive partial match)
     * @param title the search query
     * @param pageable pagination info
     * @return page of matching anime
     */
    Page<Anime> findByTitleContainingIgnoreCase(String title, Pageable pageable);
    
    /**
     * Find anime by type (TV, Movie, OVA, etc.)
     * @param type the anime type
     * @param pageable pagination info
     * @return page of anime matching the type
     */
    Page<Anime> findByType(String type, Pageable pageable);
    
    /**
     * Find top-rated anime
     * @param pageable pagination info
     * @return page of anime sorted by score descending
     */
    @Query("SELECT a FROM Anime a WHERE a.score IS NOT NULL ORDER BY a.score DESC")
    Page<Anime> findTopRated(Pageable pageable);
    
    /**
     * Find popular anime by member count
     * @param pageable pagination info
     * @return page of anime sorted by members descending
     */
    @Query("SELECT a FROM Anime a WHERE a.members IS NOT NULL ORDER BY a.members DESC")
    Page<Anime> findPopular(Pageable pageable);
    
    /**
     * Find anime by genre
     * @param genreName the genre name
     * @param pageable pagination info
     * @return page of anime with the specified genre
     */
    @Query("SELECT a FROM Anime a JOIN a.genres g WHERE g.name = :genreName")
    Page<Anime> findByGenreName(@Param("genreName") String genreName, Pageable pageable);
}

