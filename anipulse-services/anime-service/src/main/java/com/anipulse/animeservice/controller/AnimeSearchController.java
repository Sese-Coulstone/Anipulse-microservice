package com.anipulse.animeservice.controller;

import com.anipulse.animeservice.service.AnimeSearchService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class AnimeSearchController {

    private final AnimeSearchService animeSearchService;

    @GetMapping("/search")
    public ResponseEntity<?> searchAnime(@RequestParam String query,
                                                            @RequestParam(defaultValue = "1") int page) throws InterruptedException {
        return ResponseEntity.ok(animeSearchService.searchAnime(query, page));
    }

    @GetMapping("/{malId}")
    public ResponseEntity<?> getAnimeByMalId(@PathVariable Long malId) throws InterruptedException {
        return ResponseEntity.ok(animeSearchService.getAnimeByMalId(malId));
    }

    @GetMapping("/top")
    public ResponseEntity<?> getTopAnime(@RequestParam(defaultValue = "tv") String type,
                                         @RequestParam(defaultValue = "1") int page) throws InterruptedException {
        return ResponseEntity.ok(animeSearchService.getTopAnime(type, page));
    }

    @GetMapping("/seasonal")
    public ResponseEntity<?> getSeasonalAnime(@RequestParam String season,
                                              @RequestParam int year,
                                              @RequestParam(defaultValue = "1") int page) throws InterruptedException {
        return ResponseEntity.ok(animeSearchService.getSeasonalAnime(season, year, page));
    }
}
