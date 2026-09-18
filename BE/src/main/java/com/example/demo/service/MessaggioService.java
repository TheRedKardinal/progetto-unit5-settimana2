package com.example.demo.service;

import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
public class MessaggioService {

    private final MessaggioRepository messaggioRepository;
    private final ChatRepository chatRepository;
    private final UserRepository userRepository;

    public MessaggioService(MessaggioRepository messaggioRepository, ChatRepository chatRepository,
            UserRepository userRepository) {
        this.messaggioRepository = messaggioRepository;
        this.chatRepository = chatRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    public MessaggioResponse inviaMessaggio(UUID chatId, UUID mittenteId, String testo) {
        Chat chat = chatRepository.findById(chatId)
                .orElseThrow(() -> new ResourceNotFoundException("Chat non trovata"));

        verificaPartecipante(chat, mittenteId);

        User mittente = userRepository.findById(mittenteId)
                .orElseThrow(() -> new ResourceNotFoundException("Utente non trovato"));

        Messaggio messaggio = Messaggio.builder()
                .testo(testo)
                .chat(chat)
                .mittente(mittente)
                .build();

        // flush immediato: @CreationTimestamp viene valorizzato solo all'esecuzione dell'INSERT
        messaggio = messaggioRepository.saveAndFlush(messaggio);

        return MessaggioResponse.from(messaggio);
    }

    @Transactional
    public void segnaComeLetti(UUID chatId, UUID lettoreId) {
        Chat chat = chatRepository.findById(chatId)
                .orElseThrow(() -> new ResourceNotFoundException("Chat non trovata"));

        verificaPartecipante(chat, lettoreId);

        messaggioRepository.segnaComeLetti(chatId, lettoreId);
    }

    private void verificaPartecipante(Chat chat, UUID utenteId) {
        boolean partecipante = chat.getUser1().getId().equals(utenteId) || chat.getUser2().getId().equals(utenteId);

        if (!partecipante) {
            throw new ForbiddenOperationException("L'utente non fa parte di questa chat");
        }
    }
}
