package com.shopco.Authentication.auth;

import com.shopco.core.response.ApiResponse;
import com.shopco.core.response.ResponseUtil;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
@Slf4j
public class AuthController {
    private final AuthServiceImpl authService;

    public AuthController(AuthServiceImpl authService) {
        this.authService = authService;
    }


    //  TODO: write register logic here



    @PostMapping("/login")
    public ResponseEntity<ApiResponse> login(@Valid AuthRequest request){

        AuthResponse response = authService.authenticate(request);

        return ResponseEntity.ok(
                ResponseUtil.success(HttpStatus.OK.value(), "User Login successfully", null, response, null)
        );
    }


}
