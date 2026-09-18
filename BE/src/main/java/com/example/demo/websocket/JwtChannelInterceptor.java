package com.example.demo.websocket;

import java.util.List;
import java.util.UUID;

import org.springframework.lang.NonNull;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.MessagingException;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

import com.example.demo.security.JwtService;

@Component
public class JwtChannelInterceptor implements ChannelInterceptor {

    private static final String PREFIX = "Bearer ";

    private final JwtService jwtService;

    public JwtChannelInterceptor(JwtService jwtService) {
        this.jwtService = jwtService;
    }

    @Override
    public Message<?> preSend(@NonNull Message<?> message, @NonNull MessageChannel channel) {
        StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);

        if (accessor != null && StompCommand.CONNECT.equals(accessor.getCommand())) {
            String header = accessor.getFirstNativeHeader("Authorization");

            if (header == null || !header.startsWith(PREFIX)) {
                throw new MessagingException("Header Authorization mancante nella CONNECT STOMP");
            }

            String token = header.substring(PREFIX.length());

            if (!jwtService.isTokenValido(token)) {
                throw new MessagingException("Token JWT non valido");
            }

            UUID userId = jwtService.estraiUserId(token);

            var authentication = new UsernamePasswordAuthenticationToken(
                    userId, null, List.of(new SimpleGrantedAuthority("ROLE_USER")));

            // mutazione in-place sull'accessor originale (leaveMutable):
            // StompSubProtocolHandler legge questo stesso riferimento per mettere in cache
            // l'utente della sessione e riapplicarlo ai frame successivi (SEND, ecc.)
            accessor.setUser(authentication);
        }

        return message;
    }
}
