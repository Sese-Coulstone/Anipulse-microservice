package com.anipulse.animeservice.service;

import com.anipulse.animeservice.dto.AnimeRatingStatsDTO;
import com.anipulse.animeservice.dto.UserAnimeRatingDTO;
import com.anipulse.animeservice.dto.UserAnimeRatingRequestDTO;
import com.anipulse.animeservice.entity.UserAnimeRating;
import com.anipulse.animeservice.mapper.UserAnimeRatingMapper;
import com.anipulse.animeservice.repository.AnimeRepository;
import com.anipulse.animeservice.repository.UserAnimeRatingRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service for managing detailed anime ratings
 * Provides granular rating data for recommendation engine
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class UserAnimeRatingService {

    private final UserAnimeRatingRepository ratingRepository;
    private final AnimeRepository animeRepository;
    private final UserAnimeRatingMapper mapper;
    private final AnimeSearchService animeSearchService;

    // Add or update user's rating for anime
    @Transactional
    @CacheEvict(value = {"userRatings", "animeRatingStats"}, key = "#userId")
    public UserAnimeRatingDTO addOrUpdateRating(String userId, UserAnimeRatingRequestDTO request) {
        log.info("Adding/updating rating for anime {} by user {}", request.getAnimeId(), userId);

        // Ensure anime exists
        ensureAnimeExists(request.getAnimeId());

        UserAnimeRating rating = ratingRepository
                .findByUserIdAndAnimeId(userId, request.getAnimeId())
                .orElse(UserAnimeRating.builder()
                        .userId(userId)
                        .animeId(request.getAnimeId())
                        .build());

        rating.setStoryRating(request.getStoryRating());
        rating.setAnimationRating(request.getAnimationRating());
        rating.setCharacterRating(request.getCharacterRating());
        rating.setOverallRating(request.getOverallRating());
        rating.setReviewText(request.getReviewText());

        rating = ratingRepository.save(rating);
        log.info("Successfully saved rating for anime {} by user {}", request.getAnimeId(), userId);

        return mapper.toDTO(rating);
    }


    //Get user's rating for specific anime
    @Transactional(readOnly = true)
    public UserAnimeRatingDTO getUserRating(String userId, Long animeId) {
        log.debug("Fetching rating for anime {} by user {}", animeId, userId);

        return ratingRepository.findByUserIdAndAnimeId(userId, animeId)
                .map(mapper::toDTO)
                .orElse(null);
    }

    // Get all ratings by user with pagination
    @Transactional(readOnly = true)
    @Cacheable(value = "userRatings", key = "#userId + '_' + #pageable.pageNumber")
    public Page<UserAnimeRatingDTO> getUserRatings(String userId, Pageable pageable) {
        log.debug("Fetching ratings for user {}, page {}", userId, pageable.getPageNumber());

        return ratingRepository.findByUserId(userId, pageable)
                .map(mapper::toDTO);
    }

    // Get all ratings for specific anime with pagination
    @Transactional(readOnly = true)
    @Cacheable(value = "animeRatings", key = "#animeId + '_' + #pageable.pageNumber")
    public Page<UserAnimeRatingDTO> getAnimeRatings(Long animeId, Pageable pageable) {
        log.debug("Fetching ratings for anime {}, page {}", animeId, pageable.getPageNumber());

        return ratingRepository.findByAnimeId(animeId, pageable)
                .map(mapper::toDTO);
    }

    // Get rating statistics for anime
    @Transactional(readOnly = true)
    @Cacheable(value = "animeRatingStats", key = "#animeId")
    public AnimeRatingStatsDTO getAnimeRatingStats(Long animeId) {
        log.debug("Calculating rating statistics for anime {}", animeId);

        return AnimeRatingStatsDTO.builder()
                .animeId(animeId)
                .averageOverallRating(ratingRepository.getAverageOverallRating(animeId))
                .averageStoryRating(ratingRepository.getAverageStoryRating(animeId))
                .averageAnimationRating(ratingRepository.getAverageAnimationRating(animeId))
                .averageCharacterRating(ratingRepository.getAverageCharacterRating(animeId))
                .totalRatings(ratingRepository.countByAnimeId(animeId))
                .build();
    }

    // Delete user's rating
    @Transactional
    @CacheEvict(value = {"userRatings", "animeRatingStats"}, key = "#userId")
    public void deleteRating(String userId, Long animeId) {
        log.info("Deleting rating for anime {} by user {}", animeId, userId);
        ratingRepository.deleteByUserIdAndAnimeId(userId, animeId);
    }

    // Check if user has rated anime
    @Transactional(readOnly = true)
    public boolean hasRated(String userId, Long animeId) {
        return ratingRepository.existsByUserIdAndAnimeId(userId, animeId);
    }

    //  Ensure anime exists
    private void ensureAnimeExists(Long animeId) {
        if (!animeRepository.existsByMalId(animeId)) {
            try {
                animeSearchService.getAnimeByMalId(animeId);
            } catch (Exception e) {
                log.error("Failed to fetch anime {}: {}", animeId, e.getMessage());
                throw new RuntimeException("Anime not found: " + animeId);
            }
        }
    }
}
