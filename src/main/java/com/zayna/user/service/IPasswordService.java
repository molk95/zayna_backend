package com.zayna.user.service;

import com.zayna.user.dto.ChangePasswordRequest;

public interface IPasswordService {

    /** Utilisateur connecté : change son mot de passe (ancien mot de passe requis). */
    void changePassword(ChangePasswordRequest request);

    /** Public : demande de réinitialisation. Ne révèle jamais si l'email existe. */
    void requestPasswordReset(String email);

    /** Public : réinitialise le mot de passe avec le token reçu par email. */
    void resetPassword(String token, String newPassword);
}