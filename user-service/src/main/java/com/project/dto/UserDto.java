package com.project.dto;

public record UserDto(
        String id,
        String email,
        String firstName,
        String lastName,
        String organization,
        String position,
        String phone,
        String iin) { }