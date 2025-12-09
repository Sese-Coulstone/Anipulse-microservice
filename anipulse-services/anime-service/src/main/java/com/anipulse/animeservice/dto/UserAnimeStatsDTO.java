package com.anipulse.animeservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserAnimeStatsDTO {
    private Long totalAnime;
    private Long watching;
    private Long completed;
    private Long onHold;
    private Long dropped;
    private Long planToWatch;
}
