package com.anipulse.userservice.service;

import com.anipulse.userservice.dto.AuthRequest;
import com.anipulse.userservice.dto.AuthResponse;
import com.anipulse.userservice.entity.ActivationToken;
import com.anipulse.userservice.entity.Profile;
import com.anipulse.userservice.repository.ActivationTokenRepository;
import com.anipulse.userservice.repository.ProfileRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProfileService {
    private final ProfileRepository repository;
    private final KeycloakService keycloakService;
    private final ActivationTokenRepository activationTokenRepository;
    private final EmailService emailService;

    @Value("${app.activation-token-expiry}")
    private long tokenExpiryMillis;

    @Transactional
    public AuthResponse registerProfile(AuthRequest request) {
        // Check if email already exists
        if (repository.existsByEmail(request.getEmail())) {
            throw new UsernameNotFoundException("Email already exists");
        }

        if (keycloakService.userExistsInKeycloak(request.getEmail())) {
            throw new RuntimeException("User already exists in Keycloak");
        }

        try {
            // Create user in Keycloak
            String keycloakId = keycloakService.createUser(
                    request.getUsername(),
                    request.getEmail(),
                    request.getPassword()
            );

            // Create user in Database
            Profile profile = Profile.builder()
                    .keycloakId(keycloakId)
                    .username(request.getUsername())
                    .email(request.getEmail())
                    .isActive(false)
                    .emailVerified(false)
                    .build();

            repository.save(profile);

            // Generate activation token and send email
            String token = UUID.randomUUID().toString();
            ActivationToken activationToken = ActivationToken.builder()
                    .token(token)
                    .email(request.getEmail())
                    .expiryDate(LocalDateTime.now().plusSeconds(tokenExpiryMillis / 1000))
                    .used(false)
                    .build();
            activationTokenRepository.save(activationToken);

            emailService.sendActivationEmail(
                    request.getEmail(),
                    request.getUsername(),
                    token
            );
            log.info("User registered successfully: {}", request.getEmail());

            return convertToDto(profile);

        } catch (Exception e) {
            log.error("Error during registration: {}", e.getMessage(), e);
            throw new RuntimeException("Error during registration: " + e.getMessage());
        }
    }

    @Transactional
    public AuthResponse activateAccount(String token) {
        ActivationToken activationToken = activationTokenRepository.findByToken(token)
                .orElseThrow(() -> new RuntimeException("Invalid activation token"));

        if (activationToken.getUsed()) {
            throw new RuntimeException("Activation token already used");
        }

        if (activationToken.isExpired()) {
            throw new RuntimeException("Activation token has expired");
        }

        Profile profile = repository.findByEmail(activationToken.getEmail())
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        // Enable user in Keycloak
        keycloakService.enableUser(profile.getKeycloakId());

        // Update profile
        profile.setIsActive(true);
        profile.setEmailVerified(true);
        repository.save(profile);

        // Mark token as used
        activationToken.setUsed(true);
        activationTokenRepository.save(activationToken);

        log.info("Account activated successfully: {}", profile.getEmail());

        return convertToDto(profile);
    }

    public AuthResponse getProfile(String email) {
        Profile profile = repository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + email));
        return convertToDto(profile);
    }

    public Boolean userExists(String keycloakId) {
        return repository.existsByKeycloakId(keycloakId);
    }

    private AuthResponse convertToDto(Profile profile) {
        return AuthResponse.builder()
                .userId(profile.getId())
                .keycloakId(profile.getKeycloakId())
                .username(profile.getUsername())
                .email(profile.getEmail())
                .createdAt(profile.getCreatedAt())
                .updatedAt(profile.getUpdatedAt())
                .build();
    }
}
