package com.zayna.user.service;

import com.zayna.exception.InvalidFileException;
import com.zayna.exception.ResourceNotFoundException;
import com.zayna.security.SecurityUtils;
import com.zayna.user.dto.UserDTO;
import com.zayna.user.entity.User;
import com.zayna.user.enums.AccountStatus;
import com.zayna.user.mapper.UserMapper;
import com.zayna.user.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Locale;
import java.util.Set;
import java.util.UUID;
import java.util.regex.Pattern;

@Service
public class ProfileImageServiceImpl implements IProfileImageService {

    private static final Logger log = LoggerFactory.getLogger(ProfileImageServiceImpl.class);

    private static final Pattern STORED_NAME =
            Pattern.compile("^profile_\\d+_[0-9a-f\\-]{36}\\.(jpg|png|webp)$");
    private static final Set<String> ALLOWED_EXTENSIONS = Set.of("jpg", "jpeg", "png", "webp");
    private static final Set<String> ALLOWED_MIME_TYPES = Set.of("image/jpeg", "image/png", "image/webp");

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final Path storageDir;
    private final long maxSizeBytes;
    private final long maxSizeMb;

    public ProfileImageServiceImpl(UserRepository userRepository,
                                   UserMapper userMapper,
                                   @Value("${app.storage.profile-images-dir:uploads/users}") String directory,
                                   @Value("${app.storage.profile-image-max-size-mb:5}") long maxSizeMb) {
        this.userRepository = userRepository;
        this.userMapper = userMapper;
        this.storageDir = Paths.get(directory).toAbsolutePath().normalize();
        this.maxSizeMb = maxSizeMb;
        this.maxSizeBytes = maxSizeMb * 1024 * 1024;
        try {
            Files.createDirectories(storageDir);
        } catch (IOException ex) {
            log.warn("Could not create profile image directory {} at startup: {}", storageDir, ex.getMessage());
        }
    }

    @Override
    @Transactional
    public UserDTO uploadMyProfileImage(MultipartFile file) {
        User user = findCurrentUser();
        String newName = storeValidated(user.getId(), file);
        String oldName = user.getProfileImage();

        user.setProfileImage(newName);
        User saved;
        try {
            saved = userRepository.saveAndFlush(user);
        } catch (RuntimeException ex) {
            deleteQuietly(newName);
            throw ex;
        }

        deleteAfterCommit(oldName);
        return userMapper.toDTO(saved);
    }

    @Override
    @Transactional
    public UserDTO deleteMyProfileImage() {
        User user = findCurrentUser();
        String oldName = user.getProfileImage();

        if (oldName != null) {
            user.setProfileImage(null);
            user = userRepository.save(user);
            deleteAfterCommit(oldName);
        }
        return userMapper.toDTO(user);
    }

    @Override
    @Transactional(readOnly = true)
    public ProfileImage load(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Profile image not found"));

        String name = user.getProfileImage();
        if (user.getAccountStatus() != AccountStatus.ACTIVE
                || name == null
                || !STORED_NAME.matcher(name).matches()) {
            throw new ResourceNotFoundException("Profile image not found");
        }

        Path file = storageDir.resolve(name).normalize();
        if (!file.startsWith(storageDir) || !Files.isReadable(file)) {
            throw new ResourceNotFoundException("Profile image not found");
        }
        return new ProfileImage(new FileSystemResource(file), mediaTypeFor(name));
    }

    // ------------------------------------------------------------------

    private User findCurrentUser() {
        return userRepository.findById(SecurityUtils.requireCurrentUserId())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
    }

    private String storeValidated(Long userId, MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new InvalidFileException("Image file is required");
        }
        if (file.getSize() > maxSizeBytes) {
            throw new InvalidFileException("Image is too large (maximum " + maxSizeMb + " MB)");
        }

        String originalName = StringUtils.cleanPath(
                file.getOriginalFilename() == null ? "" : file.getOriginalFilename());
        String extension = extensionOf(originalName);
        if (!ALLOWED_EXTENSIONS.contains(extension)) {
            throw new InvalidFileException("Unsupported file extension: only JPG, PNG and WebP images are allowed");
        }

        String mime = file.getContentType() == null ? "" : file.getContentType().toLowerCase(Locale.ROOT);
        if (!ALLOWED_MIME_TYPES.contains(mime)) {
            throw new InvalidFileException("Unsupported content type: only image/jpeg, image/png and image/webp are allowed");
        }

        byte[] bytes;
        try {
            bytes = file.getBytes();
        } catch (IOException ex) {
            throw new InvalidFileException("Could not read the uploaded file");
        }

        String detected = detectImageType(bytes);
        if (detected == null) {
            throw new InvalidFileException("File content is not a valid JPEG, PNG or WebP image");
        }
        String normalizedExtension = extension.equals("jpeg") ? "jpg" : extension;
        if (!detected.equals(normalizedExtension)) {
            throw new InvalidFileException("File extension does not match the image content");
        }

        // Nom généré par le serveur : jamais de nom fourni par le client.
        String storedName = "profile_" + userId + "_" + UUID.randomUUID() + "." + detected;
        Path target = storageDir.resolve(storedName).normalize();
        if (!target.startsWith(storageDir)) {
            throw new InvalidFileException("Invalid file name");
        }

        try {
            Files.createDirectories(storageDir);
            Files.write(target, bytes, StandardOpenOption.CREATE_NEW);
        } catch (IOException ex) {
            log.error("Could not store profile image {}", storedName, ex);
            throw new IllegalStateException("Could not store the image");
        }
        return storedName;
    }

    private String extensionOf(String filename) {
        int dot = filename.lastIndexOf('.');
        return (dot < 0 || dot == filename.length() - 1)
                ? ""
                : filename.substring(dot + 1).toLowerCase(Locale.ROOT);
    }

    /** Détecte le vrai format par la signature binaire. Retourne "jpg", "png", "webp" ou null. */
    private String detectImageType(byte[] b) {
        if (b.length >= 3
                && (b[0] & 0xFF) == 0xFF && (b[1] & 0xFF) == 0xD8 && (b[2] & 0xFF) == 0xFF) {
            return "jpg";
        }
        if (b.length >= 8
                && (b[0] & 0xFF) == 0x89 && b[1] == 'P' && b[2] == 'N' && b[3] == 'G'
                && b[4] == 0x0D && b[5] == 0x0A && b[6] == 0x1A && b[7] == 0x0A) {
            return "png";
        }
        if (b.length >= 12
                && b[0] == 'R' && b[1] == 'I' && b[2] == 'F' && b[3] == 'F'
                && b[8] == 'W' && b[9] == 'E' && b[10] == 'B' && b[11] == 'P') {
            return "webp";
        }
        return null;
    }

    private MediaType mediaTypeFor(String storedName) {
        if (storedName.endsWith(".png")) {
            return MediaType.IMAGE_PNG;
        }
        if (storedName.endsWith(".webp")) {
            return MediaType.parseMediaType("image/webp");
        }
        return MediaType.IMAGE_JPEG;
    }

    private void deleteAfterCommit(String storedName) {
        if (storedName == null) {
            return;
        }
        if (TransactionSynchronizationManager.isSynchronizationActive()) {
            TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
                @Override
                public void afterCommit() {
                    deleteQuietly(storedName);
                }
            });
        } else {
            deleteQuietly(storedName);
        }
    }

    private void deleteQuietly(String storedName) {
        if (storedName == null || !STORED_NAME.matcher(storedName).matches()) {
            return;
        }
        try {
            Path file = storageDir.resolve(storedName).normalize();
            if (file.startsWith(storageDir)) {
                Files.deleteIfExists(file);
            }
        } catch (IOException ex) {
            log.warn("Could not delete profile image {}: {}", storedName, ex.getMessage());
        }
    }
}