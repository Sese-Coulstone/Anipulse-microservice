package com.anipulse.animeservice.dto;

import com.anipulse.animeservice.entity.WatchStatus;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserAnimeListRequestDTO {

    @NotNull(message = "MAL ID is required")
    private Long malId;

    @NotNull(message = "Watch status is required")
    private WatchStatus watchStatus;

    @Min(value = 0, message = "Progress cannot be negative")
    private Integer progress;

    @DecimalMin(value = "1.0", message = "Rating must be between 1.0 and 10.0")
    @DecimalMax(value = "10.0", message = "Rating must be between 1.0 and 10.0")
    private Double rating;

    @Size(max = 5000, message = "Notes cannot exceed 5000 characters")
    private String notes;
}
