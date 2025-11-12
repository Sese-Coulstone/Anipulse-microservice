package com.anipulse.userservice.controller;

import com.anipulse.userservice.dto.AuthRequest;
import com.anipulse.userservice.dto.AuthResponse;
import com.anipulse.userservice.service.ProfileService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class ProfileController {
    private final ProfileService profileService;

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@RequestBody AuthRequest request) {
        AuthResponse response = profileService.registerProfile(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}
