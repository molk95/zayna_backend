package com.zayna.user.dto;

import com.zayna.user.enums.RequestStatus;

import java.time.LocalDateTime;

public record OrganizerRequestDTO(
        Long id,
        Long userId,
        String username,
        RequestStatus status,
        String message,
        String adminComment,
        LocalDateTime createdAt,
        LocalDateTime reviewedAt
) {
}