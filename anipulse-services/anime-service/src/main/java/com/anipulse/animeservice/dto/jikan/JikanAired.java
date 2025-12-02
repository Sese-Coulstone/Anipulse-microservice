package com.anipulse.animeservice.dto.jikan;

import lombok.Data;
import java.time.LocalDate;

@Data
public class JikanAired {
    private LocalDate from;
    private LocalDate to;
}
