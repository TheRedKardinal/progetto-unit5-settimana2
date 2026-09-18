package com.example.demo.dto.chat;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public record ChatResponse(
        UUID id,
        UUID altroUtenteId,
        String altroUsername,
        Instant createdAt,
        List<MessaggioResponse> messaggi) {
}
