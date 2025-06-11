package com.project.controller;

import com.project.dto.AuthResponse;
import com.project.dto.LoginRequest;
import com.project.dto.PasswordChangeRequest;
import com.project.dto.RegisterRequest;
import com.project.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = {"http://localhost:3000", "http://localhost:3004"})
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;

    @PostMapping("/register")
    public void register(@RequestBody RegisterRequest request) {
        userService.register(request);
    }

    @PostMapping("/login")
    public AuthResponse login(@RequestBody LoginRequest request) {
        return userService.login(request);
    }

    @PutMapping("/password")
    public void changePassword(@RequestBody PasswordChangeRequest req) {
        userService.changePassword(req.email(), req.oldPassword(), req.newPassword());
    }


    @PostMapping("/2fa/enable")
    public void enable2FA(@RequestParam String userId) {
        userService.enable2FA(userId);
    }

    @PostMapping("/2fa/disable")
    public void disable2FA(@RequestParam String userId) {
        userService.disable2FA(userId);
    }

    @PostMapping("/login2fa")
    public AuthResponse login2fa(@RequestBody LoginRequest request) {
        return userService.loginWith2FA(request);
    }

    @PostMapping("/2fa/confirm")
    public AuthResponse confirm(@RequestParam String token, @RequestParam String email) {
        return userService.confirm2FA(token, email);
    }
}