package com.example.demo.security;

import java.util.Date;
import java.util.UUID;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

@Service
public class JwtService {

    private final SecretKey secretKey;
    private final long expirationMs;

    public JwtService(@Value("${jwt.secret}") String secret,
            @Value("${jwt.expiration-ms}") long expirationMs) {
        this.secretKey = Keys.hmacShaKeyFor(secret.getBytes());
        this.expirationMs = expirationMs;
    }

    public String generaToken(UUID userId, String username) {
        Date ora = new Date();
        Date scadenza = new Date(ora.getTime() + expirationMs);
        return Jwts.builder()
                .subject(userId.toString())
                .claim("username", username)
                .issuedAt(ora)
                .expiration(scadenza)
                .signWith(secretKey)
                .compact();
    }

    public UUID estraiUserId(String token) {
        return UUID.fromString(estraiClaims(token).getSubject());
    }

    public String estraiUsername(String token) {
        return estraiClaims(token).get("username", String.class);
    }

    public boolean isTokenValido(String token) {
        try {
            Claims claims = estraiClaims(token);
            return claims.getExpiration().after(new Date());
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }

    private Claims estraiClaims(String token) {
        return Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
}
