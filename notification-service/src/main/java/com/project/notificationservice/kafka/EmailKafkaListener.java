package com.project.notificationservice.kafka;

import com.example.commondto.EmailNotificationRequest;
import com.project.notificationservice.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmailKafkaListener {

    private final NotificationService mailService;

    @KafkaListener(topics = "email-verification-topic",
            groupId = "notification-group",
            containerFactory = "emailKafkaListenerContainerFactory")
    public void listenEmailVerification(EmailNotificationRequest request) {
        mailService.sendEmail(request.getTo(), request.getSubject(), request.getBody());
    }

    @KafkaListener(topics = "2fa-notification-topic",
            groupId = "notification-group",
            containerFactory = "emailKafkaListenerContainerFactory")
    public void listen2FANotification(EmailNotificationRequest request) {
        mailService.sendEmail(request.getTo(), request.getSubject(), request.getBody());
    }
}
