package com.example.demo.dto.auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record RegisterRequest(
        @NotBlank(message = "username obbligatorio") @Size(min = 3, max = 50) String username,
        @NotBlank(message = "email obbligatoria") @Email String email,
        @NotBlank(message = "password obbligatoria") @Size(min = 8, message = "la password deve avere almeno 8 caratteri") String password) {
}
