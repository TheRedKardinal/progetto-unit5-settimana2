package com.example.demo.service;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.example.demo.dto.user.UserSummaryResponse;
import com.example.demo.repository.UserRepository;

@Service
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public List<UserSummaryResponse> cercaUtenti(UUID currentUserId, String query) {
        String termine = query == null ? "" : query.trim();

        return userRepository.findByIdNotAndUsernameContainingIgnoreCaseOrderByUsernameAsc(currentUserId, termine)
                .stream()
                .map(UserSummaryResponse::from)
                .toList();
    }
}
