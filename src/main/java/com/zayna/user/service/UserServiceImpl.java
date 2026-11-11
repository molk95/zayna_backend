package com.zayna.user.service;

import com.zayna.exception.BadRequestException;
import com.zayna.exception.DuplicateResourceException;
import com.zayna.exception.ResourceNotFoundException;
import com.zayna.security.SecurityUtils;
import com.zayna.user.dto.AdminUpdateUserRequest;
import com.zayna.user.dto.PageResponse;
import com.zayna.user.dto.PublicProfileDTO;
import com.zayna.user.dto.UpdateProfileRequest;
import com.zayna.user.dto.UserDTO;
import com.zayna.user.entity.User;
import com.zayna.user.enums.AccountStatus;
import com.zayna.user.enums.Role;
import com.zayna.user.mapper.UserMapper;
import com.zayna.user.repository.RefreshTokenRepository;
import com.zayna.user.repository.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Locale;
import java.util.Set;

@Service
public class UserServiceImpl implements IUserService {

    private static final int MAX_PAGE_SIZE = 100;
    private static final Set<String> SORTABLE_FIELDS = Set.of(
            "id", "username", "email", "firstName", "lastName",
            "role", "accountStatus", "createdAt", "lastLoginAt");

    private final UserRepository userRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final UserMapper userMapper;

    public UserServiceImpl(UserRepository userRepository,
                           RefreshTokenRepository refreshTokenRepository,
                           UserMapper userMapper) {
        this.userRepository = userRepository;
        this.refreshTokenRepository = refreshTokenRepository;
        this.userMapper = userMapper;
    }

    // ================= Utilisateur connecté =================

    @Override
    @Transactional(readOnly = true)
    public UserDTO getMyProfile() {
        return userMapper.toDTO(findOrThrow(SecurityUtils.requireCurrentUserId()));
    }

    @Override
    @Transactional
    public UserDTO updateMyProfile(UpdateProfileRequest request) {
        User user = findOrThrow(SecurityUtils.requireCurrentUserId());

        // Modification partielle : seuls les champs non null sont appliqués.
        // Email, username, rôle et statut ne sont volontairement pas modifiables ici.
        if (request.firstName() != null) {
            user.setFirstName(requireNotBlank(request.firstName(), "First name"));
        }
        if (request.lastName() != null) {
            user.setLastName(requireNotBlank(request.lastName(), "Last name"));
        }
        if (request.phoneNumber() != null) {
            user.setPhoneNumber(request.phoneNumber().trim());
        }
        if (request.bio() != null) {
            user.setBio(request.bio().trim());
        }
        if (request.location() != null) {
            user.setLocation(request.location().trim());
        }
        if (request.dateOfBirth() != null) {
            user.setDateOfBirth(request.dateOfBirth());
        }
        if (request.profession() != null) {
            user.setProfession(request.profession().trim());
        }
        if (request.speciality() != null) {
            user.setSpeciality(request.speciality().trim());
        }
        if (request.personalStyle() != null) {
            user.setPersonalStyle(request.personalStyle().trim());
        }
        if (request.portfolioUrl() != null) {
            user.setPortfolioUrl(request.portfolioUrl().trim());
        }
        if (request.socialMediaLinks() != null) {
            user.getSocialMediaLinks().clear();
            request.socialMediaLinks().forEach((platform, url) -> {
                if (platform != null && !platform.isBlank() && url != null && !url.isBlank()) {
                    user.getSocialMediaLinks().put(platform.trim(), url.trim());
                }
            });
        }

        return userMapper.toDTO(userRepository.save(user));
    }

    // ================= Profils des autres membres =================

    @Override
    @Transactional(readOnly = true)
    public PublicProfileDTO getPublicProfile(Long id) {
        User user = findOrThrow(id);
        if (user.getAccountStatus() != AccountStatus.ACTIVE) {
            throw new ResourceNotFoundException("User not found");
        }
        return userMapper.toPublicProfile(user);
    }

    // ================= Administration =================

    @Override
    @Transactional(readOnly = true)
    public PageResponse<UserDTO> searchUsers(String search, Role role, AccountStatus status,
                                             int page, int size, String sortBy, String direction) {
        Specification<User> spec = Specification.where(null);

        if (search != null && !search.isBlank()) {
            String like = "%" + search.trim().toLowerCase(Locale.ROOT) + "%";
            spec = spec.and((root, query, cb) -> cb.or(
                    cb.like(cb.lower(root.get("username")), like),
                    cb.like(cb.lower(root.get("email")), like),
                    cb.like(cb.lower(root.get("firstName")), like),
                    cb.like(cb.lower(root.get("lastName")), like)));
        }
        if (role != null) {
            spec = spec.and((root, query, cb) -> cb.equal(root.get("role"), role));
        }
        if (status != null) {
            spec = spec.and((root, query, cb) -> cb.equal(root.get("accountStatus"), status));
        }

        String sortField = SORTABLE_FIELDS.contains(sortBy) ? sortBy : "createdAt";
        Sort.Direction sortDirection = "asc".equalsIgnoreCase(direction) ? Sort.Direction.ASC : Sort.Direction.DESC;
        Pageable pageable = PageRequest.of(
                Math.max(page, 0),
                Math.min(Math.max(size, 1), MAX_PAGE_SIZE),
                Sort.by(sortDirection, sortField));

        Page<User> result = userRepository.findAll(spec, pageable);
        return PageResponse.from(result.map(u -> userMapper.toDTO(u)));
    }

    @Override
    @Transactional(readOnly = true)
    public UserDTO getUserById(Long id) {
        return userMapper.toDTO(findOrThrow(id));
    }

    @Override
    @Transactional
    public UserDTO updateUser(Long id, AdminUpdateUserRequest request) {
        User user = findOrThrow(id);

        if (request.firstName() != null) {
            user.setFirstName(requireNotBlank(request.firstName(), "First name"));
        }
        if (request.lastName() != null) {
            user.setLastName(requireNotBlank(request.lastName(), "Last name"));
        }
        if (request.username() != null) {
            String username = request.username().trim();
            if (userRepository.existsByUsernameIgnoreCaseAndIdNot(username, id)) {
                throw new DuplicateResourceException("Username already exists");
            }
            user.setUsername(username);
        }
        if (request.email() != null) {
            String email = request.email().trim().toLowerCase(Locale.ROOT);
            if (userRepository.existsByEmailIgnoreCaseAndIdNot(email, id)) {
                throw new DuplicateResourceException("Email already exists");
            }
            user.setEmail(email);
        }
        if (request.phoneNumber() != null) {
            user.setPhoneNumber(request.phoneNumber().trim());
        }

        return userMapper.toDTO(userRepository.save(user));
    }

    @Override
    @Transactional
    public UserDTO changeRole(Long id, Role role) {
        ensureNotSelf(id, "change your own role");
        User user = findOrThrow(id);

        if (user.getRole() == role) {
            return userMapper.toDTO(user);
        }
        user.setRole(role);
        // Le rôle est lu depuis la base à chaque requête, mais on coupe aussi les sessions existantes.
        return saveAndRevokeSessions(user);
    }

    @Override
    @Transactional
    public UserDTO changeStatus(Long id, AccountStatus status) {
        ensureNotSelf(id, "change your own account status");
        User user = findOrThrow(id);

        user.setAccountStatus(status);
        if (status == AccountStatus.ACTIVE) {
            return userMapper.toDTO(userRepository.save(user));
        }
        return saveAndRevokeSessions(user);
    }

    @Override
    @Transactional
    public void deleteUser(Long id) {
        ensureNotSelf(id, "delete your own account");
        User user = findOrThrow(id);

        // Soft delete : les autres modules peuvent garder leurs références vers cet utilisateur.
        user.setAccountStatus(AccountStatus.DELETED);
        saveAndRevokeSessions(user);
    }

    // ================= Méthodes internes =================

    private User findOrThrow(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id " + id));
    }

    private void ensureNotSelf(Long targetId, String action) {
        if (SecurityUtils.requireCurrentUserId().equals(targetId)) {
            throw new BadRequestException("You cannot " + action);
        }
    }

    private String requireNotBlank(String value, String fieldName) {
        String trimmed = value.trim();
        if (trimmed.isEmpty()) {
            throw new BadRequestException(fieldName + " must not be blank");
        }
        return trimmed;
    }

    /**
     * Invalide immédiatement les sessions de l'utilisateur :
     * - tokenVersion + 1 : ses Access Tokens en circulation sont rejetés par le filtre JWT ;
     * - révocation de tous ses Refresh Tokens.
     * saveAndFlush AVANT la requête de révocation, car celle-ci vide le contexte de persistance.
     */
    private UserDTO saveAndRevokeSessions(User user) {
        user.incrementTokenVersion();
        User saved = userRepository.saveAndFlush(user);
        refreshTokenRepository.revokeAllByUserId(saved.getId());
        return userMapper.toDTO(saved);
    }
}