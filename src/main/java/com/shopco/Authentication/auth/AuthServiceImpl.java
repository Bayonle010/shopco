package com.shopco.Authentication.auth;

import com.shopco.Authentication.token.Token;
import com.shopco.Authentication.token.TokenService;
import com.shopco.core.exception.InvalidCredentialException;
import com.shopco.core.security.JwtUtil;
import com.shopco.role.Role;
import com.shopco.role.RoleRepository;
import com.shopco.user.User;
import com.shopco.user.UserRepository;
import com.shopco.user.UserResponse;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
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
    private final JwtUtil jwtUtil;
    private final TokenService tokenService;
    private final JwtDecoder  jwtDecoder;


    public AuthServiceImpl(UserRepository userRepository, PasswordEncoder passwordEncoder, RoleRepository roleRepository, AuthenticationManager authenticationManager, JwtUtil jwtUtil, TokenService tokenService,  JwtDecoder jwtDecoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.roleRepository = roleRepository;
        this.authenticationManager = authenticationManager;
        this.jwtUtil = jwtUtil;
        this.tokenService = tokenService;
        this.jwtDecoder = jwtDecoder;
    }

    //Implement Registration logic here





    //Authentication Logic
    @Override
    public AuthResponse authenticate(AuthRequest request) {

        Optional<User> userEmail = userRepository.findByEmail((request.getEmail().toLowerCase()));
        if(userEmail.isEmpty()) {
            log.info("User not found with email: {}", request.getEmail().toLowerCase());
            throw new UsernameNotFoundException("Invalid email or password");
        }

        try {

            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
            );

            User  user = (User) authentication.getPrincipal();

            String accessToken = jwtUtil.generateToken(authentication, user);
            String refreshToken = jwtUtil.generateRefreshToken(authentication, user);

            tokenService.revokeAllUserTokens(user);

            tokenService.saveUserToken(user, refreshToken);


            return AuthResponse.builder()
                    .accessToken(accessToken)
                    .refreshToken(refreshToken)
                    .userResponse(UserResponse.convertUserToUserResponse(user))
                    .build();


        }catch (BadCredentialsException e) {
            log.info("Bad credentials");
            throw new InvalidCredentialException("Invalid email or password");
        }



    }

    @Override
    public AuthResponse logout(HttpServletRequest request){
        final String authHeader = request.getHeader("Authorization");
        if(authHeader == null || !authHeader.startsWith("Bearer ")) {
            return null;
        }

        final String jwtToken = authHeader.substring(7);

        tokenService.findAndRevokeToken(jwtToken);



        return  AuthResponse.builder()
                .userResponse(null)
                .accessToken(null)
                .refreshToken(null)
                .build();
    }


    @Override
    public AuthResponse refreshToken(Authentication auth, HttpServletRequest request){
        final String authHeader = request.getHeader("Authorization");

        if(authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new BadCredentialsException("Invalid token or password");
        }

        String refreshToken = authHeader.substring(7);
        Jwt decodedJwt = jwtDecoder.decode(refreshToken);
        String email =  decodedJwt.getSubject();
        User user = userRepository.findByEmail(email).orElseThrow(
                ()-> new UsernameNotFoundException("user not found")
        );

        //Validate Refresh Token

        Token storedToken = tokenService.getValidRefreshToken(refreshToken).orElseThrow(
                ()-> new BadCredentialsException("Invalid or Expired refresh token")
        );

        tokenService.revokeToken(storedToken);

        String newAccessToken = jwtUtil.generateToken(auth, user);
        String newRefreshToken = jwtUtil.generateRefreshToken(auth, user);

        tokenService.saveUserToken(user, newRefreshToken);


        return AuthResponse.builder()
                .accessToken(newAccessToken)
                .refreshToken(newRefreshToken)
                .userResponse(UserResponse.convertUserToUserResponse(user))
                .build();

    }

}
