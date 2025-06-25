package com.shopco.Authentication.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class SignInRequest {

    @NotNull(message = "email field cannot be null")
    @NotBlank(message = "email field is required")
    @Email(message = "invalid email format")
    private String email;

    @NotBlank(message = "password field is required")
    @NotNull(message = "password field cannot be null")
    private String password;
}
