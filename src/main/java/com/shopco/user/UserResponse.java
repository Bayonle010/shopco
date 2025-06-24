package com.shopco.user;

import com.shopco.role.Role;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.UUID;

@Data
@Builder
public class UserResponse {
    private UUID id;
    private String email;
    private String firstName;
    private String lastName;
    private String username;
    private String role;



    public static UserResponse convertUserToUserResponse(User user) {
        List<String> roles = user.getRoles()
                .stream()
                .map(Role::getAuthority)
                .toList();
        return UserResponse.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .firstName(user.getFirstname())
                .lastName(user.getLastname())
                .role(String.valueOf(roles))
                .build();
    }
}
