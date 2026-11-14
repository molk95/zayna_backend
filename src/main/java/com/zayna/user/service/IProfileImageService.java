package com.zayna.user.service;

import com.zayna.user.dto.UserDTO;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.web.multipart.MultipartFile;

public interface IProfileImageService {

    /** Remplace la photo de l'utilisateur connecté. Retourne son profil mis à jour. */
    UserDTO uploadMyProfileImage(MultipartFile file);

    /** Supprime la photo de l'utilisateur connecté. Retourne son profil mis à jour. */
    UserDTO deleteMyProfileImage();

    /** Charge la photo d'un utilisateur (lecture publique). */
    ProfileImage load(Long userId);

    record ProfileImage(Resource resource, MediaType mediaType) {
    }
}