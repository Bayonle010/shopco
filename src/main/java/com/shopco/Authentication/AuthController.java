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



    @Operation(
            summary = "Authenticate a user",
            description = """
        Authenticates a user with email and password.
        On successful authentication, two tokens are returned:

        - **Access Token**: Valid for 15 minutes. Used for accessing secured endpoints.
        - **Refresh Token**: Valid for 24 hours. Can be used to obtain a new access token without requiring the user to log in again.

        **Note**: The access token should be sent in the `Authorization` header as `Bearer <token>` when accessing protected resources.
    """
    )
    @PostMapping("/login")
    public ResponseEntity<ApiResponse> authenticateUser(@RequestBody @Valid SignInRequest signInRequest){
        AuthResponse response = authService.authenticateUser(signInRequest);
        return new ResponseEntity<>(ResponseUtil.success(
                HttpStatus.OK.value(), "user authenticated successfully", response, null
        ), HttpStatus.OK);
    }

    @Operation(
            summary = "Generate a new access token",
            description = """
        Uses a valid refresh token to generate a new short-lived access token.
       \s
        - **New Access Token**: Expires in 15 minutes.
        - This endpoint should be called whenever the current access token expires to maintain a seamless user experience.
       \s
        **Headers**:
        - The refresh token must be passed in the `Authorization` header as `Bearer <refresh_token>`.
       \s
        **Note**: If the refresh token has expired or has been revoked (e.g., after logout), a new access token will not be issued.
   \s"""
    )
    @PostMapping("/refresh")
    public ResponseEntity<ApiResponse> refreshToken(@RequestBody @Valid RefreshTokenRequest request){
        AuthResponse refreshToken = authService.refreshToken(request);

        return new ResponseEntity<>(ResponseUtil.success(
                HttpStatus.OK.value(), "new access token generated successfully", refreshToken, null
        ), HttpStatus.OK);
    }


    @Operation(
            summary = "Sign out a user from the application",
            description = """
        Logs the user out by revoking their refresh token.
       \s
        - This endpoint expects a valid **Access Token** in the `Authorization` header (as `Bearer <token>`).
        - Alternatively, a **Refresh Token** can also be used — in this case, the refresh token will be marked as expired and revoked, and cannot be reused to generate new access tokens.
       \s
        **Behavior**:
        - All active refresh tokens for the user will be revoked, ending all sessions.
        - Once logged out, the user must log in again to receive new tokens.
       \s
        **Security Note**: Always ensure tokens are securely stored and transmitted over HTTPS.
   \s"""
    )
    @PostMapping("/logout")
    public ResponseEntity<ApiResponse> logout(HttpServletRequest request){

        authService.logout(request);

        return new ResponseEntity<>(ResponseUtil.success(
                HttpStatus.OK.value(), "user logged out successfully", null, null
        ), HttpStatus.OK);
    }

}
