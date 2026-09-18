package com.example.demo.dto.chat;

import java.util.UUID;

import jakarta.validation.constraints.NotNull;

public record ApriChatRequest(@NotNull(message = "altroUtenteId obbligatorio") UUID altroUtenteId) {
}
