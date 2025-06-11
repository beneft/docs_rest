package com.project.kafka;

import com.example.commondto.PasswordResetNotificationRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class KafkaPasswordResetProducer {

    private final KafkaTemplate<String, PasswordResetNotificationRequest> kafkaTemplate;

    public void sendPasswordResetEmail(PasswordResetNotificationRequest request) {
        kafkaTemplate.send("password-reset-topic", request);
    }
}