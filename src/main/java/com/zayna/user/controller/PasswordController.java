package com.zayna.user.controller;

import com.zayna.user.dto.ChangePasswordRequest;
import com.zayna.user.dto.ForgotPasswordRequest;
import com.zayna.user.dto.MessageResponse;
import com.zayna.user.dto.ResetPasswordRequest;
import com.zayna.user.service.IPasswordService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class PasswordController {

    private final IPasswordService passwordService;

    public PasswordController(IPasswordService passwordService) {
        this.passwordService = passwordService;
    }

    @PostMapping("/api/auth/forgot-password")
    public ResponseEntity<MessageResponse> forgotPassword(@Valid @RequestBody ForgotPasswordRequest request) {
        passwordService.requestPasswordReset(request.email());
        return ResponseEntity.ok(new MessageResponse(
                "If an account exists for this email, a reset link has been sent"));
    }

    @PostMapping("/api/auth/reset-password")
    public ResponseEntity<MessageResponse> resetPassword(@Valid @RequestBody ResetPasswordRequest request) {
        passwordService.resetPassword(request.token(), request.newPassword());
        return ResponseEntity.ok(new MessageResponse(
                "Password reset successfully. Please log in with your new password"));
    }

    @PutMapping("/api/users/me/change-password")
    public ResponseEntity<MessageResponse> changePassword(@Valid @RequestBody ChangePasswordRequest request) {
        passwordService.changePassword(request);
        return ResponseEntity.ok(new MessageResponse(
                "Password changed successfully. Please log in again"));
    }
}