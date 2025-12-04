package com.shopco.user.service;

import com.shopco.user.entity.User;
import org.springframework.security.core.Authentication;

public interface UserService {
    void verifyAdmin(Authentication authentication);
    boolean isAdmin(Authentication authentication);
    User getAuthenticatedUser(Authentication authentication);
}
