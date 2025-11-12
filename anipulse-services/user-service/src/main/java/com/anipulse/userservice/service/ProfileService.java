package com.anipulse.userservice.service;

import com.anipulse.userservice.dto.AuthRequest;
import com.anipulse.userservice.dto.AuthResponse;
import com.anipulse.userservice.entity.Profile;
import com.anipulse.userservice.repository.ProfileRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ProfileService {
    private final ProfileRepository repository;

    public AuthResponse registerProfile(AuthRequest request) {
        Profile newProfile = convertToEntity(request);
        repository.save(newProfile);
        return convertToDto(newProfile);
    }

    private Profile convertToEntity(AuthRequest request) {
        return Profile.builder()
                .username(request.getUsername())
                .email(request.getEmail())
                .password(request.getPassword())
                .build();
    }

    private AuthResponse convertToDto(Profile profile) {
        return AuthResponse.builder()
                .userId(profile.getId())
                .username(profile.getUsername())
                .email(profile.getEmail())
                .createdAt(profile.getCreatedAt())
                .updatedAt(profile.getUpdatedAt())
                .build();
    }
}
