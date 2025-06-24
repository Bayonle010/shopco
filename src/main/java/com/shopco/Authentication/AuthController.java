package com.shopco.Authentication;

import com.shopco.Authentication.dto.AuthResponse;
import com.shopco.Authentication.dto.RefreshTokenRequest;
import com.shopco.Authentication.dto.SignInRequest;
import com.shopco.Authentication.dto.SignUpRequest;
import com.shopco.Authentication.service.impl.AuthServiceImpl;
import com.shopco.core.response.ApiResponse;
import com.shopco.core.response.ResponseUtil;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {
    private final AuthServiceImpl authService;

    public AuthController(AuthServiceImpl authService) {
        this.authService = authService;
    }


    //  TODO: write register logic here



    @Operation(summary = "Authenticate a user", description = "access token valid for 15 mins and refresh token valid for 24 hours")
    @PostMapping("/login")
    public ResponseEntity<ApiResponse> authenticateUser(@RequestBody @Valid SignInRequest signInRequest){
        AuthResponse response = authService.authenticateUser(signInRequest);
        return new ResponseEntity<>(ResponseUtil.success(
                HttpStatus.OK.value(), "user authenticated successfully", response, null
        ), HttpStatus.OK);
    }

    @Operation(summary = "generate a new access token", description = "the new access token expires in 15 minutes")
    @PostMapping("/refresh")
    public ResponseEntity<ApiResponse> refreshToken(@RequestBody @Valid RefreshTokenRequest request){
        String refreshToken = authService.refreshToken(request);

        return new ResponseEntity<>(ResponseUtil.success(
                HttpStatus.OK.value(), "new access token generated successfully", refreshToken, null
        ), HttpStatus.OK);
    }

    @PostMapping("/logout")
    public ResponseEntity<ApiResponse> logout(HttpServletRequest request){

        authService.logout(request);

        return new ResponseEntity<>(ResponseUtil.success(
                HttpStatus.OK.value(), "user logged out successfully", null, null
        ), HttpStatus.OK);
    }

}
