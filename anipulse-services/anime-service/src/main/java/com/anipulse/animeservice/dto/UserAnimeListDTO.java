package com.anipulse.animeservice.dto;

import com.anipulse.animeservice.entity.WatchStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserAnimeListDTO {
    private String userId;
    private Long animeId;

    // Embedded anime info (for convenience)
    private String animeTitle;
    private String animeTitleEnglish;
    private String animeImageUrl;
    private Integer totalEpisodes;

    private WatchStatus watchStatus;
    private Integer progress;
    private Double rating;
    private String notes;

    private LocalDateTime addedAt;
    private LocalDateTime updatedAt;
    private LocalDateTime completedAt;
}
