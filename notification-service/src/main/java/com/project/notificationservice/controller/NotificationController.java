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
    private NotificationService notificationService;

    @PostMapping
    public ResponseEntity<Void> notifySigners(@RequestBody NotificationRequest req) {
        notificationService.notifySigners(req);
        return ResponseEntity.ok().build();
    }
}
