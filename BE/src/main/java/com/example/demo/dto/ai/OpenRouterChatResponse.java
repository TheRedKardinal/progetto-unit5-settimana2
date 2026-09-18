package com.example.demo.dto.ai;

import java.util.List;

public record OpenRouterChatResponse(List<Choice> choices) {

    public record Choice(OpenRouterMessage message) {
    }
}
