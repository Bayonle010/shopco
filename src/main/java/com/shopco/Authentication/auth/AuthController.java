package com.shopco.Authentication.auth;

import com.shopco.core.response.ApiResponse;
import com.shopco.core.response.ResponseUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
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
    @PostMapping("/register")
    public ResponseEntity<ApiResponse> registerUser(@Valid @RequestBody AuthRegister request){

        AuthResponse response = authService.register(request);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ResponseUtil.success(
                        HttpStatus.CREATED.value(), "User registered successfully",
                        null,response,  null));
    }




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
    public ResponseEntity<ApiResponse> refreshToken(Authentication auth, HttpServletRequest request){
        AuthResponse response = authService.refreshToken(auth, request);

        return ResponseEntity.ok(
                ResponseUtil.success(HttpStatus.OK.value(), "User token Refresh successfully", null, response, null)
        );
    }


}
