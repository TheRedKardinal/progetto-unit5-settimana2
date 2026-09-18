package com.example.demo.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import com.example.demo.dto.ai.OpenRouterChatRequest;
import com.example.demo.dto.ai.OpenRouterChatResponse;
import com.example.demo.dto.ai.OpenRouterMessage;
import com.example.demo.dto.ai.SuggerimentoResponse;
import com.example.demo.entity.Chat;
import com.example.demo.entity.Messaggio;
import com.example.demo.exception.ForbiddenOperationException;
import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.repository.ChatRepository;
import com.example.demo.repository.MessaggioRepository;

@Service
public class AiSuggestionService {

    private static final String PROMPT_SISTEMA = """
            Sei un assistente che aiuta l'utente a rispondere ai messaggi di una chat privata.
            In base alla conversazione, suggerisci una possibile risposta breve, naturale e in italiano
            da mandare al posto dell'utente. Rispondi SOLO con il testo del messaggio suggerito,
            senza virgolette né commenti aggiuntivi.
            """;

    private final RestClient openRouterRestClient;
    private final ChatRepository chatRepository;
    private final MessaggioRepository messaggioRepository;
    private final String model;
    private final int contestoLimit;

    public AiSuggestionService(RestClient openRouterRestClient, ChatRepository chatRepository,
            MessaggioRepository messaggioRepository,
            @Value("${openrouter.model}") String model,
            @Value("${ai.contesto-messaggi-limit:20}") int contestoLimit) {
        this.openRouterRestClient = openRouterRestClient;
        this.chatRepository = chatRepository;
        this.messaggioRepository = messaggioRepository;
        this.model = model;
        this.contestoLimit = contestoLimit;
    }

    public SuggerimentoResponse generaSuggerimento(UUID chatId, UUID currentUserId) {
        Chat chat = chatRepository.findById(chatId)
                .orElseThrow(() -> new ResourceNotFoundException("Chat non trovata"));

        boolean partecipante = chat.getUser1().getId().equals(currentUserId)
                || chat.getUser2().getId().equals(currentUserId);

        if (!partecipante) {
            throw new ForbiddenOperationException("L'utente non fa parte di questa chat");
        }

        List<Messaggio> ultimiMessaggi = messaggioRepository
                .findByChat_IdOrderByCreatedAtDesc(chatId, PageRequest.of(0, contestoLimit));
        Collections.reverse(ultimiMessaggi);

        List<OpenRouterMessage> messaggiLlm = new ArrayList<>();
        messaggiLlm.add(new OpenRouterMessage("system", PROMPT_SISTEMA));

        for (Messaggio messaggio : ultimiMessaggi) {
            String ruolo = messaggio.getMittente().getId().equals(currentUserId) ? "assistant" : "user";
            messaggiLlm.add(new OpenRouterMessage(ruolo, messaggio.getTesto()));
        }

        OpenRouterChatRequest richiesta = new OpenRouterChatRequest(model, messaggiLlm);

        OpenRouterChatResponse risposta = openRouterRestClient.post()
                .uri("/chat/completions")
                .body(richiesta)
                .retrieve()
                .body(OpenRouterChatResponse.class);

        if (risposta == null || risposta.choices() == null || risposta.choices().isEmpty()) {
            throw new IllegalStateException("Nessun suggerimento ricevuto dall'AI");
        }

        String testoSuggerito = risposta.choices().get(0).message().content().trim();

        return new SuggerimentoResponse(testoSuggerito);
    }
}
