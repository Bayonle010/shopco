package com.shopco.Authentication.auth;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.core.Authentication;

public interface AuthService {
    //Implement Registration logic here
    AuthResponse register(AuthRegister request);
    AuthResponse authenticate(AuthRequest authRequest);
    AuthResponse logout(HttpServletRequest request);
    AuthResponse refreshToken(Authentication auth, HttpServletRequest request);
}
