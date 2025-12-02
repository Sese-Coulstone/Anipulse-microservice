package com.anipulse.animeservice.dto.jikan;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import java.time.LocalDate;
import java.util.List;

@Data
public class JikanAnimeData {
    @JsonProperty("mal_id")
    private Long malId;

    private String title;

    @JsonProperty("title_english")
    private String titleEnglish;

    private String synopsis;

    private List<JikanGenre> genres;

    private Integer episodes;

    private Double score;

    @JsonProperty("scored_by")
    private Integer scoredBy;

    private String type; // TV, Movie, OVA, etc.

    private String status; // Airing, Finished Airing, etc.

    private JikanAired aired;

    private JikanImages images;

    private String rating; // G, PG, PG-13, R, etc.

    private Integer rank;

    private Integer popularity;
}
