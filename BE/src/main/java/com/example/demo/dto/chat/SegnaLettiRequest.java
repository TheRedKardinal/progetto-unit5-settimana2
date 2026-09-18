package com.example.demo.dto.chat;

import java.util.UUID;

import jakarta.validation.constraints.NotNull;

public record SegnaLettiRequest(@NotNull(message = "chatId obbligatorio") UUID chatId) {
}
