package com.anipulse.animeservice.mapper;

import com.anipulse.animeservice.dto.UserAnimeRatingDTO;
import com.anipulse.animeservice.entity.UserAnimeRating;
import org.springframework.stereotype.Component;

@Component
public class UserAnimeRatingMapper {

    public UserAnimeRatingDTO toDTO(UserAnimeRating entity) {
        if (entity == null) {
            return null;
        }

        UserAnimeRatingDTO.UserAnimeRatingDTOBuilder builder = UserAnimeRatingDTO.builder()
                .userId(entity.getUserId())
                .animeId(entity.getAnimeId())
                .storyRating(entity.getStoryRating())
                .animationRating(entity.getAnimationRating())
                .characterRating(entity.getCharacterRating())
                .overallRating(entity.getOverallRating())
                .reviewText(entity.getReviewText())
                .createdAt(entity.getCreatedAt())
                .ratedAt(entity.getRatedAt());

        // Add anime info if available
        if (entity.getAnime() != null) {
            builder.animeTitle(entity.getAnime().getTitle())
                    .animeImageUrl(entity.getAnime().getImageUrl());
        }

        return builder.build();
    }
}
