package com.project.kafka;

import com.example.commondto.EmailNotificationRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class KafkaEmailProducer {

    private final KafkaTemplate<String, EmailNotificationRequest> kafkaTemplate;

    public void sendEmail(EmailNotificationRequest request) {
        kafkaTemplate.send("email-verification-topic", request);
    }
}