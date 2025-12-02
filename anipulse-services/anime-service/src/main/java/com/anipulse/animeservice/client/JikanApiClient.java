package com.anipulse.animeservice.client;

import com.anipulse.animeservice.config.JikanProperties;
import com.anipulse.animeservice.dto.jikan.JikanAnimeResponse;
import com.anipulse.animeservice.dto.jikan.JikanSearchResponse;
import com.anipulse.animeservice.util.JikanRateLimiter;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
@Slf4j
public class JikanApiClient {

    private final WebClient jikanWebClient;
    private final JikanRateLimiter rateLimiter;
    private final JikanProperties jikanProperties;

    @CircuitBreaker(name = "jikan-api", fallbackMethod = "getAnimeByIdFallback")
    @Retry(name = "jikan-api")
    public Mono<JikanAnimeResponse> getAnimeById(Long malId) throws InterruptedException {
        rateLimiter.acquire();
        log.info("Fetching anime with MAL ID: {}", malId);

        return jikanWebClient.get()
                .uri("/anime/{id}", malId)
                .retrieve()
                .bodyToMono(JikanAnimeResponse.class)
                .doOnError(error -> log.error("Error fetching anime {}: {}", malId, error.getMessage()));
    }

    @CircuitBreaker(name = "jikan-api", fallbackMethod = "searchAnimeFallback")
    @Retry(name = "jikan-api")
    public Mono<JikanSearchResponse> searchAnime(String query, int page) throws InterruptedException {
        rateLimiter.acquire();
        log.info("Searching anime with query: '{}', page: {}", query, page);

        return jikanWebClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/anime")
                        .queryParam("q", query)
                        .queryParam("page", page)
                        .queryParam("limit", 25)
                        .build())
                .retrieve()
                .bodyToMono(JikanSearchResponse.class)
                .doOnError(error -> log.error("Error searching anime: {}", error.getMessage()));
    }

    @CircuitBreaker(name = "jikan-api", fallbackMethod = "getTopAnimeFallback")
    @Retry(name = "jikan-api")
    public Mono<JikanSearchResponse> getTopAnime(String type, int page) throws InterruptedException {
        rateLimiter.acquire();
        log.info("Fetching top anime, type: {}, page: {}", type, page);

        return jikanWebClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/top/anime")
                        .queryParam("type", type)
                        .queryParam("page", page)
                        .queryParam("limit", 25)
                        .build())
                .retrieve()
                .bodyToMono(JikanSearchResponse.class);
    }

    @CircuitBreaker(name = "jikan-api", fallbackMethod = "getSeasonalAnimeFallback")
    @Retry(name = "jikan-api")
    public Mono<JikanSearchResponse> getSeasonalAnime(String season, int year, int page) throws InterruptedException {
        rateLimiter.acquire();
        log.info("Fetching seasonal anime: {} {}, page: {}", season, year, page);

        return jikanWebClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/seasons/{year}/{season}")
                        .queryParam("page", page)
                        .build(year, season))
                .retrieve()
                .bodyToMono(JikanSearchResponse.class);
    }

    // Fallback methods
    private Mono<JikanAnimeResponse> getAnimeByIdFallback(Long malId, Exception ex) {
        log.warn("Fallback triggered for getAnimeById({}): {}", malId, ex.getMessage());
        return Mono.empty();
    }

    private Mono<JikanSearchResponse> searchAnimeFallback(String query, int page, Exception ex) {
        log.warn("Fallback triggered for searchAnime: {}", ex.getMessage());
        return Mono.just(new JikanSearchResponse());
    }

    private Mono<JikanSearchResponse> getTopAnimeFallback(String type, int page, Exception ex) {
        log.warn("Fallback triggered for getTopAnime: {}", ex.getMessage());
        return Mono.just(new JikanSearchResponse());
    }

    private Mono<JikanSearchResponse> getSeasonalAnimeFallback(String season, int year, int page, Exception ex) {
        log.warn("Fallback triggered for getSeasonalAnime: {}", ex.getMessage());
        return Mono.just(new JikanSearchResponse());
    }
}
