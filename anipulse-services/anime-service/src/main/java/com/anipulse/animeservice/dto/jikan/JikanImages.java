package com.anipulse.animeservice.dto.jikan;

import lombok.Data;

@Data
public class JikanImages {
    private JikanImageType jpg;
    private JikanImageType webp;

    @Data
    public static class JikanImageType {
        private String imageUrl;
        private String smallImageUrl;
        private String largeImageUrl;
    }
}
