package com.shopco.user.service.impl;


import com.shopco.role.Role;
import com.shopco.role.RoleRepository;
import com.shopco.user.component.SuperAdminProperties;
import com.shopco.user.entity.User;
import com.shopco.user.enums.UserType;
import com.shopco.user.repositories.UserRepository;
import com.shopco.user.service.SuperAdminBootStrapService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.HashSet;
import java.util.Optional;

@Service
public class SuperAdminBootStrapServiceImpl implements SuperAdminBootStrapService {
    private final static Logger log = LoggerFactory.getLogger(SuperAdminBootStrapServiceImpl.class);

    private final SuperAdminProperties superAdminProperties;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    public SuperAdminBootStrapServiceImpl(SuperAdminProperties superAdminProperties, UserRepository userRepository, RoleRepository roleRepository, PasswordEncoder passwordEncoder) {
        this.superAdminProperties = superAdminProperties;
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void createSuperAdminAccount() {
        String email = normalize(superAdminProperties.getEmail());

        if (userRepository.existsByEmail(email)){
            return;
        }

        String rawPassword = superAdminProperties.getPassword();
        if (rawPassword == null || rawPassword.isBlank()) {
            throw new IllegalStateException("APP_SUPERADMIN_PASSWORD is missing.");
        }

        String encoded = passwordEncoder.encode(rawPassword);

        if (roleRepository.findByAuthority("ROLE_SUPER_ADMIN").isEmpty()) {
            roleRepository.save(new Role("ROLE_SUPER_ADMIN"));
        }

        Role role = roleRepository.findByAuthority("ROLE_SUPER_ADMIN").get();

        User admin = User.builder()
                .firstname("Super")
                .lastname("Admin")
                .email(email)
                .username("shopco-superadmin")
                .password(encoded)
                .userType(UserType.ADMIN)
                .isEnabled(true)
                .isVerified(true)
                .roles(new HashSet<>(Collections.singleton(role)))
                .build();

        User saved = userRepository.save(admin);
        log.info("Super admin created with id {}", saved.getId());
    }

    private void ensureSuperAdminRole(User admin) {

        if (admin.getRoles() == null){
            admin.setRoles(new HashSet<>());
        }

        Optional<Role> superadminRole = roleRepository.findByAuthority("ROLE_SUPER_ADMIN");
        superadminRole.ifPresent(role -> admin.getRoles().add(role));
        userRepository.save(admin);
        log.info("ROLE_SUPER_ADMIN assigned to {}", admin.getEmail());

    }


    private String normalize(String email) {
        return email == null ? null : email.trim().toLowerCase();
    }

}
