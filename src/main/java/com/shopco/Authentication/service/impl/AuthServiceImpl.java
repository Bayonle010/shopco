package com.shopco.Authentication.service.impl;

import com.shopco.Authentication.dto.AuthResponse;
import com.shopco.Authentication.dto.RefreshTokenRequest;
import com.shopco.Authentication.dto.SignInRequest;
import com.shopco.Authentication.service.AuthService;
import com.shopco.Authentication.token.service.Impl.TokenServiceImpl;
import com.shopco.core.exception.InvalidCredentialException;
import com.shopco.core.security.JwtUtil;
import com.shopco.role.RoleRepository;
import com.shopco.user.User;
import com.shopco.user.UserRepository;
import com.shopco.user.UserResponse;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;

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
        String formatedEmailFromRequest = signInRequest.getEmail().toLowerCase();

        Optional<User> user = userRepository.findByEmail(formatedEmailFromRequest);

        if(user.isEmpty()){
            log.info("User not found with email: {}", formatedEmailFromRequest);
            throw new UsernameNotFoundException("invalid email or password");
        }

        log.info("attempting to authenticate with email {}" , signInRequest.getEmail());

        try {

            Authentication authentication = authenticationManager.authenticate(

                    new UsernamePasswordAuthenticationToken(formatedEmailFromRequest, signInRequest.getPassword())
            );
            log.info("User authenticated successfully. Generating JWT token...");

            // return jwt token.
            String accessToken = jwtUtil.generateAccessToken(authentication, formatedEmailFromRequest);

            String refreshToken = jwtUtil.generateRefreshToken(authentication, formatedEmailFromRequest);

            tokenService.saveRefreshToken(user.get(), refreshToken);

            UserResponse userInfo = UserResponse.convertUserToUserResponse(user.get());


            return AuthResponse.builder()
                    .accessToken(accessToken)
                    .refreshToken(refreshToken)
                    .userDto(userInfo)
                    .build();

        }catch (AuthenticationException e){
            log.error("Invalid email or Password {}", e.getMessage());
            throw new InvalidCredentialException("invalid email or password");
        }

    }


    @Override
    public AuthResponse refreshToken(RefreshTokenRequest request) {

        String refreshToken = request.getRefreshToken();


        Jwt decodedJwt = jwtUtil.decodeJwt(refreshToken); // validates structure, signature, expiry

        String email = decodedJwt.getSubject();

        log.info("email decoded from jwt {}", email);

        Optional<User> userOptional = userRepository.findByEmail(email);


        if (userOptional.isEmpty()){
            throw new InvalidCredentialException("user with the token not found");
        }


        User user = userOptional.get();

        log.info("user with email {} + owns the jwt", userOptional.get());

        boolean isValidToken = tokenService.isRefreshTokenValid(refreshToken, user);



        // Generate new access token
        Authentication authentication = new UsernamePasswordAuthenticationToken(
                user.getEmail(), null, user.getAuthorities());


        String accessToken = jwtUtil.generateAccessToken(authentication, user.getEmail());

        return AuthResponse.builder()
                .accessToken(accessToken)
                .build();
    }



    @Override
    public void logout(HttpServletRequest request) {
        final String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new InvalidCredentialException("token not found");
        }

        String token = authHeader.substring(7); // Remove "Bearer "

        Jwt decodedJwt = jwtUtil.decodeJwt(token);

        String email = decodedJwt.getSubject(); // user email

        log.info("decode user email {}", email);

        User user = userRepository.findByEmail(email).orElseThrow(()-> new InvalidCredentialException("invalid token : user with the passed token not found"));

        tokenService.revokeAllUserTokens(user);

    }

}
