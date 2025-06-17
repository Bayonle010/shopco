package com.shopco.Authentication.auth;

import com.shopco.core.response.ApiResponse;

public interface AuthService {
    AuthResponse authenticate(AuthRequest authRequest);
}
