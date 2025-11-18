package com.anipulse.userservice.service;

import jakarta.ws.rs.core.Response;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.resource.RealmResource;
import org.keycloak.admin.client.resource.UserResource;
import org.keycloak.admin.client.resource.UsersResource;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class KeycloakService {

    private final Keycloak keycloak;

    @Value("${keycloak.realm}")
    private String realm;

    public String createUser(String username, String email, String password) {
        try {
            RealmResource realmResource = keycloak.realm(realm);
            UsersResource usersResource = realmResource.users();

            // Implementation for creating a user in Keycloak goes here
            UserRepresentation user = new UserRepresentation();
            user.setUsername(username);
            user.setEmail(email);
            user.setEnabled(false); // User is disabled until email verification
            user.setEmailVerified(false);

            Response response = usersResource.create(user);

            if (response.getStatus() == 201) {
                String userId = response.getLocation().getPath().replaceAll(".*/([^/]+)$", "$1");
                log.info("User created in Keycloak with ID: {}", userId);

                // Set password
                CredentialRepresentation credential = new CredentialRepresentation();
                credential.setType(CredentialRepresentation.PASSWORD);
                credential.setValue(password);
                credential.setTemporary(false);

                UserResource userResource = usersResource.get(userId);
                userResource.resetPassword(credential);

                return userId;
            } else {
                log.error("Failed to create user in Keycloak. Status: {}", response.getStatus());
                throw new RuntimeException("Failed to create user in Keycloak");
            }
        } catch (Exception e) {
            log.error("Error creating user in Keycloak: {}", e.getMessage(), e);
            throw new RuntimeException("Error creating user in Keycloak: " + e.getMessage());
        }

    }

    public void enableUser(String keycloakId) {
        try {
            RealmResource realmResource = keycloak.realm(realm);
            UserResource userResource = realmResource.users().get(keycloakId);

            UserRepresentation user = userResource.toRepresentation();
            user.setEnabled(true);
            user.setEmailVerified(true);

            userResource.update(user);
            log.info("User enabled in Keycloak: {}", keycloakId);
        } catch (Exception e) {
            log.error("Error enabling user in Keycloak: {}", e.getMessage(), e);
            throw new RuntimeException("Error enabling user in Keycloak");
        }
    }

    public void deleteUser(String keycloakId) {
        try {
            RealmResource realmResource = keycloak.realm(realm);
            realmResource.users().delete(keycloakId);
            log.info("User deleted from Keycloak: {}", keycloakId);
        } catch (Exception e) {
            log.error("Error deleting user from Keycloak: {}", e.getMessage(), e);
            throw new RuntimeException("Error deleting user from Keycloak");
        }
    }

    public boolean userExistsInKeycloak(String email) {
        try {
            RealmResource realmResource = keycloak.realm(realm);
            List<UserRepresentation> users = realmResource.users().search(email, true);
            return !users.isEmpty();
        } catch (Exception e) {
            log.error("Error checking user existence in Keycloak: {}", e.getMessage(), e);
            return false;
        }
    }
}
