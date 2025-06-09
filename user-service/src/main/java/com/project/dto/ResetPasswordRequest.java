package com.project.dto;

public record ResetPasswordRequest(String token, String newPassword) {}
