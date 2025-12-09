package com.anipulse.animeservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AnimeRatingStatsDTO {
    private Long animeId;
    private String animeTitle;

    private Double averageOverallRating;
    private Double averageStoryRating;
    private Double averageAnimationRating;
    private Double averageCharacterRating;

    private Long totalRatings;
}
