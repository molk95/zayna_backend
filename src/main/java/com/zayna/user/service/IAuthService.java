package com.zayna.user.service;

import com.zayna.user.dto.AuthResponse;
import com.zayna.user.dto.LoginRequest;
import com.zayna.user.dto.RegisterRequest;

public interface IAuthService {

    AuthResponse register(RegisterRequest request);

    AuthResponse login(LoginRequest request);

    AuthResponse refresh(String refreshToken);

    void logout(String refreshToken);
}