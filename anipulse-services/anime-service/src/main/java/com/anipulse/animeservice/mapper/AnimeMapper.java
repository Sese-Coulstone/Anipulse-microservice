package com.anipulse.animeservice.mapper;

import com.anipulse.animeservice.dto.AnimeDTO;
import com.anipulse.animeservice.dto.jikan.JikanAnimeData;
import com.anipulse.animeservice.entity.Anime;
import com.anipulse.animeservice.entity.AnimeGenre;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class AnimeMapper {

    public Anime jikanToEntity(JikanAnimeData jikanData) {
        Anime anime = Anime.builder()
                .malId(jikanData.getMalId())
                .title(jikanData.getTitle())
                .titleEnglish(jikanData.getTitleEnglish())
                .synopsis(jikanData.getSynopsis())
                .episodes(jikanData.getEpisodes())
                .score(jikanData.getScore())
                .scoredBy(jikanData.getScoredBy())
                .type(jikanData.getType())
                .status(jikanData.getStatus())
                .rating(jikanData.getRating())
                .animeRank(jikanData.getRank())
                .popularity(jikanData.getPopularity())
                .lastSyncedAt(LocalDateTime.now())
                .build();

        if (jikanData.getAired() != null) {
            anime.setAiredFrom(jikanData.getAired().getFrom().toLocalDate());
            anime.setAiredTo(jikanData.getAired().getTo().toLocalDate());
        }

        if (jikanData.getImages() != null && jikanData.getImages().getJpg() != null) {
            anime.setImageUrl(jikanData.getImages().getJpg().getImageUrl());
        }

        return anime;
    }

    public void updateEntityFromJikan(Anime anime, JikanAnimeData jikanData) {
        anime.setTitle(jikanData.getTitle());
        anime.setTitleEnglish(jikanData.getTitleEnglish());
        anime.setSynopsis(jikanData.getSynopsis());
        anime.setEpisodes(jikanData.getEpisodes());
        anime.setScore(jikanData.getScore());
        anime.setScoredBy(jikanData.getScoredBy());
        anime.setType(jikanData.getType());
        anime.setStatus(jikanData.getStatus());
        anime.setRating(jikanData.getRating());
        anime.setAnimeRank(jikanData.getRank());
        anime.setPopularity(jikanData.getPopularity());
        anime.setLastSyncedAt(LocalDateTime.now());

        if (jikanData.getAired() != null) {
            if (jikanData.getAired().getFrom() != null) {
                anime.setAiredFrom(jikanData.getAired().getFrom().toLocalDate());
            }
            if (jikanData.getAired().getTo() != null) {
                anime.setAiredTo(jikanData.getAired().getTo().toLocalDate());
            }
        }

        if (jikanData.getImages() != null && jikanData.getImages().getJpg() != null) {
            anime.setImageUrl(jikanData.getImages().getJpg().getImageUrl());
        }
    }

    public AnimeDTO entityToDTO(Anime anime) {
        return AnimeDTO.builder()
                .id(anime.getId())
                .malId(anime.getMalId())
                .title(anime.getTitle())
                .titleEnglish(anime.getTitleEnglish())
                .synopsis(anime.getSynopsis())
                .episodes(anime.getEpisodes())
                .score(anime.getScore())
                .scoredBy(anime.getScoredBy())
                .type(anime.getType())
                .status(anime.getStatus())
                .airedFrom(anime.getAiredFrom())
                .airedTo(anime.getAiredTo())
                .imageUrl(anime.getImageUrl())
                .rating(anime.getRating())
                .rank(anime.getAnimeRank())
                .popularity(anime.getPopularity())
                .genres(anime.getGenres().stream()
                        .map(AnimeGenre::getName)
                        .collect(Collectors.toSet()))
                .build();
    }

    public AnimeDTO jikanToDTO(JikanAnimeData jikanData) {
        AnimeDTO.AnimeDTOBuilder builder = AnimeDTO.builder()
                .malId(jikanData.getMalId())
                .title(jikanData.getTitle())
                .titleEnglish(jikanData.getTitleEnglish())
                .synopsis(jikanData.getSynopsis())
                .episodes(jikanData.getEpisodes())
                .score(jikanData.getScore())
                .scoredBy(jikanData.getScoredBy())
                .type(jikanData.getType())
                .status(jikanData.getStatus())
                .rating(jikanData.getRating())
                .rank(jikanData.getRank())
                .popularity(jikanData.getPopularity());

        if (jikanData.getAired() != null) {
            builder.airedFrom(jikanData.getAired().getFrom().toLocalDate())
                    .airedTo(jikanData.getAired().getTo().toLocalDate());
        }

        if (jikanData.getImages() != null && jikanData.getImages().getJpg() != null) {
            builder.imageUrl(jikanData.getImages().getJpg().getImageUrl());
        }

        if (jikanData.getGenres() != null) {
            builder.genres(jikanData.getGenres().stream()
                    .map(com.anipulse.animeservice.dto.jikan.JikanGenre::getName)
                    .collect(Collectors.toSet()));
        }

        return builder.build();
    }
}
