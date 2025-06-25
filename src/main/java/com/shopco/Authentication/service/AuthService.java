package com.shopco.Authentication.service;

import com.shopco.Authentication.dto.AuthResponse;
import com.shopco.Authentication.dto.RefreshTokenRequest;
import com.shopco.Authentication.dto.SignInRequest;
import com.shopco.Authentication.dto.SignUpRequest;
import jakarta.servlet.http.HttpServletRequest;

public interface AuthService {

    AuthResponse registerUser(SignUpRequest request);

    AuthResponse authenticateUser(SignInRequest signInRequest);

    AuthResponse refreshToken(RefreshTokenRequest refreshTokenRequest);

    void logout(HttpServletRequest httpServletRequest);
}
