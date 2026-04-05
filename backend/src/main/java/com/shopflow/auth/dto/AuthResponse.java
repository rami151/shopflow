package com.shopflow.auth.dto;

public record AuthResponse(
        String accessToken,
        String refreshToken,
        String email,
        String role
) {}
