package com.shopco.Authentication.dto;

import jakarta.validation.constraints.*;
import lombok.*;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class SignUpRequest {
    @NotBlank(message = "firstname field cannot be blank")
    private String firstname;

    @NotBlank(message = "Lastname field cannot be blank")
    private String lastname;

    @NotBlank(message = "email field cannot be blank")
    @Email(message = "invalid email format")
    private String email;

    @NotBlank(message = "The username field cannot be blank")
    private String username;

    @Size(min = 8, message = "Password must be at least 8 characters long")
    @Pattern(
            regexp = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=!])(?=\\S+$).{8,}$",
            message = "Password must contain at least one uppercase letter, one lowercase letter, one number, and one special character"
    )

    @NotBlank(message = "password field cannot be blank")
    private String password;


}
