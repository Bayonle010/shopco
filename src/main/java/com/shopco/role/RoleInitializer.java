package com.shopco.role;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class RoleInitializer implements CommandLineRunner {

    private static final Logger logger = LoggerFactory.getLogger(RoleInitializer.class);
    private final RoleRepository roleRepository;

    public RoleInitializer(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }

    @Override
    public void run(String... args) throws Exception {
        logger.info("Initializing role for users");

        // Create default roles if they don't exist
        if (roleRepository.findByAuthority("ROLE_USER").isEmpty()) {
            roleRepository.save(new Role("ROLE_USER"));
        }

        if (roleRepository.findByAuthority("ROLE_ADMIN").isEmpty()) {
            roleRepository.save(new Role("ROLE_ADMIN"));
        }

        if (roleRepository.findByAuthority("ROLE_SUPER_ADMIN").isEmpty()) {
            roleRepository.save(new Role("ROLE_SUPER_ADMIN"));
        }
    }
}