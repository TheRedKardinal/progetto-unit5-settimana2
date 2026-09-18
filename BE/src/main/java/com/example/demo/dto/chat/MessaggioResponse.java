package com.example.demo.dto.chat;

import java.time.Instant;
import java.util.UUID;

import com.example.demo.entity.Messaggio;

public record MessaggioResponse(UUID id, UUID chatId, String testo, Instant createdAt, UUID mittenteId, boolean letto) {

    public static MessaggioResponse from(Messaggio messaggio) {
        return new MessaggioResponse(
                messaggio.getId(),
                messaggio.getChat().getId(),
                messaggio.getTesto(),
                messaggio.getCreatedAt(),
                messaggio.getMittente().getId(),
                messaggio.isLetto());
    }
}
