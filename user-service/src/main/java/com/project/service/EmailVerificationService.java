package com.project.service;

import com.example.commondto.EmailNotificationRequest;
import com.project.kafka.KafkaEmailProducer;
import com.project.model.EmailVerification;
import com.project.repo.EmailVerificationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class EmailVerificationService {

    private static final int EXPIRATION_MINUTES = 15;

    private final EmailVerificationRepository repo;
    private final KafkaEmailProducer kafkaEmailProducer;

    public void createOrUpdateVerification(String email) {
        String code = UUID.randomUUID().toString();
        LocalDateTime now = LocalDateTime.now();

        EmailVerification verification = repo.findByEmail(email)
                .orElse(EmailVerification.builder()
                        .email(email)
                        .build());

        verification.setCode(code);
        verification.setConfirmed(false);
        verification.setCreatedAt(now);
        verification.setExpiresAt(now.plusMinutes(EXPIRATION_MINUTES));

        repo.save(verification);
        String confirmLink = "http://localhost:8081/api/email/confirm?code=" + code;

        System.out.println("Подтверждение email:" + confirmLink);

        EmailNotificationRequest request = new EmailNotificationRequest(
                email,
                "Email Verification",
                "Please confirm your email by clicking the link: " + confirmLink
        );

        kafkaEmailProducer.sendEmail(request);
    }

    public boolean confirmEmail(String code) {
        Optional<EmailVerification> optional = repo.findByCode(code);
        if (optional.isPresent()) {
            EmailVerification verification = optional.get();
            if (verification.isConfirmed()) return false;
            if (verification.getExpiresAt().isBefore(LocalDateTime.now())) return false;

            verification.setConfirmed(true);
            repo.save(verification);
            return true;
        }
        return false;
    }

    public boolean isEmailConfirmed(String email) {
        return repo.findByEmail(email)
                .map(EmailVerification::isConfirmed)
                .orElse(false);
    }

    public boolean resendVerification(String email) {
        Optional<EmailVerification> optional = repo.findByEmail(email);

        if (optional.isEmpty()) {
            return false;
        }

        createOrUpdateVerification(email);
        return true;
    }
}
