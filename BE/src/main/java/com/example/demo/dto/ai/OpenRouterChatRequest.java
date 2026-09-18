package com.example.demo.dto.ai;

import java.util.List;

public record OpenRouterChatRequest(String model, List<OpenRouterMessage> messages) {
}
