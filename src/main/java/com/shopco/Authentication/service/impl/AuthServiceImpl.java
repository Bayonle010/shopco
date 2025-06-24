package com.shopco.Authentication.service.impl;

import com.shopco.Authentication.dto.AuthResponse;
import com.shopco.Authentication.dto.RefreshTokenRequest;
import com.shopco.Authentication.dto.SignInRequest;
import com.shopco.Authentication.dto.SignUpRequest;
import com.shopco.Authentication.service.AuthService;
import com.shopco.Authentication.token.service.Impl.TokenServiceImpl;
import com.shopco.core.exception.ResourceNotFoundException;
import com.shopco.core.security.JwtUtil;
import com.shopco.role.Role;
import com.shopco.role.RoleRepository;
import com.shopco.user.User;
import com.shopco.user.UserRepository;
import com.shopco.user.UserResponse;
import jakarta.persistence.RollbackException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Optional;

@Slf4j
@Service
public  class AuthServiceImpl implements AuthService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final RoleRepository roleRepository;
    private final AuthenticationManager authenticationManager;
    private final TokenServiceImpl tokenService;



    @Autowired
    private JwtUtil jwtUtil;

    public AuthServiceImpl(UserRepository userRepository, PasswordEncoder passwordEncoder, RoleRepository roleRepository, AuthenticationManager authenticationManager, JwtUtil jwtUtil, TokenServiceImpl tokenService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.roleRepository = roleRepository;
        this.authenticationManager = authenticationManager;
        this.tokenService = tokenService;
    }




    @Override
    public AuthResponse authenticateUser(SignInRequest signInRequest){

        Optional<User> user = userRepository.findByEmail(signInRequest.getEmail().toLowerCase());

        if(user.isEmpty()){
            log.info("User not found with email: {}", signInRequest.getEmail().toLowerCase());
            throw new UsernameNotFoundException("invalid email or password");
        }



        log.info("attempting to authenticate with email {}" , signInRequest.getEmail());
        Authentication authentication = authenticationManager.authenticate(

                new UsernamePasswordAuthenticationToken(signInRequest.getEmail().toLowerCase(), signInRequest.getPassword())
        );
        log.info("User authenticated successfully. Generating JWT token...");

        // return jwt token.
        String accessToken = jwtUtil.generateAccessToken(authentication, signInRequest.getEmail().toLowerCase());

        String refreshToken = jwtUtil.generateRefreshToken(authentication, signInRequest.getEmail());

        tokenService.saveRefreshToken(user.get(), refreshToken);

        UserResponse userInfo = UserResponse.convertUserToUserResponse(user.get());


        return AuthResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .userDto(userInfo)
                .build();

    }


    @Override
    public String refreshToken(RefreshTokenRequest request) {

        String refreshToken = request.getRefreshToken();


        Jwt decodedJwt = jwtUtil.decodeJwt(refreshToken); // validates structure, signature, expiry

        String email = decodedJwt.getSubject();

        log.info("email decoded from jwt {}", email);

        Optional<User> userOptional = userRepository.findByEmail(email);


        if (userOptional.isEmpty()){
            throw new ResourceNotFoundException("user not found");
        }


        User user = userOptional.get();

        boolean isValidToken = tokenService.isRefreshTokenValid(refreshToken, user);



        // Generate new access token
        Authentication authentication = new UsernamePasswordAuthenticationToken(
                user.getEmail(), null, user.getAuthorities());


        return jwtUtil.generateAccessToken(authentication, user.getEmail());
    }



    @Override
    public void logout(HttpServletRequest request) {
        final String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new ResourceNotFoundException("token not found");
        }

        String token = authHeader.substring(7); // Remove "Bearer "
        Jwt decodedJwt = jwtUtil.decodeJwt(token);


        String email = decodedJwt.getSubject(); // user email

        log.info("decode user email {}", email);

        User user = userRepository.findByEmail(email).orElseThrow(()-> new UsernameNotFoundException("user not found"));

        tokenService.revokeAllUserTokens(user);

    }

}
