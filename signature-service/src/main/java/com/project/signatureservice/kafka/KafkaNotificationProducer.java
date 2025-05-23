package com.project.signatureservice.kafka;

import com.example.commondto.NotificationRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class KafkaNotificationProducer {

    private final KafkaTemplate<String, NotificationRequest> kafkaTemplate;

    public void sendNotification(NotificationRequest request) {
        kafkaTemplate.send("notify-signers", request);
    }
}