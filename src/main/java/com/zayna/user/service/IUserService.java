package com.zayna.user.service;

import com.zayna.user.dto.AdminUpdateUserRequest;
import com.zayna.user.dto.PageResponse;
import com.zayna.user.dto.PublicProfileDTO;
import com.zayna.user.dto.UpdateProfileRequest;
import com.zayna.user.dto.UserDTO;
import com.zayna.user.enums.AccountStatus;
import com.zayna.user.enums.Role;

public interface IUserService {

    // --- Utilisateur connecté ---
    UserDTO getMyProfile();

    UserDTO updateMyProfile(UpdateProfileRequest request);

    // --- Profils des autres membres ---
    PublicProfileDTO getPublicProfile(Long id);

    // --- Administration (ADMIN) ---
    PageResponse<UserDTO> searchUsers(String search, Role role, AccountStatus status,
                                      int page, int size, String sortBy, String direction);

    UserDTO getUserById(Long id);

    UserDTO updateUser(Long id, AdminUpdateUserRequest request);

    UserDTO changeRole(Long id, Role role);

    UserDTO changeStatus(Long id, AccountStatus status);

    void deleteUser(Long id);
}