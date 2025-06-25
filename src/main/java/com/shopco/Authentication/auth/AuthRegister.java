package com.shopco.Authentication.auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
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
    @Email(message = "please provide a valid email address")
    private String email;

    @NotBlank(message = "username field is required")
    @NotNull(message = "username field cannot be null")
    private String username;

    @NotBlank(message = "password field is required")
    @NotNull(message = "password field cannot be null")
    @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$",
            message = "Password must include uppercase, lowercase, number, and special character")
    private String password;
}
