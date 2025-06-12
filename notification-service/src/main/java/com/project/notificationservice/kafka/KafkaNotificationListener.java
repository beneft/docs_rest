package com.project.notificationservice.kafka;

import com.example.commondto.NotificationRequest;
import com.example.commondto.SignerDTO;
import com.project.notificationservice.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

@Service
@RequiredArgsConstructor
public class KafkaNotificationListener {
    private final NotificationService mailService;

    @KafkaListener(topics = "notify-signers", groupId = "notification-group", containerFactory = "notificationKafkaListenerContainerFactory")
    public void listenApproval(NotificationRequest req) {
        mailService.notifySigners(req);
    }

}