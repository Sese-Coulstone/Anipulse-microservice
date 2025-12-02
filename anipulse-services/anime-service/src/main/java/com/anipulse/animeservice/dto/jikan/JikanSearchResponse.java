package com.anipulse.animeservice.dto.jikan;

import lombok.Data;
import java.util.List;

@Data
public class JikanSearchResponse {
    private List<JikanAnimeData> data;
    private JikanPagination pagination;
}
