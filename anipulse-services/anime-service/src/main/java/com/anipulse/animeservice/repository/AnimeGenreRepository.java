package com.anipulse.animeservice.repository;

import com.anipulse.animeservice.entity.AnimeGenre;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repository for AnimeGenre entity
 * Provides data access methods for anime genres
 */
@Repository
public interface AnimeGenreRepository extends JpaRepository<AnimeGenre, Long> {
    
    /**
     * Find genre by MyAnimeList genre ID
     * @param malGenreId the MAL genre ID
     * @return Optional containing the genre if found
     */
    Optional<AnimeGenre> findByMalGenreId(Long malGenreId);
    
    /**
     * Find genre by name
     * @param name the genre name
     * @return Optional containing the genre if found
     */
    Optional<AnimeGenre> findByName(String name);
    
    /**
     * Check if genre exists by MAL genre ID
     * @param malGenreId the MAL genre ID
     * @return true if genre exists
     */
    boolean existsByMalGenreId(Long malGenreId);
    
    /**
     * Check if genre exists by name
     * @param name the genre name
     * @return true if genre exists
     */
    boolean existsByName(String name);
}

