package com.example.demo.websocket;

import java.security.Principal;
import java.time.Instant;
import java.util.UUID;

import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import com.example.demo.dto.chat.InviaMessaggioRequest;
import com.example.demo.dto.chat.LetturaEvent;
import com.example.demo.dto.chat.MessaggioResponse;
import com.example.demo.dto.chat.SegnaLettiRequest;
import com.example.demo.service.MessaggioService;

@Controller
public class ChatWebSocketController {

    private final MessaggioService messaggioService;
    private final SimpMessagingTemplate messagingTemplate;

    public ChatWebSocketController(MessaggioService messaggioService, SimpMessagingTemplate messagingTemplate) {
        this.messaggioService = messaggioService;
        this.messagingTemplate = messagingTemplate;
    }

    @MessageMapping("/chat.invia")
    public void inviaMessaggio(@Payload InviaMessaggioRequest request, Principal principal) {
        UUID mittenteId = UUID.fromString(principal.getName());

        MessaggioResponse messaggio = messaggioService.inviaMessaggio(request.chatId(), mittenteId, request.testo());

        messagingTemplate.convertAndSend("/topic/chat." + request.chatId(), messaggio);
    }

    @MessageMapping("/chat.letto")
    public void segnaLetti(@Payload SegnaLettiRequest request, Principal principal) {
        UUID lettoreId = UUID.fromString(principal.getName());

        messaggioService.segnaComeLetti(request.chatId(), lettoreId);

        messagingTemplate.convertAndSend(
                "/topic/chat." + request.chatId() + ".letture",
                new LetturaEvent(request.chatId(), lettoreId, Instant.now()));
    }
}
