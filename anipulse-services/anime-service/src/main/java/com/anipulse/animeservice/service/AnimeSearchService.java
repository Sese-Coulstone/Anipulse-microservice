package com.anipulse.animeservice.service;

import com.anipulse.animeservice.client.JikanApiClient;
import com.anipulse.animeservice.dto.AnimeDTO;
import com.anipulse.animeservice.dto.AnimeSearchResultDTO;
import com.anipulse.animeservice.dto.jikan.JikanAnimeData;
import com.anipulse.animeservice.dto.jikan.JikanAnimeResponse;
import com.anipulse.animeservice.dto.jikan.JikanSearchResponse;
import com.anipulse.animeservice.entity.Anime;
import com.anipulse.animeservice.entity.AnimeGenre;
import com.anipulse.animeservice.mapper.AnimeMapper;
import com.anipulse.animeservice.repository.AnimeGenreRepository;
import com.anipulse.animeservice.repository.AnimeRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class AnimeSearchService {

    private final JikanApiClient jikanApiClient;
    private final AnimeRepository animeRepository;
    private final AnimeGenreRepository genreRepository;
    private final AnimeMapper animeMapper;

    /**
     * Get anime by MAL ID with caching and database persistence
     */
    @Cacheable(value = "anime", key = "#malId")
    public AnimeDTO getAnimeByMalId(Long malId) throws InterruptedException {
        log.info("Fetching anime with MAL ID: {}", malId);

        // Fetch from JIKAN if not in database
        return fetchAndPersistAnime(malId);
    }

    /**
     * Search anime with caching
     */
    @Cacheable(value = "animeSearch", key = "#query + '_' + #page")
    public AnimeSearchResultDTO searchAnime(String query, int page) throws InterruptedException {
        log.info("Searching anime with query: '{}', page: {}", query, page);

        JikanSearchResponse response = jikanApiClient.searchAnime(query, page)
                .block();

        if (response == null || response.getData() == null) {
            return AnimeSearchResultDTO.builder()
                    .data(List.of())
                    .pagination(AnimeSearchResultDTO.PaginationDTO.builder()
                            .currentPage(page)
                            .hasNextPage(false)
                            .build())
                    .build();
        }

        List<AnimeDTO> animeList = response.getData().stream()
                .map(animeMapper::jikanToDTO)
                .collect(Collectors.toList());

        AnimeSearchResultDTO.PaginationDTO pagination = AnimeSearchResultDTO.PaginationDTO.builder()
                .currentPage(page)
                .lastPage(response.getPagination() != null ?
                        response.getPagination().getLastVisiblePage() : 1)
                .hasNextPage(response.getPagination() != null &&
                        response.getPagination().getHasNextPage())
                .totalItems(response.getPagination() != null &&
                        response.getPagination().getItems() != null ?
                        response.getPagination().getItems().getTotal() : 0)
                .build();

        return AnimeSearchResultDTO.builder()
                .data(animeList)
                .pagination(pagination)
                .build();
    }

    /**
     * Get top anime with caching
     */
    @Cacheable(value = "topAnime", key = "#type + '_' + #page")
    public AnimeSearchResultDTO getTopAnime(String type, int page) throws InterruptedException {
        log.info("Fetching top anime, type: {}, page: {}", type, page);

        JikanSearchResponse response = jikanApiClient.getTopAnime(type, page)
                .block();

        return buildSearchResult(response, page);
    }

    /**
     * Get seasonal anime with caching
     */
    @Cacheable(value = "seasonalAnime", key = "#season + '_' + #year + '_' + #page")
    public AnimeSearchResultDTO getSeasonalAnime(String season, int year, int page) throws InterruptedException {
        log.info("Fetching seasonal anime: {} {}, page: {}", season, year, page);

        JikanSearchResponse response = jikanApiClient.getSeasonalAnime(season, year, page)
                .block();

        return buildSearchResult(response, page);
    }

    // Private helper methods

    @Transactional
    protected AnimeDTO fetchAndPersistAnime(Long malId) throws InterruptedException {
        JikanAnimeResponse response = jikanApiClient.getAnimeById(malId)
                .block();

        if (response == null || response.getData() == null) {
            throw new RuntimeException("Anime not found: " + malId);
        }

        Anime anime = animeMapper.jikanToEntity(response.getData());

        // Process genres
        Set<AnimeGenre> genres = processGenres(response.getData().getGenres());
        anime.setGenres(genres);

        return animeMapper.entityToDTO(anime);
    }

    @Transactional
    protected AnimeDTO refreshAnimeData(Anime anime) throws InterruptedException {
        JikanAnimeResponse response = jikanApiClient.getAnimeById(anime.getMalId())
                .block();

        if (response != null && response.getData() != null) {
            animeMapper.updateEntityFromJikan(anime, response.getData());

            // Update genres
            Set<AnimeGenre> genres = processGenres(response.getData().getGenres());
            anime.getGenres().clear();
            anime.getGenres().addAll(genres);

            anime = animeRepository.save(anime);
            log.info("Refreshed anime data: {}", anime.getMalId());
        }

        return animeMapper.entityToDTO(anime);
    }

    @Transactional
    protected Set<AnimeGenre> processGenres(List<com.anipulse.animeservice.dto.jikan.JikanGenre> jikanGenres) {
        if (jikanGenres == null) {
            return new HashSet<>();
        }

        return jikanGenres.stream()
                .map(jg -> genreRepository.findByMalGenreId(jg.getMalId())
                        .orElseGet(() -> {
                            AnimeGenre newGenre = AnimeGenre.builder()
                                    .malGenreId(jg.getMalId())
                                    .name(jg.getName())
                                    .type(jg.getType())
                                    .build();
                            return genreRepository.save(newGenre);
                        }))
                .collect(Collectors.toSet());
    }

    private AnimeSearchResultDTO buildSearchResult(JikanSearchResponse response, int page) {
        if (response == null || response.getData() == null) {
            return AnimeSearchResultDTO.builder()
                    .data(List.of())
                    .pagination(AnimeSearchResultDTO.PaginationDTO.builder()
                            .currentPage(page)
                            .hasNextPage(false)
                            .build())
                    .build();
        }

        List<AnimeDTO> animeList = response.getData().stream()
                .map(animeMapper::jikanToDTO)
                .collect(Collectors.toList());

        AnimeSearchResultDTO.PaginationDTO pagination = AnimeSearchResultDTO.PaginationDTO.builder()
                .currentPage(page)
                .lastPage(response.getPagination() != null ?
                        response.getPagination().getLastVisiblePage() : 1)
                .hasNextPage(response.getPagination() != null &&
                        response.getPagination().getHasNextPage())
                .totalItems(response.getPagination() != null &&
                        response.getPagination().getItems() != null ?
                        response.getPagination().getItems().getTotal() : 0)
                .build();

        return AnimeSearchResultDTO.builder()
                .data(animeList)
                .pagination(pagination)
                .build();
    }
}
