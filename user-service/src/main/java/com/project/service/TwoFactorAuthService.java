package com.project.service;

import com.example.commondto.EmailNotificationRequest;
import com.project.kafka.KafkaEmailProducer;
import com.project.model.TwoFactorAuthToken;
import com.project.repo.TwoFactorAuthTokenRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TwoFactorAuthService {

    private final TwoFactorAuthTokenRepository repo;
    private static final Logger logger = LoggerFactory.getLogger(TwoFactorAuthService.class);
    private final KafkaEmailProducer kafkaEmailProducer;

    public String generate2FAToken(String userId, String email, String password) {
        String token = UUID.randomUUID().toString();
        TwoFactorAuthToken entity = TwoFactorAuthToken.builder()
                .token(token)
                .userId(userId)
                .expiresAt(Instant.now().plus(Duration.ofMinutes(10)))
                .password(password)
                .build();
        repo.save(entity);

        String url = "http://localhost:3000/2fa?token=" + token;
        logger.info("2FA Verification Link: {}", url);

        EmailNotificationRequest request = new EmailNotificationRequest();
        request.setTo(email);
        request.setSubject("Your 2FA Verification Link");
        request.setBody("Click the link to verify: http://localhost:3000/2fa?token=" + token);
        kafkaEmailProducer.send2FAEmail( request);

        return token;
    }

    public TwoFactorAuthToken validateAndGet2FAToken(String token, String userId) {
        Optional<TwoFactorAuthToken> optional = repo.findById(token);
        if (optional.isEmpty()) throw new NoSuchElementException("Token not found.");

        TwoFactorAuthToken entity = optional.get();
        if (!entity.getUserId().equals(userId)) throw new IllegalArgumentException("Invalid user.");
        if (entity.getExpiresAt().isBefore(Instant.now())) throw new IllegalArgumentException("Token expired.");

        repo.deleteById(token);
        return entity;
    }

}
