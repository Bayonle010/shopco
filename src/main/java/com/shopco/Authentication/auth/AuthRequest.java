package com.shopco.Authentication.auth;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class AuthRequest {

    @NotNull(message = "email field cannot be null")
    @NotBlank(message = "email field is required")
    private String email;

    @NotBlank(message = "password field is required")
    @NotNull(message = "password field cannot be null")
    private String password;
}
