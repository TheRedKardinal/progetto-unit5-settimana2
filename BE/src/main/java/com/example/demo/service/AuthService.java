package com.example.demo.service;

import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.demo.dto.auth.AuthResponse;
import com.example.demo.dto.auth.LoginRequest;
import com.example.demo.dto.auth.RegisterRequest;
import com.example.demo.entity.User;
import com.example.demo.exception.ConflictException;
import com.example.demo.repository.UserRepository;
import com.example.demo.security.JwtService;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder, JwtService jwtService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    public AuthResponse registra(RegisterRequest request) {
        if (userRepository.existsByUsername(request.username())) {
            throw new ConflictException("Username già in uso");
        }
        if (userRepository.existsByEmail(request.email())) {
            throw new ConflictException("Email già in uso");
        }

        User utente = User.builder()
                .username(request.username())
                .email(request.email())
                .password(passwordEncoder.encode(request.password()))
                .build();

        utente = userRepository.save(utente);

        String token = jwtService.generaToken(utente.getId(), utente.getUsername());
        return new AuthResponse(token, utente.getId(), utente.getUsername());
    }

    public AuthResponse login(LoginRequest request) {
        User utente = userRepository.findByUsername(request.username())
                .orElseThrow(() -> new BadCredentialsException("Credenziali non valide"));

        if (!passwordEncoder.matches(request.password(), utente.getPassword())) {
            throw new BadCredentialsException("Credenziali non valide");
        }

        String token = jwtService.generaToken(utente.getId(), utente.getUsername());
        return new AuthResponse(token, utente.getId(), utente.getUsername());
    }
}
