package com.example.demo.service;

import java.util.UUID;

import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import com.example.demo.dto.statistiche.StatisticheResponse;
import com.example.demo.entity.User;
import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.repository.ChatRepository;
import com.example.demo.repository.MessaggioRepository;
import com.example.demo.repository.UserRepository;

@Service
public class StatisticheService {

    private final UserRepository userRepository;
    private final MessaggioRepository messaggioRepository;
    private final ChatRepository chatRepository;
    private final TemplateEngine templateEngine;
    private final EmailService emailService;

    public StatisticheService(UserRepository userRepository, MessaggioRepository messaggioRepository,
            ChatRepository chatRepository, TemplateEngine templateEngine, EmailService emailService) {
        this.userRepository = userRepository;
        this.messaggioRepository = messaggioRepository;
        this.chatRepository = chatRepository;
        this.templateEngine = templateEngine;
        this.emailService = emailService;
    }

    public StatisticheResponse calcolaEInviaEmail(UUID userId) {
        User utente = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Utente non trovato"));

        long messaggiInviati = messaggioRepository.countByMittente_Id(userId);
        long messaggiRicevuti = messaggioRepository.countMessaggiRicevuti(userId);
        long numeroChat = chatRepository.countByPartecipante(userId);

        Context context = new Context();
        context.setVariable("username", utente.getUsername());
        context.setVariable("messaggiInviati", messaggiInviati);
        context.setVariable("messaggiRicevuti", messaggiRicevuti);
        context.setVariable("numeroChat", numeroChat);

        String corpoHtml = templateEngine.process("email/statistiche", context);

        emailService.inviaEmailHtml(utente.getEmail(), "Le tue statistiche di messaggistica", corpoHtml);

        return new StatisticheResponse(
                utente.getId(), utente.getUsername(), messaggiInviati, messaggiRicevuti, numeroChat,
                true, utente.getEmail());
    }
}
