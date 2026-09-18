package com.example.demo.dto.user;

import java.util.UUID;

import com.example.demo.entity.User;

public record UserSummaryResponse(UUID id, String username) {

    public static UserSummaryResponse from(User user) {
        return new UserSummaryResponse(user.getId(), user.getUsername());
    }
}
