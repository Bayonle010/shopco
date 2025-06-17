package com.shopco.Authentication.auth;

import com.shopco.core.response.ApiResponse;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;

public interface AuthService {
    AuthResponse authenticate(AuthRequest authRequest);
    AuthResponse logout(HttpServletRequest request);
}
