package com.example.demo.dto.auth;

import java.util.UUID;

public record AuthResponse(String token, UUID userId, String username) {
}
