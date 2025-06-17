package com.shopco.Authentication.auth;

import com.shopco.core.response.ApiResponse;
import com.shopco.core.response.ResponseUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {
    private final AuthServiceImpl authService;

    public AuthController(AuthServiceImpl authService) {
        this.authService = authService;
    }


    //  TODO: write register logic here



    @PostMapping("/login")
    public ResponseEntity<ApiResponse> login(@Valid @RequestBody AuthRequest request){

        AuthResponse response = authService.authenticate(request);

        return ResponseEntity.ok(
                ResponseUtil.success(HttpStatus.OK.value(), "User Login successfully", null, response, null)
        );
    }


    @PostMapping("/logout")
    public ResponseEntity<ApiResponse> logout(HttpServletRequest request){

        AuthResponse response = authService.logout(request);

        return ResponseEntity.ok(
                ResponseUtil.success(HttpStatus.OK.value(), "User Logout successfully", null, response, null)
        );
    }


    @PostMapping("/refresh-token")
    public ResponseEntity<ApiResponse> refreshToken(HttpServletRequest request){
        AuthResponse response = authService.refreshToken(request);

        return ResponseEntity.ok(
                ResponseUtil.success(HttpStatus.OK.value(), "User Refresh successfully", null, response, null)
        );
    }


}
