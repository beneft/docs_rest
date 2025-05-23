package com.project.config;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/fallback")
public class FallbackController {

    @RequestMapping(value = "/filestorage")
    public ResponseEntity<String> fallbackFile() {
        return fallback("File storage");
    }

    @RequestMapping(value = "/signature")
    public ResponseEntity<String> fallbackSignature() {
        return fallback("Signature");
    }

    @RequestMapping(value = "/user")
    public ResponseEntity<String> fallbackUser() {
        return fallback("User");
    }

    @RequestMapping(value = "/template")
    public ResponseEntity<String> fallbackTemplate() {
        return fallback("Template");
    }

    @RequestMapping(value = "/notification")
    public ResponseEntity<String> fallbackNotification() {
        return fallback("Notification");
    }

    private ResponseEntity<String> fallback(String serviceName) {
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                .body(serviceName + " service is currently unavailable. Please try again later.");
    }
}
