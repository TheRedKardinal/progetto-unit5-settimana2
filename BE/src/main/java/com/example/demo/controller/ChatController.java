package com.example.demo.controller;

import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.dto.ai.SuggerimentoResponse;
import com.example.demo.dto.chat.ApriChatRequest;
import com.example.demo.dto.chat.ChatResponse;
import com.example.demo.service.AiSuggestionService;
import com.example.demo.service.ChatService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/chat")
public class ChatController {

    private final ChatService chatService;
    private final AiSuggestionService aiSuggestionService;

    public ChatController(ChatService chatService, AiSuggestionService aiSuggestionService) {
        this.chatService = chatService;
        this.aiSuggestionService = aiSuggestionService;
    }

    @PostMapping("/apri")
    public ResponseEntity<ChatResponse> apriChat(@AuthenticationPrincipal UUID currentUserId,
            @Valid @RequestBody ApriChatRequest request) {
        return ResponseEntity.ok(chatService.apriOCreaChat(currentUserId, request.altroUtenteId()));
    }

    @PostMapping("/{chatId}/suggerimento")
    public ResponseEntity<SuggerimentoResponse> suggerimento(@PathVariable UUID chatId,
            @AuthenticationPrincipal UUID currentUserId) {
        return ResponseEntity.ok(aiSuggestionService.generaSuggerimento(chatId, currentUserId));
    }
}
