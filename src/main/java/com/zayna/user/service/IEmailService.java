package com.zayna.user.service;

public interface IEmailService {

    /**
     * Envoie (de façon asynchrone) l'email de réinitialisation de mot de passe.
     *
     * @param token valeur brute du token, à ne jamais journaliser en production
     */
    void sendPasswordResetEmail(String to, String firstName, String token, long validityMinutes);
}