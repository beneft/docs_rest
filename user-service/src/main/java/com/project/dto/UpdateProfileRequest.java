package com.project.dto;

public record UpdateProfileRequest(
        String email,
        String firstName,
        String lastName,
        String organization,
        String position,
        String phone) { }
