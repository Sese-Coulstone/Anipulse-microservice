package com.anipulse.animeservice.dto.jikan;

import lombok.Data;
import java.time.OffsetDateTime;

@Data
public class JikanAired {
    private OffsetDateTime from;  // Can parse "2005-01-07T00:00:00+00:00"
    private OffsetDateTime to;
}
