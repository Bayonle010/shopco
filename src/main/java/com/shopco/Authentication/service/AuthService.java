package com.shopco.Authentication.service;

import com.shopco.Authentication.dto.AuthResponse;
import com.shopco.Authentication.dto.RefreshTokenRequest;
import com.shopco.Authentication.dto.SignInRequest;
import com.shopco.Authentication.dto.SignUpRequest;
import jakarta.mail.MessagingException;
import jakarta.servlet.http.HttpServletRequest;

public interface AuthService {

    AuthResponse registerUser(SignUpRequest request) throws MessagingException;

    AuthResponse authenticateUser(SignInRequest signInRequest);

    AuthResponse refreshToken(RefreshTokenRequest refreshTokenRequest);

    void logout(HttpServletRequest httpServletRequest);
}
