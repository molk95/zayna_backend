package com.zayna.config;

import com.zayna.user.dto.ValidationPatterns;
import com.zayna.user.entity.User;
import com.zayna.user.enums.AccountStatus;
import com.zayna.user.enums.Role;
import com.zayna.user.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Locale;

@Component
public class DataInitializer implements ApplicationRunner {

    private static final Logger log = LoggerFactory.getLogger(DataInitializer.class);

    private final AdminProperties adminProperties;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public DataInitializer(AdminProperties adminProperties,
                           UserRepository userRepository,
                           PasswordEncoder passwordEncoder) {
        this.adminProperties = adminProperties;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(ApplicationArguments args) {
        if (userRepository.existsByRole(Role.ADMIN)) {
            return;
        }

        String email = trimToNull(adminProperties.email());
        String username = trimToNull(adminProperties.username());
        String password = adminProperties.password();

        if (email == null || username == null || password == null || password.isBlank()) {
            log.warn("No ADMIN exists and ADMIN_EMAIL / ADMIN_USERNAME / ADMIN_PASSWORD are not all defined: "
                    + "initial administrator NOT created.");
            return;
        }
        email = email.toLowerCase(Locale.ROOT);

        if (!email.contains("@") || !username.matches(ValidationPatterns.USERNAME)
                || !password.matches(ValidationPatterns.PASSWORD)) {
            log.error("Initial administrator NOT created: ADMIN_EMAIL, ADMIN_USERNAME or ADMIN_PASSWORD is invalid "
                    + "(the password must be 8-64 characters with an uppercase letter, a lowercase letter, "
                    + "a digit and a special character).");
            return;
        }
        if (userRepository.existsByEmailIgnoreCase(email) || userRepository.existsByUsernameIgnoreCase(username)) {
            log.error("Initial administrator NOT created: this email or username is already used by another account.");
            return;
        }

        User admin = new User();
        admin.setFirstName(valueOrDefault(adminProperties.firstName(), "Admin"));
        admin.setLastName(valueOrDefault(adminProperties.lastName(), "ZAYNA"));
        admin.setUsername(username);
        admin.setEmail(email);
        admin.setPasswordHash(passwordEncoder.encode(password));
        admin.setRole(Role.ADMIN);
        admin.setAccountStatus(AccountStatus.ACTIVE);
        userRepository.save(admin);

        log.info("Initial administrator created (username: {}).", username);
    }

    private String trimToNull(String value) {
        return (value == null || value.isBlank()) ? null : value.trim();
    }

    private String valueOrDefault(String value, String defaultValue) {
        return (value == null || value.isBlank()) ? defaultValue : value.trim();
    }
}