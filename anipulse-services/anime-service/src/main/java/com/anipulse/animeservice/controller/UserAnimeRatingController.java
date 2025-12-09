package com.anipulse.animeservice.controller;

import com.anipulse.animeservice.dto.AnimeRatingStatsDTO;
import com.anipulse.animeservice.dto.UserAnimeRatingDTO;
import com.anipulse.animeservice.dto.UserAnimeRatingRequestDTO;
import com.anipulse.animeservice.service.UserAnimeRatingService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

/**
 * Controller for anime ratings management
 * User-specific endpoints require authentication
 */
@RestController
@RequestMapping("/ratings")
@RequiredArgsConstructor
public class UserAnimeRatingController {

    private final UserAnimeRatingService ratingService;

    // Add or update user's rating for anime (PROTECTED)
    @PostMapping
    public ResponseEntity<UserAnimeRatingDTO> addOrUpdateRating(
            @AuthenticationPrincipal Jwt jwt,
            @Valid @RequestBody UserAnimeRatingRequestDTO request) {

        String userId = jwt.getClaim("sub");
        UserAnimeRatingDTO result = ratingService.addOrUpdateRating(userId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(result);
    }

    // Get user's rating for specific anime (PROTECTED)
    @GetMapping("/my-rating/{animeId}")
    public ResponseEntity<UserAnimeRatingDTO> getMyRating(
            @AuthenticationPrincipal Jwt jwt,
            @PathVariable Long animeId) {

        String userId = jwt.getClaim("sub");
        UserAnimeRatingDTO result = ratingService.getUserRating(userId, animeId);

        if (result == null) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(result);
    }

    // Get all ratings by user (PROTECTED)
    @GetMapping("/my-ratings")
    public ResponseEntity<Page<UserAnimeRatingDTO>> getMyRatings(
            @AuthenticationPrincipal Jwt jwt,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {

        String userId = jwt.getClaim("sub");
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "ratedAt"));
        Page<UserAnimeRatingDTO> result = ratingService.getUserRatings(userId, pageable);
        return ResponseEntity.ok(result);
    }

    // Get all ratings for specific anime (PUBLIC)
    @GetMapping("/anime/{animeId}")
    public ResponseEntity<Page<UserAnimeRatingDTO>> getAnimeRatings(
            @PathVariable Long animeId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {

        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "ratedAt"));
        Page<UserAnimeRatingDTO> result = ratingService.getAnimeRatings(animeId, pageable);
        return ResponseEntity.ok(result);
    }

    // Get rating statistics for anime (PUBLIC)
    @GetMapping("/anime/{animeId}/stats")
    public ResponseEntity<AnimeRatingStatsDTO> getAnimeRatingStats(
            @PathVariable Long animeId) {

        AnimeRatingStatsDTO stats = ratingService.getAnimeRatingStats(animeId);
        return ResponseEntity.ok(stats);
    }

    // Delete user's rating (PROTECTED)
    @DeleteMapping("/{animeId}")
    public ResponseEntity<Void> deleteRating(
            @AuthenticationPrincipal Jwt jwt,
            @PathVariable Long animeId) {

        String userId = jwt.getClaim("sub");
        ratingService.deleteRating(userId, animeId);
        return ResponseEntity.noContent().build();
    }

    // Check if user has rated anime (PROTECTED)
    @GetMapping("/anime/{animeId}/exists")
    public ResponseEntity<Boolean> hasRated(
            @AuthenticationPrincipal Jwt jwt,
            @PathVariable Long animeId) {

        String userId = jwt.getClaim("sub");
        boolean hasRated = ratingService.hasRated(userId, animeId);
        return ResponseEntity.ok(hasRated);
    }
}
