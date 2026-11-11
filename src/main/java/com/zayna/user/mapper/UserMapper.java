package com.zayna.user.mapper;

import com.zayna.user.dto.OrganizerRequestDTO;
import com.zayna.user.dto.PublicProfileDTO;
import com.zayna.user.dto.UserDTO;
import com.zayna.user.entity.OrganizerRequest;
import com.zayna.user.entity.User;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class UserMapper {

    public UserDTO toDTO(User user) {
        return new UserDTO(
                user.getId(),
                user.getFirstName(),
                user.getLastName(),
                user.getUsername(),
                user.getEmail(),
                user.getPhoneNumber(),
                imageUrl(user),
                user.getBio(),
                user.getLocation(),
                user.getDateOfBirth(),
                user.getProfession(),
                user.getSpeciality(),
                user.getPersonalStyle(),
                copyLinks(user),
                user.getPortfolioUrl(),
                user.getRole(),
                user.getAccountStatus(),
                user.getCreatedAt(),
                user.getUpdatedAt(),
                user.getLastLoginAt()
        );
    }

    public PublicProfileDTO toPublicProfile(User user) {
        return new PublicProfileDTO(
                user.getId(),
                user.getUsername(),
                user.getFirstName(),
                user.getLastName(),
                imageUrl(user),
                user.getBio(),
                user.getLocation(),
                user.getProfession(),
                user.getSpeciality(),
                user.getPersonalStyle(),
                copyLinks(user),
                user.getPortfolioUrl(),
                user.getRole(),
                user.getCreatedAt()
        );
    }

    public OrganizerRequestDTO toDTO(OrganizerRequest request) {
        return new OrganizerRequestDTO(
                request.getId(),
                request.getUser().getId(),
                request.getUser().getUsername(),
                request.getStatus(),
                request.getMessage(),
                request.getAdminComment(),
                request.getCreatedAt(),
                request.getReviewedAt()
        );
    }

    private String imageUrl(User user) {
        if (user.getProfileImage() == null || user.getProfileImage().isBlank()) {
            return null;
        }
        return "/api/users/" + user.getId() + "/profile-image?v=" + user.getProfileImage();
    }

    private Map<String, String> copyLinks(User user) {
        return user.getSocialMediaLinks() == null
                ? new HashMap<>()
                : new HashMap<>(user.getSocialMediaLinks());
    }
}