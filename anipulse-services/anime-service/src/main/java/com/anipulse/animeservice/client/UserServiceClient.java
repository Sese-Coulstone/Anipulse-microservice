package com.anipulse.animeservice.client;

import com.anipulse.sharedservice.dto.AuthResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "USER-SERVICE", fallbackFactory = UserServiceClientFallback.class)
public interface UserServiceClient {
    @GetMapping("/{email}")
    ResponseEntity<AuthResponse> getUserProfile(@PathVariable String email);
}
