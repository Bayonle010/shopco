package com.shopco.user.component;


import com.shopco.user.service.SuperAdminBootStrapService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class SuperAdminBootStrapRunner implements CommandLineRunner {
    private final SuperAdminBootStrapService superAdminBootstrapService;

    public SuperAdminBootStrapRunner(SuperAdminBootStrapService superAdminBootstrapService) {
        this.superAdminBootstrapService = superAdminBootstrapService;
    }


    @Override
    public void run(String... args) throws Exception {
        superAdminBootstrapService.createSuperAdminAccount();
    }
}
