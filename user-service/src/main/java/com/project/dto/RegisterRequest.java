package com.project.dto;

public record RegisterRequest(
        String email,
        String password,
        String firstName,
        String lastName,
        String organization,
        String position,
        String phone) { }