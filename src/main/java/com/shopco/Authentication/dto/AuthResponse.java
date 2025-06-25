package com.shopco.Authentication.dto;

import com.shopco.user.UserDto;
import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class AuthResponse {
    private String accessToken;
    private String refreshToken;
    private UserDto userDto;
}
