package com.example.demo.dto.auth;

import jakarta.validation.constraints.NotBlank;

public record LoginRequest(
        @NotBlank(message = "username obbligatorio") String username,
        @NotBlank(message = "password obbligatoria") String password) {
}
