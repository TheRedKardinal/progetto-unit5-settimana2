package com.example.demo.dto.chat;

import java.util.UUID;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record InviaMessaggioRequest(
        @NotNull(message = "chatId obbligatorio") UUID chatId,
        @NotBlank(message = "testo obbligatorio") String testo) {
}
