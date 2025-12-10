package com.anipulse.animeservice.service;

import com.anipulse.animeservice.dto.UserAnimeListDTO;
import com.anipulse.animeservice.dto.UserAnimeListRequestDTO;
import com.anipulse.animeservice.dto.UserAnimeStatsDTO;
import com.anipulse.animeservice.entity.UserAnimeList;
import com.anipulse.animeservice.entity.WatchStatus;
import com.anipulse.animeservice.mapper.UserAnimeListMapper;
import com.anipulse.animeservice.repository.AnimeRepository;
import com.anipulse.animeservice.repository.UserAnimeListRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserAnimeListService {

    private final UserAnimeListRepository userAnimeListRepository;
    private final AnimeRepository animeRepository;
    private final UserAnimeListMapper mapper;
    private final AnimeSearchService animeSearchService;

    // Add anime to user's list or update existing entry
    @Transactional
    @CacheEvict(value = "userAnimeList", key = "#userId")
    public UserAnimeListDTO createOrUpdateAnime(String userId,
                                                UserAnimeListRequestDTO request) {
        log.info("Adding/updating anime (MAL ID: {}) for user {}", request.getMalId(), userId);

        // Ensure anime exists and get the database ID
        Long animeDbId = ensureAnimeExists(request.getMalId());

        UserAnimeList entry = userAnimeListRepository
                .findByUserIdAndAnimeId(userId, animeDbId)
                .orElse(UserAnimeList.builder()
                        .userId(userId)
                        .animeId(animeDbId)
                        .build());

        // Update fields
        entry.setWatchStatus(request.getWatchStatus());
        entry.setProgress(request.getProgress() != null ? request.getProgress() : 0);
        entry.setRating(request.getRating());
        entry.setNotes(request.getNotes());

        // Set completed date if status is COMPLETED
        if (request.getWatchStatus() == WatchStatus.COMPLETED && entry.getCompletedAt() == null) {
            entry.setCompletedAt(LocalDateTime.now());
        }

        entry = userAnimeListRepository.save(entry);
        log.info("Added anime (DB ID: {}) for user {}", animeDbId, userId);
        return mapper.toDTO(entry);
    }

    // Get user's complete anime list with pagination
    @Transactional(readOnly = true)
    @Cacheable(value = "userAnimeList", key = "#userId + '-' + #pageable.pageNumber")
    public Page<UserAnimeListDTO> getUserAnimeList(String userId, Pageable pageable) {
        log.debug("Fetching anime list for user {}, page {}", userId, pageable.getPageNumber());

        return userAnimeListRepository.findByUserId(userId, pageable)
                .map(mapper::toDTO);
    }

    /**
     * Get user's anime list filtered by watch status
     */
    @Transactional(readOnly = true)
    @Cacheable(value = "userAnimeListByStatus", key = "#userId + '_' + #status + '_' + #pageable.pageNumber")
    public Page<UserAnimeListDTO> getUserAnimeListByStatus(String userId, WatchStatus status, Pageable pageable) {
        log.debug("Fetching {} anime for user {}", status, userId);

        return userAnimeListRepository.findByUserIdAndWatchStatus(userId, status, pageable)
                .map(mapper::toDTO);
    }

    /**
     * Get specific anime from user's list
     */
    @Transactional(readOnly = true)
    public UserAnimeListDTO getUserAnimeEntry(String userId, Long animeId) {
        log.debug("Fetching anime {} for user {}", animeId, userId);

        return userAnimeListRepository.findByUserIdAndAnimeId(userId, animeId)
                .map(mapper::toDTO)
                .orElse(null);
    }

    /**
     * Check if anime is in user's list
     */
    @Transactional(readOnly = true)
    public boolean isAnimeInList(String userId, Long animeId) {
        return userAnimeListRepository.existsByUserIdAndAnimeId(userId, animeId);
    }

    /**
     * Delete anime from user's list
     */
    @Transactional
    @CacheEvict(value = {"userAnimeList", "userAnimeListByStatus"}, key = "#userId")
    public void removeAnimeFromList(String userId, Long animeId) {
        log.info("Removing anime {} from user {}'s list", animeId, userId);
        userAnimeListRepository.deleteByUserIdAndAnimeId(userId, animeId);
    }

    /**
     * Get anime IDs in user's list (for filtering recommendations)
     */
    @Transactional(readOnly = true)
    @Cacheable(value = "userAnimeIds", key = "#userId")
    public List<Long> getUserAnimeIds(String userId) {
        return userAnimeListRepository.findAnimeIdsByUserId(userId);
    }

    /**
     * Get user's statistics
     */
    @Transactional(readOnly = true)
    @Cacheable(value = "userAnimeStats", key = "#userId")
    public UserAnimeStatsDTO getUserStatistics(String userId) {
        return UserAnimeStatsDTO.builder()
                .totalAnime(userAnimeListRepository.countByUserId(userId))
                .watching(userAnimeListRepository.countByUserIdAndWatchStatus(userId, WatchStatus.WATCHING))
                .completed(userAnimeListRepository.countByUserIdAndWatchStatus(userId, WatchStatus.COMPLETED))
                .onHold(userAnimeListRepository.countByUserIdAndWatchStatus(userId, WatchStatus.ON_HOLD))
                .dropped(userAnimeListRepository.countByUserIdAndWatchStatus(userId, WatchStatus.DROPPED))
                .planToWatch(userAnimeListRepository.countByUserIdAndWatchStatus(userId, WatchStatus.PLAN_TO_WATCH))
                .build();
    }


    /**
     * Ensure anime exists in database and return its database ID
     * @param malId MyAnimeList ID
     * @return database ID of the anime
     */
    private Long ensureAnimeExists(Long malId) {
        // Check if anime already exists
        return animeRepository.findByMalId(malId)
                .map(anime -> {
                    log.debug("Anime with MAL ID {} found with database ID {}", malId, anime.getId());
                    return anime.getId();
                })
                .orElseGet(() -> {
                    // Fetch from JIKAN API and save to database
                    try {
                        log.info("Anime with MAL ID {} not found, fetching from API...", malId);
                        animeSearchService.getAnimeByMalId(malId);

                        // Retrieve the saved anime to get its database ID
                        return animeRepository.findByMalId(malId)
                                .map(anime -> {
                                    log.info("Anime {} successfully saved with database ID {}", malId, anime.getId());
                                    return anime.getId();
                                })
                                .orElseThrow(() -> new RuntimeException("Failed to save anime: " + malId));
                    } catch (Exception e) {
                        log.error("Failed to fetch anime {}: {}", malId, e.getMessage(), e);
                        throw new RuntimeException("Anime not found or could not be fetched: " + malId, e);
                    }
                });
    }
}
