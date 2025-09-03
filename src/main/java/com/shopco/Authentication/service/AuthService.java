package com.shopco.Authentication.service;

import com.shopco.Authentication.dto.AuthResponse;
import com.shopco.Authentication.dto.RefreshTokenRequest;
import com.shopco.Authentication.dto.SignInRequest;
import com.shopco.Authentication.dto.SignUpRequest;
import com.shopco.core.response.ApiResponse;
import jakarta.mail.MessagingException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;

public interface AuthService {

    ResponseEntity<ApiResponse> registerUser(SignUpRequest request) throws MessagingException;

    ResponseEntity<ApiResponse> authenticateUser(SignInRequest signInRequest);

    ResponseEntity<ApiResponse> refreshToken(RefreshTokenRequest refreshTokenRequest);

    ResponseEntity<ApiResponse> logout(HttpServletRequest httpServletRequest);
}
