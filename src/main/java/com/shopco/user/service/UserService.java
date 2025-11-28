package com.shopco.user.service;

import org.springframework.security.core.Authentication;

public interface UserService {
    void verifyAdmin(Authentication authentication);
    boolean isAdmin(Authentication authentication);
}
