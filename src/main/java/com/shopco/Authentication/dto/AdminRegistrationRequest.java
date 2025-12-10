package com.shopco.Authentication.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record AdminRegistrationRequest(
        @NotBlank(message = "firstName field cannot be blank")
        @JsonProperty("firstName")
        String firstName,

        @NotBlank(message = "lastName field cannot be blank")
        @JsonProperty("lastName")
        String lastName,

        @NotBlank(message = "email field cannot be blank")
        @Email(message = "invalid email format")
        String email,

        @NotBlank(message = "baseRole field cannot be empty")
        @JsonProperty("baseRole")
        String baseRole,

        @Size(min = 8, message = "Password must be at least 8 characters long")
        @Pattern(
             regexp = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=!])(?=\\S+$).{8,}$",
            message = "Password must contain at least one uppercase letter, one lowercase letter, one number, and one special character"
        )
        String password
) {
}
