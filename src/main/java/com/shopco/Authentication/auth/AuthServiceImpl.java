package com.shopco.Authentication.auth;

import com.shopco.core.response.ApiResponse;
import com.shopco.core.security.JwtUtil;
import com.shopco.role.RoleRepository;
import com.shopco.user.User;
import com.shopco.user.UserRepository;
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


    public AuthServiceImpl(UserRepository userRepository, PasswordEncoder passwordEncoder, RoleRepository roleRepository, AuthenticationManager authenticationManager, JwtUtil jwtUtil) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.roleRepository = roleRepository;
        this.authenticationManager = authenticationManager;
        this.jwtUtil = jwtUtil;
    }

    //Implement Registration logic here



    //Authentication Logic
    @Override
    public ApiResponse authenticate(LoginRequest request) {

        Optional<User> userEmail = userRepository.findByEmail((request.getEmail().toLowerCase()));
        if(userEmail.isEmpty()) {
            throw new BadCredentialsException("Invalid email or password");
        }

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
        );


        String accessToken = jwtUtil.generateJwt(authentication, request.getEmail().toLowerCase());
        String refreshToken = jwtUtil.generateRefreshToken(request.getEmail().toLowerCase());

       // RefreshToken refreshTokenObj = new RefreshToken(accessToken, refreshToken, Instant.now().plus(jwtUtil.getRefreshTokenExpirationMs());


        return null;
    }

    public static void main(String[] args) {
        System.out.println(Instant.now());

        System.out.println("-------------->>>>>>");

        System.out.println(LocalDateTime.now());
    }


}
