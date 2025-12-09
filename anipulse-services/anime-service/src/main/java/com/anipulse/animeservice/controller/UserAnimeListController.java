package com.anipulse.animeservice.controller;

import com.anipulse.animeservice.dto.UserAnimeListDTO;
import com.anipulse.animeservice.dto.UserAnimeListRequestDTO;
import com.anipulse.animeservice.dto.UserAnimeStatsDTO;
import com.anipulse.animeservice.entity.WatchStatus;
import com.anipulse.animeservice.service.UserAnimeListService;
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

import java.util.List;

@RestController
@RequestMapping("/my-list")
@RequiredArgsConstructor
public class UserAnimeListController {

    private final UserAnimeListService userAnimeListService;

    @PostMapping
    public ResponseEntity<UserAnimeListDTO> addOrUpdateAnime(
            @AuthenticationPrincipal Jwt jwt,
            @Valid @RequestBody UserAnimeListRequestDTO request) {
        String userId = jwt.getClaim("sub");
        UserAnimeListDTO response = userAnimeListService.createOrUpdateAnime(userId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    // Get user's anime list with pagination and sorting
    @GetMapping
    public ResponseEntity<Page<UserAnimeListDTO>> getUserAnimeList(
            @AuthenticationPrincipal Jwt jwt,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "updatedAt") String sortBy,
            @RequestParam(defaultValue = "DESC") Sort.Direction direction) {

        String userId = jwt.getClaim("sub");
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortBy));
        Page<UserAnimeListDTO> result = userAnimeListService.getUserAnimeList(userId, pageable);
        return ResponseEntity.ok(result);
    }

    // Get user's anime list filtered by watch status
    @GetMapping("/status/{status}")
    public ResponseEntity<Page<UserAnimeListDTO>> getUserAnimeListByStatus(
            @AuthenticationPrincipal Jwt jwt,
            @PathVariable WatchStatus status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {

        String userId = jwt.getClaim("sub");
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "updatedAt"));
        Page<UserAnimeListDTO> result = userAnimeListService.getUserAnimeListByStatus(userId, status, pageable);
        return ResponseEntity.ok(result);
    }

    // Get specific anime from user's list
    @GetMapping("/anime/{animeId}")
    public ResponseEntity<UserAnimeListDTO> getUserAnimeEntry(
            @AuthenticationPrincipal Jwt jwt,
            @PathVariable Long animeId) {

        String userId = jwt.getClaim("sub");
        UserAnimeListDTO result = userAnimeListService.getUserAnimeEntry(userId, animeId);

        if (result == null) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(result);
    }

    // Check if anime is in user's list
    @GetMapping("/anime/{animeId}/exists")
    public ResponseEntity<Boolean> isAnimeInList(
            @AuthenticationPrincipal Jwt jwt,
            @PathVariable Long animeId) {

        String userId = jwt.getClaim("sub");
        boolean exists = userAnimeListService.isAnimeInList(userId, animeId);
        return ResponseEntity.ok(exists);
    }

    // Remove anime from user's list
    @DeleteMapping("/anime/{animeId}")
    public ResponseEntity<Void> removeAnimeFromList(
            @AuthenticationPrincipal Jwt jwt,
            @PathVariable Long animeId) {

        String userId = jwt.getClaim("sub");
        userAnimeListService.removeAnimeFromList(userId, animeId);
        return ResponseEntity.noContent().build();
    }

    // Get user's anime statistics
    @GetMapping("/stats")
    public ResponseEntity<UserAnimeStatsDTO> getUserStatistics(
            @AuthenticationPrincipal Jwt jwt) {

        String userId = jwt.getClaim("sub");
        UserAnimeStatsDTO stats = userAnimeListService.getUserStatistics(userId);
        return ResponseEntity.ok(stats);
    }

    // Get anime IDs in user's list (for recommendations)
    @GetMapping("/anime-ids")
    public ResponseEntity<List<Long>> getUserAnimeIds(
            @AuthenticationPrincipal Jwt jwt) {

        String userId = jwt.getClaim("sub");
        List<Long> animeIds = userAnimeListService.getUserAnimeIds(userId);
        return ResponseEntity.ok(animeIds);
    }
    
}
