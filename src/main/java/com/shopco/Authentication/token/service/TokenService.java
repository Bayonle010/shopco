package com.shopco.Authentication.token.service;

import com.shopco.user.User;

public interface TokenService {
    void saveRefreshToken(User user, String refreshToken);

    void revokeAllUserTokens(User user);

    boolean isRefreshTokenValid(String rawToken, User user);

    void deleteExpiredTokens();
}
