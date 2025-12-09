package com.anipulse.animeservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserAnimeRatingDTO {
    private String userId;
    private Long animeId;

    // Embedded anime info
    private String animeTitle;
    private String animeImageUrl;

    private Integer storyRating;
    private Integer animationRating;
    private Integer characterRating;
    private Integer overallRating;
    private String reviewText;

    private LocalDateTime createdAt;
    private LocalDateTime ratedAt;
}
