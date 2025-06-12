package com.project.controller;

import com.project.service.EmailVerificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/email")
@RequiredArgsConstructor
public class EmailVerificationController {

    private final EmailVerificationService service;

    @GetMapping("/confirm")
    public ResponseEntity<Map<String, Object>> confirm(@RequestParam String code) {
        boolean result = service.confirmEmail(code);

        Map<String, Object> response = new HashMap<>();
        if (result) {
            response.put("status", "success");
            response.put("message", "Email успешно подтвержден!");
            return ResponseEntity.ok(response);
        } else {
            response.put("status", "failed");
            response.put("message", "Код недействителен или истек!");
            return ResponseEntity.badRequest().body(response);
        }
    }

    @PostMapping("/resend")
    public String resend(@RequestParam String email) {
        boolean result = service.resendVerification(email);
        return result ? "Ссылка подтверждения обновлена. Проверьте логи." : "Email не найден!";
    }
}
