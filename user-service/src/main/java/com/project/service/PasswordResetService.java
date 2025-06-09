package com.project.service;

import com.project.config.KeycloakProperties;
import com.project.model.PasswordResetToken;
import com.project.repo.PasswordResetTokenRepository;
import lombok.RequiredArgsConstructor;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
@Service
public class PasswordResetService {
    private final PasswordResetTokenRepository tokenRepo;
    private final Keycloak keycloak;
    private final KeycloakProperties props;

    private static final Logger logger = LoggerFactory.getLogger(PasswordResetService.class);

    public void initiateReset(String email) {
        List<UserRepresentation> users = keycloak.realm(props.getRealm()).users().searchByEmail(email, true);
        if (users.isEmpty()) return;

        String userId = users.get(0).getId();
        String token = UUID.randomUUID().toString();

        tokenRepo.save(new PasswordResetToken(token, userId, Instant.now().plus(Duration.ofHours(1))));

        String resetUrl = "http://localhost:3000/reset-password?token=" + token;
        logger.info(resetUrl);
    }

    public void resetPassword(String token, String newPassword) {
        PasswordResetToken t = tokenRepo.findById(token)
                .orElseThrow(() -> new IllegalArgumentException("Invalid token"));

        if (t.getExpiresAt().isBefore(Instant.now())) {
            throw new IllegalArgumentException("Token expired");
        }

        CredentialRepresentation pass = new CredentialRepresentation();
        pass.setTemporary(false);
        pass.setType(CredentialRepresentation.PASSWORD);
        pass.setValue(newPassword);

        keycloak.realm(props.getRealm()).users().get(t.getUserId()).resetPassword(pass);
        tokenRepo.deleteById(token);
    }
}