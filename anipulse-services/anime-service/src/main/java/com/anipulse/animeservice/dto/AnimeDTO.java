package com.anipulse.animeservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.Set;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AnimeDTO {
    private Long id;
    private Long malId;
    private String title;
    private String titleEnglish;
    private String synopsis;
    private Integer episodes;
    private Double score;
    private Integer scoredBy;
    private String type;
    private String status;
    private LocalDate airedFrom;
    private LocalDate airedTo;
    private String imageUrl;
    private String rating;
    private Integer rank;
    private Integer popularity;
    private Set<String> genres;
}
