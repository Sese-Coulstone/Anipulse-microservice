package com.anipulse.animeservice.util;

import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.Refill;
import org.springframework.stereotype.Component;

import java.time.Duration;

@Component
public class JikanRateLimiter {

    private final Bucket bucket;

    public JikanRateLimiter() {
        // JIKAN API limit: 3 requests per second, 60 per minute
        Bandwidth limit = Bandwidth.classic(3, Refill.intervally(3, Duration.ofSeconds(1)));
        Bandwidth minuteLimit = Bandwidth.classic(60, Refill.intervally(60, Duration.ofMinutes(1)));

        this.bucket = Bucket.builder()
                .addLimit(limit)
                .addLimit(minuteLimit)
                .build();
    }

    public void acquire() throws InterruptedException {
        bucket.asBlocking().consume(1);
    }

    public boolean tryAcquire() {
        return bucket.tryConsume(1);
    }
}
