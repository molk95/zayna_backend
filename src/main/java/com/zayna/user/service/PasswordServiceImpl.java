package com.zayna.user.service;

import com.zayna.exception.BadRequestException;
import com.zayna.exception.ResourceNotFoundException;
import com.zayna.security.SecurityUtils;
import com.zayna.security.TokenUtils;
import com.zayna.user.dto.ChangePasswordRequest;
import com.zayna.user.entity.PasswordResetToken;
import com.zayna.user.entity.User;
import com.zayna.user.enums.AccountStatus;
import com.zayna.user.repository.PasswordResetTokenRepository;
import com.zayna.user.repository.RefreshTokenRepository;
import com.zayna.user.repository.UserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import java.time.LocalDateTime;
import java.util.Locale;

@Service
public class PasswordServiceImpl implements IPasswordService {

    private static final String INVALID_RESET_TOKEN = "Invalid or expired reset token";

    private final UserRepository userRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final PasswordResetTokenRepository resetTokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final IEmailService emailService;
    private final long resetExpirationMinutes;

    public PasswordServiceImpl(UserRepository userRepository,
                               RefreshTokenRepository refreshTokenRepository,
                               PasswordResetTokenRepository resetTokenRepository,
                               PasswordEncoder passwordEncoder,
                               IEmailService emailService,
                               @Value("${app.password-reset.expiration-minutes:30}") long resetExpirationMinutes) {
        this.userRepository = userRepository;
        this.refreshTokenRepository = refreshTokenRepository;
        this.resetTokenRepository = resetTokenRepository;
        this.passwordEncoder = passwordEncoder;
        this.emailService = emailService;
        this.resetExpirationMinutes = resetExpirationMinutes;
    }

    @Override
    @Transactional
    public void changePassword(ChangePasswordRequest request) {
        User user = userRepository.findById(SecurityUtils.requireCurrentUserId())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        if (!passwordEncoder.matches(request.oldPassword(), user.getPasswordHash())) {
            throw new BadRequestException("Old password is incorrect");
        }
        if (passwordEncoder.matches(request.newPassword(), user.getPasswordHash())) {
            throw new BadRequestException("New password must be different from the old password");
        }
        applyNewPassword(user, request.newPassword());
    }

    @Override
    @Transactional
    public void requestPasswordReset(String email) {
        String normalized = email.trim().toLowerCase(Locale.ROOT);

        // Aucune erreur si l'email n'existe pas ou si le compte n'est pas actif (pas d'énumération de comptes).
        userRepository.findByEmailIgnoreCase(normalized)
                .filter(u -> u.getAccountStatus() == AccountStatus.ACTIVE)
                .ifPresent(this::createTokenAndSendEmail);
    }

    @Override
    @Transactional
    public void resetPassword(String token, String newPassword) {
        PasswordResetToken stored = resetTokenRepository
                .findByTokenHash(TokenUtils.sha256Hex(token.trim()))
                .orElseThrow(() -> new BadRequestException(INVALID_RESET_TOKEN));

        if (stored.isUsed() || stored.isExpired()) {
            throw new BadRequestException(INVALID_RESET_TOKEN);
        }
        User user = stored.getUser();
        if (user.getAccountStatus() != AccountStatus.ACTIVE) {
            throw new BadRequestException(INVALID_RESET_TOKEN);
        }

        stored.setUsed(true);
        resetTokenRepository.save(stored);
        applyNewPassword(user, newPassword);
    }

    // ------------------------------------------------------------------

    private void createTokenAndSendEmail(User user) {
        // Un seul token valide à la fois par utilisateur.
        resetTokenRepository.invalidateAllByUserId(user.getId());

        String rawToken = TokenUtils.generateOpaqueToken();
        resetTokenRepository.save(new PasswordResetToken(
                TokenUtils.sha256Hex(rawToken),
                user,
                LocalDateTime.now().plusMinutes(resetExpirationMinutes)));

        String to = user.getEmail();
        String firstName = user.getFirstName();

        // L'email n'est envoyé qu'une fois la transaction validée.
        TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
            @Override
            public void afterCommit() {
                emailService.sendPasswordResetEmail(to, firstName, rawToken, resetExpirationMinutes);
            }
        });
    }

    /**
     * Applique le nouveau mot de passe et coupe toutes les sessions.
     * saveAndFlush AVANT les requêtes de révocation, car elles vident le contexte de persistance.
     */
    private void applyNewPassword(User user, String newPassword) {
        user.setPasswordHash(passwordEncoder.encode(newPassword));
        user.incrementTokenVersion();
        userRepository.saveAndFlush(user);
        refreshTokenRepository.revokeAllByUserId(user.getId());
        resetTokenRepository.invalidateAllByUserId(user.getId());
    }
}