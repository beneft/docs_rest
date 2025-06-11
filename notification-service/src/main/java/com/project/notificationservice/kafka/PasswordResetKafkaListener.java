package com.project.notificationservice.kafka;

import com.example.commondto.PasswordResetNotificationRequest;
import com.project.notificationservice.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PasswordResetKafkaListener {

    private final NotificationService mailService;

    @KafkaListener(topics = "password-reset-topic",
            groupId = "notification-group",
            containerFactory = "passwordResetKafkaListenerContainerFactory")
    public void listenPasswordReset(PasswordResetNotificationRequest request) {
        mailService.sendEmail(request.getTo(), request.getSubject(), request.getBody());
    }
}
