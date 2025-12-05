package com.anipulse.animeservice.controller;

import com.anipulse.animeservice.dto.AnimeDTO;
import com.anipulse.animeservice.service.AnimeSearchService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class AnimeSearchController {

    private final AnimeSearchService animeSearchService;

    @GetMapping("/{malId}")
    public ResponseEntity<AnimeDTO> getAnimeByMalId(@PathVariable Long malId) throws InterruptedException {
        AnimeDTO animeDTO = animeSearchService.getAnimeByMalId(malId);
        return ResponseEntity.ok(animeDTO);
    }
}
