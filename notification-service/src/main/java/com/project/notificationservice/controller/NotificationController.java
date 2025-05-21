package com.project.notificationservice.controller;

import com.example.commondto.NotificationRequest;
import com.example.commondto.SignerDTO;
import com.project.notificationservice.service.NotificationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

@RestController
@RequestMapping("/notify")
public class NotificationController {

    @Autowired
    private NotificationService mailService;
    private static final Logger logger = LoggerFactory.getLogger(NotificationController.class);
    @PostMapping
    public ResponseEntity<Void> notifySigners(@RequestBody NotificationRequest req) {
        for (SignerDTO signer : req.getSigners()) {
            String link = (signer.getUserId() == null)
                    ? generateGuestLink(req.getDocumentId(), signer.getEmail())
                    : "http://localhost:3000/profile";

            logger.info(link);

            String body = String.format("Hello %s,\nYou are invited to sign '%s'.\nClick here: %s",
                    signer.getFullName(), req.getDocumentName(), link);

            mailService.sendEmail(signer.getEmail(), "Please sign the document", body);
        }
        return ResponseEntity.ok().build();
    }

    private String generateGuestLink(String docId, String email) {
        String encoded = Base64.getUrlEncoder().encodeToString(email.getBytes(StandardCharsets.UTF_8));
        return "http://localhost:3000/sign/" + docId + "/" + encoded;
    }
}
