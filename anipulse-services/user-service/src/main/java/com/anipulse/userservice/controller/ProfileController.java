package com.anipulse.userservice.controller;

import com.anipulse.userservice.dto.AuthRequest;
import com.anipulse.userservice.dto.AuthResponse;
import com.anipulse.userservice.service.ProfileService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequiredArgsConstructor
public class ProfileController {
    private final ProfileService profileService;

    @PostMapping("/register")
    public ResponseEntity<Map<String, Object>> register(@Valid @RequestBody AuthRequest request) {
        try {
            AuthResponse response = profileService.registerProfile(request);
            return ResponseEntity.status(HttpStatus.CREATED).body(Map.of(
                    "success", true,
                    "message", "Registration successful. Please check your email for activation link.",
                    "data", response
            ));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of(
                    "success", false,
                    "message", e.getMessage()
            ));
        }
    }

    @GetMapping("/activate/{token}")
    public ResponseEntity<Map<String, Object>> activateAccount(@PathVariable String token) {
        try {
            AuthResponse response = profileService.activateAccount(token);
            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "Account activated successfully!",
                    "data", response
            ));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of(
                    "success", false,
                    "message", e.getMessage()
            ));
        }
    }

    @GetMapping("/{email}")
    public ResponseEntity<AuthResponse> getProfile(@PathVariable String email) {
        AuthResponse response = profileService.getProfile(email);
        return ResponseEntity.ok(response);
    }
}
