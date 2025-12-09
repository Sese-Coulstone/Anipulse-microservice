package com.anipulse.animeservice.controller;

import com.anipulse.animeservice.repository.UserAnimeListRepository;
import com.anipulse.animeservice.repository.UserAnimeRatingRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

/**
 * Controller for recommendation service to fetch training data
 * All endpoints require SERVICE role
 */
@RestController
@RequestMapping("/recommendation-data")
@RequiredArgsConstructor
@PreAuthorize("hasRole('SERVICE')")
public class RecommendationDataController {

    private final UserAnimeListRepository listRepository;
    private final UserAnimeRatingRepository ratingRepository;

    /**
     * Get recently updated user lists (for model training)
     */
    @GetMapping("/recent-lists")
    public ResponseEntity<?> getRecentLists(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime since) {

        return ResponseEntity.ok(listRepository.findRecentlyUpdated(since));
    }

    /**
     * Get recently added ratings (for model training)
     */
    @GetMapping("/recent-ratings")
    public ResponseEntity<?> getRecentRatings(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime since) {

        return ResponseEntity.ok(ratingRepository.findRecentlyRated(since));
    }

    /**
     * Get completed anime for specific user (for recommendations)
     */
    @GetMapping("/user/{userId}/completed")
    public ResponseEntity<?> getUserCompletedAnime(@PathVariable String userId) {
        return ResponseEntity.ok(listRepository.findCompletedAnimeByUserId(userId));
    }

    /**
     * Get user's rated anime (for collaborative filtering)
     */
//    @GetMapping("/user/{userId}/ratings")
//    public ResponseEntity<?> getUserRatings(@PathVariable String userId) {
//        return ResponseEntity.ok(ratingRepository.findByUserIdWithRatings(userId));
//    }
}
