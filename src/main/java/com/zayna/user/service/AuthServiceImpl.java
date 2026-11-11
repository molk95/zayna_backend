package com.zayna.user.service;

import com.zayna.exception.AccountNotActiveException;
import com.zayna.exception.DuplicateResourceException;
import com.zayna.exception.InvalidTokenException;
import com.zayna.security.JwtService;
import com.zayna.security.TokenUtils;
import com.zayna.user.dto.AuthResponse;
import com.zayna.user.dto.LoginRequest;
import com.zayna.user.dto.RegisterRequest;
import com.zayna.user.entity.RefreshToken;
import com.zayna.user.entity.User;
import com.zayna.user.enums.AccountStatus;
import com.zayna.user.enums.Role;
import com.zayna.user.mapper.UserMapper;
import com.zayna.user.repository.RefreshTokenRepository;
import com.zayna.user.repository.UserRepository;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Locale;

@Service
public class AuthServiceImpl implements IAuthService {

    private final UserRepository userRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final UserMapper userMapper;

    /** Faux hachage utilisé quand l'utilisateur n'existe pas, pour garder un temps de réponse constant. */
    private final String dummyHash;

    public AuthServiceImpl(UserRepository userRepository,
                           RefreshTokenRepository refreshTokenRepository,
                           PasswordEncoder passwordEncoder,
                           JwtService jwtService,
                           UserMapper userMapper) {
        this.userRepository = userRepository;
        this.refreshTokenRepository = refreshTokenRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.userMapper = userMapper;
        this.dummyHash = passwordEncoder.encode("zayna-dummy-password");
    }

    @Override
    @Transactional
    public AuthResponse register(RegisterRequest request) {
        String email = request.email().trim().toLowerCase(Locale.ROOT);
        String username = request.username().trim();

        if (userRepository.existsByEmailIgnoreCase(email)) {
            throw new DuplicateResourceException("Email already exists");
        }
        if (userRepository.existsByUsernameIgnoreCase(username)) {
            throw new DuplicateResourceException("Username already exists");
        }

        User user = new User();
        user.setFirstName(request.firstName().trim());
        user.setLastName(request.lastName().trim());
        user.setUsername(username);
        user.setEmail(email);
        user.setPasswordHash(passwordEncoder.encode(request.password()));
        if (request.phoneNumber() != null && !request.phoneNumber().isBlank()) {
            user.setPhoneNumber(request.phoneNumber().trim());
        }
        // Le rôle n'est JAMAIS lu depuis la requête : toujours CLIENT à l'inscription.
        user.setRole(Role.CLIENT);
        user.setAccountStatus(AccountStatus.ACTIVE);
        user.setLastLoginAt(LocalDateTime.now());

        try {
            user = userRepository.saveAndFlush(user);
        } catch (DataIntegrityViolationException ex) {
            // Cas de concurrence : deux inscriptions simultanées avec le même email/username
            throw new DuplicateResourceException("Email or username already exists");
        }

        return buildAuthResponse(user, issueRefreshToken(user));
    }

    @Override
    @Transactional
    public AuthResponse login(LoginRequest request) {
        String identifier = request.identifier().trim();

        User user = userRepository
                .findByEmailIgnoreCaseOrUsernameIgnoreCase(identifier, identifier)
                .orElse(null);

        String hashToCheck = (user != null) ? user.getPasswordHash() : dummyHash;
        boolean passwordMatches = passwordEncoder.matches(request.password(), hashToCheck);

        if (user == null || !passwordMatches || user.getAccountStatus() == AccountStatus.DELETED) {
            throw new BadCredentialsException("Invalid credentials");
        }
        ensureAccountActive(user);

        user.setLastLoginAt(LocalDateTime.now());
        userRepository.save(user);

        return buildAuthResponse(user, issueRefreshToken(user));
    }

    @Override
    @Transactional(noRollbackFor = InvalidTokenException.class)
    public AuthResponse refresh(String refreshToken) {
        String hash = TokenUtils.sha256Hex(refreshToken.trim());
        RefreshToken stored = refreshTokenRepository.findByTokenHash(hash)
                .orElseThrow(() -> new InvalidTokenException("Invalid refresh token"));

        if (stored.isRevoked()) {
            // Réutilisation d'un token déjà utilisé : vol possible, on coupe toutes les sessions.
            refreshTokenRepository.revokeAllByUserId(stored.getUser().getId());
            throw new InvalidTokenException("Refresh token has already been used or revoked");
        }
        if (stored.isExpired()) {
            throw new InvalidTokenException("Refresh token expired, please log in again");
        }

        User user = stored.getUser();
        ensureAccountActive(user);

        // Rotation : l'ancien token est révoqué, un nouveau est émis.
        String newRaw = TokenUtils.generateOpaqueToken();
        String newHash = TokenUtils.sha256Hex(newRaw);
        refreshTokenRepository.save(new RefreshToken(newHash, user, newExpiration()));

        stored.setRevoked(true);
        stored.setReplacedByHash(newHash);
        refreshTokenRepository.save(stored);

        return buildAuthResponse(user, newRaw);
    }

    @Override
    @Transactional
    public void logout(String refreshToken) {
        String hash = TokenUtils.sha256Hex(refreshToken.trim());
        refreshTokenRepository.findByTokenHash(hash).ifPresent(token -> {
            if (!token.isRevoked()) {
                token.setRevoked(true);
                refreshTokenRepository.save(token);
            }
        });
    }

    // ------------------------------------------------------------------

    private void ensureAccountActive(User user) {
        switch (user.getAccountStatus()) {
            case ACTIVE -> { }
            case DISABLED -> throw new AccountNotActiveException("Your account is disabled");
            case SUSPENDED -> throw new AccountNotActiveException("Your account is suspended");
            case DELETED -> throw new AccountNotActiveException("This account no longer exists");
        }
    }

    private String issueRefreshToken(User user) {
        String raw = TokenUtils.generateOpaqueToken();
        refreshTokenRepository.save(new RefreshToken(TokenUtils.sha256Hex(raw), user, newExpiration()));
        return raw;
    }

    private LocalDateTime newExpiration() {
        return LocalDateTime.now().plusDays(jwtService.getRefreshTokenExpirationDays());
    }

    private AuthResponse buildAuthResponse(User user, String rawRefreshToken) {
        return new AuthResponse(
                jwtService.generateAccessToken(user),
                rawRefreshToken,
                "Bearer",
                jwtService.getAccessTokenExpirationSeconds(),
                userMapper.toDTO(user)
        );
    }
}