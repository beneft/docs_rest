package com.project.notificationservice.service;

import com.example.commondto.DeputyDTO;
import com.example.commondto.NotificationRequest;
import com.example.commondto.SignerDTO;
import com.project.notificationservice.controller.NotificationController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Objects;

@Service
public class NotificationService {

    @Autowired
    private JavaMailSender mailSender;

    private static final Logger logger = LoggerFactory.getLogger(NotificationController.class);

    public void notifySigners(NotificationRequest req) {
        for (SignerDTO signer : req.getSigners()) {
            // Notify signer
            String signerLink = generateLink(req.getDocumentId(), signer.getUserId(), signer.getEmail());
            if (Objects.equals(signer.getUserId(), req.getInitiator())){
                sendCreationNotification(signer.getEmail(), signer.getFullName(), req.getDocumentName(), signerLink);
            } else {
                sendNotification(signer.getEmail(), signer.getFullName(), req.getDocumentName(), signerLink);
            }

            // Notify deputy (if present)
            DeputyDTO deputy = signer.getDeputy();
            if (deputy != null && deputy.getEmail() != null) {
                String deputyLink = generateLink(req.getDocumentId(), null, deputy.getEmail());
                sendNotification(deputy.getEmail(), deputy.getName(), req.getDocumentName(), deputyLink);
            }
        }
    }

    private void sendNotification(String to, String name, String documentName, String link) {
        String body = String.format(
                "Hello %s,\nYou are invited to sign '%s'.\nClick here: %s",
                name, documentName, link
        );

        logger.info("link was generated: " + link);
        sendEmail(to, "Please sign the document", body);
    }

    private void sendCreationNotification(String to, String name, String documentName, String link) {
        String body = String.format(
                "Hello %s,\nYou have created the document and assigned to sign '%s'.\nClick here: %s",
                name, documentName, link
        );

        logger.info(link);
        //sendEmail(to, "Please sign the document", body);
    }

    private String generateLink(String documentId, String userId, String email) {
        if (userId == null) {
            String encoded = Base64.getUrlEncoder().encodeToString(email.getBytes(StandardCharsets.UTF_8));
            return "http://localhost:3000/sign/" + documentId + "/" + encoded;
        } else {
            return "http://localhost:3000/profile";
        }
    }

    public void sendEmail(String to, String subject, String text) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject(subject);
        message.setText(text);
        mailSender.send(message);
    }
}
