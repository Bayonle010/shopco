package com.shopco.user.service;

import com.shopco.core.exception.AccessDeniedException;
import com.shopco.core.exception.ResourceNotFoundException;
import com.shopco.user.entity.User;
import com.shopco.user.enums.UserType;
import com.shopco.user.repositories.UserRepository;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public void verifyAdmin(Authentication authentication) {
        User user = userRepository.findByEmail(authentication.getName())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        if (!user.getUserType().equals(UserType.ADMIN)) {
            throw new AccessDeniedException("Access denied: User is not an admin");
        }

    }

    @Override
    public boolean isAdmin(Authentication authentication) {
        try {
            verifyAdmin(authentication); // reuse logic
            return true;
        } catch (AccessDeniedException ex) {
            return false;
        }
    }
}
