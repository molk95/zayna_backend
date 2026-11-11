package com.zayna.user.dto;

import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;
import java.util.Map;

public record UpdateProfileRequest(
        @Size(min = 1, max = 50, message = "First name must be between 1 and 50 characters")
        String firstName,

        @Size(min = 1, max = 50, message = "Last name must be between 1 and 50 characters")
        String lastName,

        @Pattern(regexp = ValidationPatterns.PHONE, message = ValidationPatterns.PHONE_MESSAGE)
        String phoneNumber,

        @Size(max = 500, message = "Bio must not exceed 500 characters")
        String bio,

        @Size(max = 100, message = "Location must not exceed 100 characters")
        String location,

        @Past(message = "Date of birth must be in the past")
        LocalDate dateOfBirth,

        @Size(max = 100, message = "Profession must not exceed 100 characters")
        String profession,

        @Size(max = 100, message = "Speciality must not exceed 100 characters")
        String speciality,

        @Size(max = 100, message = "Personal style must not exceed 100 characters")
        String personalStyle,

        @Size(max = 10, message = "Too many social media links (max 10)")
        Map<@Size(max = 50, message = "Platform name too long") String,
                @Size(max = 255, message = "Social media URL too long") String> socialMediaLinks,

        @Pattern(regexp = ValidationPatterns.URL, message = ValidationPatterns.URL_MESSAGE)
        String portfolioUrl
) {
}