package com.zayna.contact.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record ContactRequest(
        @NotBlank(message = "Le nom est obligatoire")
        @Size(max = 100, message = "Le nom ne doit pas dépasser 100 caractères")
        String fullName,

        @NotBlank(message = "L'email est obligatoire")
        @Email(message = "L'email n'est pas valide")
        @Size(max = 150, message = "L'email ne doit pas dépasser 150 caractères")
        String email,

        @Pattern(regexp = "^[0-9+ ()-]{6,30}$", message = "Le numéro de téléphone n'est pas valide")
        String phone,

        @NotBlank(message = "Le sujet est obligatoire")
        @Size(max = 150, message = "Le sujet ne doit pas dépasser 150 caractères")
        String subject,

        @NotBlank(message = "Le message est obligatoire")
        @Size(max = 2000, message = "Le message ne doit pas dépasser 2000 caractères")
        String message) {
}
