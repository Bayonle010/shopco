package com.shopco.user.model;

import com.shopco.role.Role;
import com.shopco.user.entity.User;
import lombok.Builder;
import lombok.Data;

import java.util.List;
import java.util.UUID;

@Data
@Builder
public class UserDto {
    private UUID id;
    private String username;
    private String email;
    private String firstName;
    private String lastName;
    private String role;
    private String userType;


    public static UserDto convertUserEntityToUserDto(User user) {

        List<String> roles = user.getRoles()
                .stream()
                .map(Role::getAuthority)
                .toList();
        
        return UserDto.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .firstName(user.getFirstname())
                .lastName(user.getLastname())
                .role(String.valueOf(roles))
                .build();

    }
}
