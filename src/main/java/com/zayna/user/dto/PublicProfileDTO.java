package com.zayna.user.dto;

import com.zayna.user.enums.Role;

import java.time.LocalDateTime;
import java.util.Map;

public record PublicProfileDTO(
        Long id,
        String username,
        String firstName,
        String lastName,
        String profileImageUrl,
        String bio,
        String location,
        String profession,
        String speciality,
        String personalStyle,
        Map<String, String> socialMediaLinks,
        String portfolioUrl,
        Role role,
        LocalDateTime createdAt
) {
}