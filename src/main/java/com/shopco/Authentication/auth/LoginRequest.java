package com.shopco.Authentication.auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class LoginRequest {
    @NotBlank(message = "email field cannot be blank")
    @Email(message = "invalid email format")
    private String email;

    @NotBlank(message = "password field cannot be blank")
    private String password;

}
