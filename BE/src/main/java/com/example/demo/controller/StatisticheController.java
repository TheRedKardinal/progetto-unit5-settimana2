package com.example.demo.controller;

import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.dto.statistiche.StatisticheResponse;
import com.example.demo.service.StatisticheService;

@RestController
@RequestMapping("/api/utenti/me")
public class StatisticheController {

    private final StatisticheService statisticheService;

    public StatisticheController(StatisticheService statisticheService) {
        this.statisticheService = statisticheService;
    }

    @PostMapping("/statistiche")
    public ResponseEntity<StatisticheResponse> statistiche(@AuthenticationPrincipal UUID currentUserId) {
        return ResponseEntity.ok(statisticheService.calcolaEInviaEmail(currentUserId));
    }
}
