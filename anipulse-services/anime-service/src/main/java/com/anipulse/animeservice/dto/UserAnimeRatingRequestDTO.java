package com.anipulse.animeservice.dto;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserAnimeRatingRequestDTO {

    @NotNull(message = "Anime ID is required")
    private Long animeId;

    @NotNull(message = "Story rating is required")
    @Min(value = 1, message = "Story rating must be between 1 and 10")
    @Max(value = 10, message = "Story rating must be between 1 and 10")
    private Integer storyRating;

    @NotNull(message = "Animation rating is required")
    @Min(value = 1, message = "Animation rating must be between 1 and 10")
    @Max(value = 10, message = "Animation rating must be between 1 and 10")
    private Integer animationRating;

    @NotNull(message = "Character rating is required")
    @Min(value = 1, message = "Character rating must be between 1 and 10")
    @Max(value = 10, message = "Character rating must be between 1 and 10")
    private Integer characterRating;

    @NotNull(message = "Overall rating is required")
    @Min(value = 1, message = "Overall rating must be between 1 and 10")
    @Max(value = 10, message = "Overall rating must be between 1 and 10")
    private Integer overallRating;

    @Size(max = 10000, message = "Review cannot exceed 10000 characters")
    private String reviewText;
}
