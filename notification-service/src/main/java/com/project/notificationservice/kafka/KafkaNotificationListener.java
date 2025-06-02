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

    @KafkaListener(topics = "notify-signers", groupId = "notification-group")
    public void listen(NotificationRequest req) {
//        for (SignerDTO signer : req.getSigners()) {
//            String link = (signer.getUserId() == null)
//                    ? generateGuestLink(req.getDocumentId(), signer.getEmail())
//                    : "http://localhost:3000/profile";
//            String body = String.format("Hello %s,\nYou are invited to sign '%s'.\nClick here: %s",
//                    signer.getFullName(), req.getDocumentName(), link);
//
//            System.out.println(link);
//
//            mailService.sendEmail(signer.getEmail(), "Please sign the document", body);
//        }
        mailService.notifySigners(req);
    }

    private String generateGuestLink(String docId, String email) {
        String encoded = Base64.getUrlEncoder().encodeToString(email.getBytes(StandardCharsets.UTF_8));
        return "http://localhost:3000/sign/" + docId + "/" + encoded;
    }
}