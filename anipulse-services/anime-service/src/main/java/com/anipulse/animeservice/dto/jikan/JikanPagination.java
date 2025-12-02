package com.anipulse.animeservice.dto.jikan;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class JikanPagination {
    @JsonProperty("last_visible_page")
    private Integer lastVisiblePage;

    @JsonProperty("has_next_page")
    private Boolean hasNextPage;

    @JsonProperty("current_page")
    private Integer currentPage;

    private JikanPaginationItems items;

    @Data
    public static class JikanPaginationItems {
        private Integer count;
        private Integer total;
        private Integer perPage;
    }
}
