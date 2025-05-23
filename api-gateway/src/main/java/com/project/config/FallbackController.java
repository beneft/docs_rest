package com.project.config;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/fallback")
public class FallbackController {

    @GetMapping("/filestorage")
    public ResponseEntity<String> fallbackFile() {
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                .body("File storage service is unavailable.");
    }

    @GetMapping("/signature")
    public ResponseEntity<String> fallbackSignature() {
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                .body("Signature service is unavailable.");
    }

    @GetMapping("/user")
    public ResponseEntity<String> fallbackUser() {
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                .body("User service is unavailable.");
    }

    @GetMapping("/template")
    public ResponseEntity<String> fallbackTemplate() {
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                .body("Template service is unavailable.");
    }

    @GetMapping("/notification")
    public ResponseEntity<String> fallbackNotification() {
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                .body("Notification service is unavailable.");
    }
}
