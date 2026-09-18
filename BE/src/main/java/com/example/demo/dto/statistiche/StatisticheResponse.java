package com.example.demo.dto.statistiche;

import java.util.UUID;

public record StatisticheResponse(
        UUID utenteId,
        String username,
        long messaggiInviati,
        long messaggiRicevuti,
        long numeroChat,
        boolean emailInviata,
        String emailDestinatario) {
}
