package com.zayna.user.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record AdminUpdateUserRequest(
        @Size(min = 1, max = 50, message = "First name must be between 1 and 50 characters")
        String firstName,

        @Size(min = 1, max = 50, message = "Last name must be between 1 and 50 characters")
        String lastName,

        @Pattern(regexp = ValidationPatterns.USERNAME, message = ValidationPatterns.USERNAME_MESSAGE)
        String username,

        @Email(message = "Invalid email format")
        @Size(max = 150, message = "Email must not exceed 150 characters")
        String email,

        @Pattern(regexp = ValidationPatterns.PHONE, message = ValidationPatterns.PHONE_MESSAGE)
        String phoneNumber
) {
}