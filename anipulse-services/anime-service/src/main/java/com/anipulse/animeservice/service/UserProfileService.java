package com.anipulse.animeservice.service;

import com.anipulse.animeservice.client.UserServiceClient;
import com.anipulse.sharedservice.dto.AuthResponse;
import feign.FeignException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserProfileService {

    private final UserServiceClient userServiceClient;

    /**
     * Fetches user profile with Redis caching (1-hour TTL)
     * @param email User's email
     * @return Optional containing user profile if found, empty otherwise
     */
    @Cacheable(value = "user-profiles", key = "#email", unless = "#result == null || #result.isEmpty()")
    public Optional<AuthResponse> getUserProfile(String email) {
        try {
            log.debug("Fetching user profile for email: {}", email);
            ResponseEntity<AuthResponse> response = userServiceClient.getUserProfile(email);
            
            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                log.info("Successfully fetched user profile for email: {}", email);
                return Optional.of(response.getBody());
            }
            
            log.warn("User profile not found for email: {}", email);
            return Optional.empty();
        } catch (FeignException.NotFound e) {
            log.warn("User not found for email: {}", email);
            return Optional.empty();
        } catch (Exception e) {
            log.error("Error fetching user profile for email: {}. Error: {}", email, e.getMessage());
            return Optional.empty();
        }
    }

    /**
     * Validates if a user exists by email
     * @param email User's email
     * @return true if user exists, false otherwise
     */
    public boolean userExists(String email) {
        return getUserProfile(email).isPresent();
    }

    /**
     * Gets user ID by email
     * @param email User's email
     * @return User ID if found, null otherwise
     */
    public String getUserId(String email) {
        return getUserProfile(email)
                .map(AuthResponse::getUserId)
                .orElse(null);
    }
}

