package com.example.demo.controller;

import java.util.List;
import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.dto.user.UserSummaryResponse;
import com.example.demo.service.UserService;

@RestController
@RequestMapping("/api/utenti")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public ResponseEntity<List<UserSummaryResponse>> cerca(@AuthenticationPrincipal UUID currentUserId,
            @RequestParam(name = "q", required = false, defaultValue = "") String query) {
        return ResponseEntity.ok(userService.cercaUtenti(currentUserId, query));
    }
}
