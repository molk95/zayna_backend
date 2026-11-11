package com.zayna.user.dto;

import com.zayna.user.enums.AccountStatus;
import com.zayna.user.enums.Role;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Map;

public record UserDTO(
        Long id,
        String firstName,
        String lastName,
        String username,
        String email,
        String phoneNumber,
        String profileImageUrl,
        String bio,
        String location,
        LocalDate dateOfBirth,
        String profession,
        String speciality,
        String personalStyle,
        Map<String, String> socialMediaLinks,
        String portfolioUrl,
        Role role,
        AccountStatus accountStatus,
        LocalDateTime createdAt,
        LocalDateTime updatedAt,
        LocalDateTime lastLoginAt
) {
}