package com.zayna.user.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
public class EmailServiceImpl implements IEmailService {

    private static final Logger log = LoggerFactory.getLogger(EmailServiceImpl.class);

    private final ObjectProvider<JavaMailSender> mailSenderProvider;
    private final String mailHost;
    private final String from;
    private final String linkBase;
    private final boolean devLogLinks;

    public EmailServiceImpl(ObjectProvider<JavaMailSender> mailSenderProvider,
                            @Value("${spring.mail.host:}") String mailHost,
                            @Value("${spring.mail.username:}") String mailUsername,
                            @Value("${app.mail.from:}") String mailFrom,
                            @Value("${app.password-reset.link-base:zayna://reset-password}") String linkBase,
                            @Value("${app.mail.dev-log-links:false}") boolean devLogLinks) {
        this.mailSenderProvider = mailSenderProvider;
        this.mailHost = mailHost == null ? "" : mailHost.trim();
        this.from = (mailFrom == null || mailFrom.isBlank())
                ? (mailUsername == null ? "" : mailUsername.trim())
                : mailFrom.trim();
        this.linkBase = linkBase;
        this.devLogLinks = devLogLinks;
    }

    @Override
    @Async
    public void sendPasswordResetEmail(String to, String firstName, String token, long validityMinutes) {
        String link = linkBase + (linkBase.contains("?") ? "&" : "?") + "token=" + token;

        JavaMailSender sender = mailSenderProvider.getIfAvailable();
        if (sender == null || mailHost.isEmpty()) {
            if (devLogLinks) {
                log.warn("[DEV ONLY] SMTP not configured. Password reset for {} -> link: {} | token: {}",
                        to, link, token);
            } else {
                log.warn("SMTP not configured (SPRING_MAIL_HOST is empty): password reset email to {} was NOT sent.", to);
            }
            return;
        }

        try {
            SimpleMailMessage message = new SimpleMailMessage();
            if (!from.isEmpty()) {
                message.setFrom(from);
            }
            message.setTo(to);
            message.setSubject("ZAYNA - Reset your password");
            message.setText("Hello " + firstName + ",\n\n"
                    + "We received a request to reset your ZAYNA password.\n\n"
                    + "Open this link on your phone: " + link + "\n\n"
                    + "Or copy this code into the app: " + token + "\n\n"
                    + "This link is valid for " + validityMinutes + " minutes and can be used only once.\n"
                    + "If you did not request this, you can safely ignore this email.\n\n"
                    + "The ZAYNA team");
            sender.send(message);
        } catch (MailException ex) {
            log.error("Failed to send password reset email to {}: {}", to, ex.getMessage());
        }
    }
}