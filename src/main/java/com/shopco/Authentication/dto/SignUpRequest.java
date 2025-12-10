package com.shopco.Authentication.dto;

import jakarta.validation.constraints.*;
import lombok.*;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class SignUpRequest {
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
    @Size(min = 8, message = "Password must be at least 8 characters long")
    @Pattern(
            regexp = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=!])(?=\\S+$).{8,}$",
            message = "Password must contain at least one uppercase letter, one lowercase letter, one number, and one special character"
    )
    private String password;

}
