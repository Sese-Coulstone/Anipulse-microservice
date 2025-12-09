package com.anipulse.animeservice.config;

import com.anipulse.sharedservice.security.KeycloakRealmRoleConverter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.web.SecurityFilterChain;

/**
 * Security configuration for anime-service
 * Configures JWT authentication with Keycloak
 * Defines public and secured endpoints
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http, JwtAuthenticationConverter jwtAuthenticationConverter) throws Exception {
        return http
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth
                        // Public anime search/browse endpoints
                        .requestMatchers(HttpMethod.GET, "/search").permitAll()
                        .requestMatchers(HttpMethod.GET, "/{malId}").permitAll()
                        .requestMatchers(HttpMethod.GET, "/top").permitAll()
                        .requestMatchers(HttpMethod.GET, "/seasonal/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/ratings/anime/{animeId}").permitAll()
                        .requestMatchers(HttpMethod.GET, "/ratings/anime/{animeId}/stats").permitAll()

                        // Actuator endpoints
                        .requestMatchers("/actuator/**").permitAll()
                        
                        // User-specific endpoints require authentication
                        .requestMatchers("/my-list/**").authenticated()

                        // PROTECTED: User's rating endpoints
                        .requestMatchers(HttpMethod.POST, "/ratings").authenticated()
                        .requestMatchers(HttpMethod.DELETE, "/ratings/**").authenticated()
                        .requestMatchers(HttpMethod.GET, "/ratings/my-rating/**").authenticated()
                        .requestMatchers(HttpMethod.GET, "/ratings/my-ratings").authenticated()
                        .requestMatchers(HttpMethod.GET, "/ratings/anime/{animeId}/exists").authenticated()

                        // PROTECTED: Recommendation data endpoints (for recommendation-service)
                        .requestMatchers("/recommendation-data/**").hasRole("SERVICE")
                        
                        // All other requests require authentication
                        .anyRequest().authenticated()
                )
                .oauth2ResourceServer(oAuth -> oAuth.jwt(jwt -> jwt.jwtAuthenticationConverter(jwtAuthenticationConverter)))
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .build();
    }

    /**
     * Configure JWT authentication converter with Keycloak role mapping
     */
    @Bean
    public JwtAuthenticationConverter jwtAuthenticationConverter() {
        JwtAuthenticationConverter converter = new JwtAuthenticationConverter();
        converter.setJwtGrantedAuthoritiesConverter(new KeycloakRealmRoleConverter());
        return converter;
    }
}

