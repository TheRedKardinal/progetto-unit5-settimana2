package com.example.demo.dto.chat;

import java.time.Instant;
import java.util.UUID;

public record ChatSummaryResponse(
        UUID id,
        UUID altroUtenteId,
        String altroUsername,
        String ultimoMessaggioTesto,
        Instant ultimoMessaggioData,
        long nonLetti) {
}
