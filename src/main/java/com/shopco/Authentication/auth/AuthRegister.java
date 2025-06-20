package com.shopco.Authentication.auth;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class AuthRegister {

    @NotBlank(message = "firstname field is required")
    @NotNull(message = "firstname field cannot be null")
    private String firstname;

    @NotBlank(message = "lastname field is required")
    @NotNull(message = "lastname field cannot be null")
    private String lastname;

    @NotBlank(message = "email field is required")
    @NotNull(message = "email field cannot be null")
    private String email;

    @NotBlank(message = "username field is required")
    @NotNull(message = "username field cannot be null")
    private String username;

    @NotBlank(message = "password field is required")
    @NotNull(message = "password field cannot be null")
    private String password;
}
