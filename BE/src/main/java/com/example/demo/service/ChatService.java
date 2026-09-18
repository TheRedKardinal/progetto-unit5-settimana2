package com.example.demo.service;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.demo.dto.chat.ChatResponse;
import com.example.demo.dto.chat.ChatSummaryResponse;
import com.example.demo.dto.chat.MessaggioResponse;
import com.example.demo.entity.Chat;
import com.example.demo.entity.Messaggio;
import com.example.demo.entity.User;
import com.example.demo.exception.ForbiddenOperationException;
import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.repository.ChatRepository;
import com.example.demo.repository.MessaggioRepository;
import com.example.demo.repository.UserRepository;

@Service
public class ChatService {

    private final ChatRepository chatRepository;
    private final UserRepository userRepository;
    private final MessaggioRepository messaggioRepository;
    private final int limiteMessaggiDefault;

    public ChatService(ChatRepository chatRepository, UserRepository userRepository,
            MessaggioRepository messaggioRepository,
            @Value("${chat.messaggi-default-limit:50}") int limiteMessaggiDefault) {
        this.chatRepository = chatRepository;
        this.userRepository = userRepository;
        this.messaggioRepository = messaggioRepository;
        this.limiteMessaggiDefault = limiteMessaggiDefault;
    }

    @Transactional
    public ChatResponse apriOCreaChat(UUID currentUserId, UUID altroUtenteId) {
        if (currentUserId.equals(altroUtenteId)) {
            throw new ForbiddenOperationException("Non puoi aprire una chat con te stesso");
        }

        Chat chat = chatRepository.findTraUtenti(currentUserId, altroUtenteId)
                .orElseGet(() -> creaNuovaChat(currentUserId, altroUtenteId));

        return costruisciRisposta(chat, currentUserId, limiteMessaggiDefault);
    }

    private Chat creaNuovaChat(UUID currentUserId, UUID altroUtenteId) {
        User utenteCorrente = userRepository.findById(currentUserId)
                .orElseThrow(() -> new ResourceNotFoundException("Utente corrente non trovato"));
        User altroUtente = userRepository.findById(altroUtenteId)
                .orElseThrow(() -> new ResourceNotFoundException("Utente destinatario non trovato"));

        Chat nuovaChat = Chat.builder()
                .user1(utenteCorrente)
                .user2(altroUtente)
                .build();

        // flush immediato: @CreationTimestamp viene valorizzato solo all'esecuzione dell'INSERT,
        // e costruisciRisposta() legge createdAt dall'oggetto Java subito dopo
        return chatRepository.saveAndFlush(nuovaChat);
    }

    private ChatResponse costruisciRisposta(Chat chat, UUID currentUserId, int limiteMessaggi) {
        User altroUtente = chat.getUser1().getId().equals(currentUserId) ? chat.getUser2() : chat.getUser1();

        List<MessaggioResponse> messaggi = messaggioRepository
                .findByChat_IdOrderByCreatedAtDesc(chat.getId(), PageRequest.of(0, limiteMessaggi))
                .stream()
                .map(MessaggioResponse::from)
                .toList();

        return new ChatResponse(chat.getId(), altroUtente.getId(), altroUtente.getUsername(), chat.getCreatedAt(), messaggi);
    }

    public List<ChatSummaryResponse> listaChatUtente(UUID currentUserId) {
        return chatRepository.findByPartecipante(currentUserId).stream()
                .map(chat -> costruisciSommario(chat, currentUserId))
                .sorted(Comparator.comparing(ChatSummaryResponse::ultimoMessaggioData, Comparator.nullsLast(Comparator.reverseOrder())))
                .toList();
    }

    private ChatSummaryResponse costruisciSommario(Chat chat, UUID currentUserId) {
        User altroUtente = chat.getUser1().getId().equals(currentUserId) ? chat.getUser2() : chat.getUser1();

        Optional<Messaggio> ultimoMessaggio = messaggioRepository.findFirstByChat_IdOrderByCreatedAtDesc(chat.getId());
        long nonLetti = messaggioRepository.countByChat_IdAndMittente_IdNotAndLettoFalse(chat.getId(), currentUserId);

        return new ChatSummaryResponse(
                chat.getId(),
                altroUtente.getId(),
                altroUtente.getUsername(),
                ultimoMessaggio.map(Messaggio::getTesto).orElse(null),
                ultimoMessaggio.map(Messaggio::getCreatedAt).orElse(chat.getCreatedAt()),
                nonLetti);
    }
}
