package com.anipulse.animeservice.client;

import com.anipulse.sharedservice.dto.AuthResponse;
import feign.FeignException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.openfeign.FallbackFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class UserServiceClientFallback implements FallbackFactory<UserServiceClient> {

    @Override
    public UserServiceClient create(Throwable cause) {
        return new UserServiceClient() {
            @Override
            public ResponseEntity<AuthResponse> getUserProfile(String email) {
                if (cause instanceof FeignException.NotFound) {
                    log.warn("User not found with email: {}", email);
                    return ResponseEntity.notFound().build();
                }
                
                log.error("User service unavailable for email: {}. Error: {}", email, cause.getMessage());
                
                // Return a service unavailable response
                return ResponseEntity.status(503).build();
            }
        };
    }
}

