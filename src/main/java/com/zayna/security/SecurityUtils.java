package com.zayna.security;

import com.zayna.user.enums.Role;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Optional;

public final class SecurityUtils {

    private SecurityUtils() {
    }

    public static Optional<CustomUserDetails> getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()
                || !(authentication.getPrincipal() instanceof CustomUserDetails details)) {
            return Optional.empty();
        }
        return Optional.of(details);
    }

    public static CustomUserDetails requireCurrentUser() {
        return getCurrentUser().orElseThrow(
                () -> new AuthenticationCredentialsNotFoundException("Authentication required"));
    }

    public static Long requireCurrentUserId() {
        return requireCurrentUser().getId();
    }

    public static boolean hasRole(Role role) {
        return getCurrentUser().map(u -> u.getRole() == role).orElse(false);
    }

    public static boolean isAdmin() {
        return hasRole(Role.ADMIN);
    }
}