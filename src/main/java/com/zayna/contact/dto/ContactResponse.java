package com.zayna.contact.dto;

import com.zayna.contact.entity.ContactMessage;
import com.zayna.contact.enumeration.ContactStatus;

import java.time.Instant;

public record ContactResponse(
        Long id,
        String fullName,
        String email,
        String phone,
        String subject,
        String message,
        ContactStatus status,
        Instant createdAt) {

    public static ContactResponse from(ContactMessage entity) {
        return new ContactResponse(
                entity.getId(),
                entity.getFullName(),
                entity.getEmail(),
                entity.getPhone(),
                entity.getSubject(),
                entity.getMessage(),
                entity.getStatus(),
                entity.getCreatedAt());
    }
}
