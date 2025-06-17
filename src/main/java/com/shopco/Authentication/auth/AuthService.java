package com.shopco.Authentication.auth;

import com.shopco.core.response.ApiResponse;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.core.Authentication;

public interface AuthService {
    AuthResponse authenticate(AuthRequest authRequest);
    AuthResponse logout(HttpServletRequest request);
    AuthResponse refreshToken(Authentication auth, HttpServletRequest request);
}
