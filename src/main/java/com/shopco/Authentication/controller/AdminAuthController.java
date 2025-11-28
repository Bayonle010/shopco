package com.shopco.Authentication.controller;

import com.shopco.Authentication.dto.AdminRegistrationRequest;
import com.shopco.Authentication.service.AuthService;
import com.shopco.core.response.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/admin/auth")
@Tag(name = "Auth (Admin) ")
public class AdminAuthController {

    private final AuthService authService;

    public AdminAuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("")
    public ResponseEntity<ApiResponse> addAdmin(@Valid @RequestBody AdminRegistrationRequest request){
        return authService.handleAddAdmin(request);
    }
}
