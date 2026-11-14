package com.zayna.user.controller;

import com.zayna.user.dto.UserDTO;
import com.zayna.user.service.IProfileImageService;
import org.springframework.core.io.Resource;
import org.springframework.http.CacheControl;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.time.Duration;

@RestController
@RequestMapping("/api/users")
public class UserProfileController {

    private final IProfileImageService profileImageService;

    public UserProfileController(IProfileImageService profileImageService) {
        this.profileImageService = profileImageService;
    }

    @PostMapping(value = "/me/profile-image", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<UserDTO> uploadMyProfileImage(@RequestParam("file") MultipartFile file) {
        return ResponseEntity.ok(profileImageService.uploadMyProfileImage(file));
    }

    @DeleteMapping("/me/profile-image")
    public ResponseEntity<UserDTO> deleteMyProfileImage() {
        return ResponseEntity.ok(profileImageService.deleteMyProfileImage());
    }

    /** Public : l'URL contient ?v=<nom du fichier>, donc un cache long est sans risque. */
    @GetMapping("/{id}/profile-image")
    public ResponseEntity<Resource> getProfileImage(@PathVariable Long id) {
        IProfileImageService.ProfileImage image = profileImageService.load(id);
        return ResponseEntity.ok()
                .contentType(image.mediaType())
                .cacheControl(CacheControl.maxAge(Duration.ofDays(30)).cachePublic())
                .body(image.resource());
    }
}