package com.example.demo.dto.chat;

import java.time.Instant;
import java.util.UUID;

public record LetturaEvent(UUID chatId, UUID lettoreId, Instant momento) {
}
