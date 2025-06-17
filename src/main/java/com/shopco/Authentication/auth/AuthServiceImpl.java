package com.shopco.Authentication.auth;

import com.shopco.Authentication.refreshtoken.TokenService;
import com.shopco.core.response.ApiResponse;
import com.shopco.core.security.JwtUtil;
import com.shopco.role.RoleRepository;
import com.shopco.user.User;
import com.shopco.user.UserRepository;
import com.shopco.user.UserResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.Optional;

@Slf4j
@Service
public  class AuthServiceImpl implements AuthService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final RoleRepository roleRepository;
    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;
    private final TokenService tokenService;
    private final UserResponse userResponse;


    public AuthServiceImpl(UserRepository userRepository, PasswordEncoder passwordEncoder, RoleRepository roleRepository, AuthenticationManager authenticationManager, JwtUtil jwtUtil, TokenService tokenService, UserResponse userResponse) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.roleRepository = roleRepository;
        this.authenticationManager = authenticationManager;
        this.jwtUtil = jwtUtil;
        this.tokenService = tokenService;
        this.userResponse = userResponse;
    }

    //Implement Registration logic here



    //Authentication Logic
    @Override
    public AuthResponse authenticate(AuthRequest request) {

        Optional<User> userEmail = userRepository.findByEmail((request.getEmail().toLowerCase()));
        if(userEmail.isEmpty()) {
            throw new BadCredentialsException("Invalid email or password");
        }

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
        );

        User  user = (User) authentication.getPrincipal();

        String accessToken = jwtUtil.generateToken(user);
        String refreshToken = jwtUtil.generateRefreshToken(user);

        tokenService.revokeAllUserTokens(user);

        tokenService.saveUserToken(user, refreshToken);


        return AuthResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .userResponse(userResponse.convertUserToUserResponse(user))
                .build();
    }

}
