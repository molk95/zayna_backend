package com.zayna.contact.dto;

import com.zayna.contact.enumeration.ContactStatus;
import jakarta.validation.constraints.NotNull;

public record ContactStatusRequest(
        @NotNull(message = "Le statut est obligatoire")
        ContactStatus status) {
}
