package com.shopco.Authentication.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class RefreshTokenRequest {
    @NotBlank(message = "refreshtoken field cannot be blank")
    @NotNull(message = "refreshtoken fields cannot be null")
    private String refreshToken;
}
