package com.anipulse.animeservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AnimeSearchResultDTO {
    private List<AnimeDTO> data;
    private PaginationDTO pagination;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PaginationDTO {
        private Integer currentPage;
        private Integer lastPage;
        private Boolean hasNextPage;
        private Integer totalItems;
    }
}

