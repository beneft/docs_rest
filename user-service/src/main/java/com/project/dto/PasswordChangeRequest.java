package com.project.dto;

public record PasswordChangeRequest(
        String email,
        String oldPassword,
        String newPassword
) {}
