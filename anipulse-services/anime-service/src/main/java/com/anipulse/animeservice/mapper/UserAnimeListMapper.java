package com.anipulse.animeservice.mapper;

import com.anipulse.animeservice.dto.UserAnimeListDTO;
import com.anipulse.animeservice.entity.UserAnimeList;
import org.springframework.stereotype.Component;

@Component
public class UserAnimeListMapper {

    public UserAnimeListDTO toDTO(UserAnimeList entity) {
        if (entity == null) {
            return null;
        }

        UserAnimeListDTO.UserAnimeListDTOBuilder builder = UserAnimeListDTO.builder()
                .userId(entity.getUserId())
                .animeId(entity.getAnimeId())
                .watchStatus(entity.getWatchStatus())
                .progress(entity.getProgress())
                .rating(entity.getRating())
                .notes(entity.getNotes())
                .addedAt(entity.getAddedAt())
                .updatedAt(entity.getUpdatedAt())
                .completedAt(entity.getCompletedAt());

        // Add anime info if available
        if (entity.getAnime() != null) {
            builder.animeTitle(entity.getAnime().getTitle())
                    .animeTitleEnglish(entity.getAnime().getTitleEnglish())
                    .animeImageUrl(entity.getAnime().getImageUrl())
                    .totalEpisodes(entity.getAnime().getEpisodes());
        }

        return builder.build();
    }
}
