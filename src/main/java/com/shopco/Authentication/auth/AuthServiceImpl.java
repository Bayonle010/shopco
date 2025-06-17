package com.shopco.Authentication.auth;

import com.shopco.Authentication.refreshtoken.Token;
import com.shopco.Authentication.refreshtoken.TokenService;
import com.shopco.core.security.JwtUtil;
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
                .userResponse(UserResponse.convertUserToUserResponse(user))
                .build();
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
    public AuthResponse refreshToken(HttpServletRequest request){
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

        String newAccessToken = jwtUtil.generateToken(user);
        String newRefreshToken = jwtUtil.generateRefreshToken(user);

        tokenService.saveUserToken(user, newRefreshToken);


        return AuthResponse.builder()
                .accessToken(newAccessToken)
                .refreshToken(newRefreshToken)
                .userResponse(UserResponse.convertUserToUserResponse(user))
                .build();

    }

}
