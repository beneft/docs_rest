package com.project.controller;

import com.project.service.EmailVerificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/email")
@RequiredArgsConstructor
public class EmailVerificationController {

    private final EmailVerificationService service;

    @GetMapping("/confirm")
    public String confirm(@RequestParam String code) {
        boolean result = service.confirmEmail(code);
        return result ? "Email успешно подтвержден!" : "Код недействителен или истек!";
    }

    @PostMapping("/resend")
    public String resend(@RequestParam String email) {
        boolean result = service.resendVerification(email);
        return result ? "Ссылка подтверждения обновлена. Проверьте логи." : "Email не найден!";
    }
}
