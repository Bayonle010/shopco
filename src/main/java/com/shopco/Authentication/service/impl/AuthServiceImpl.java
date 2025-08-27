package com.shopco.Authentication.service.impl;

import com.shopco.Authentication.dto.AuthResponse;
import com.shopco.Authentication.dto.RefreshTokenRequest;
import com.shopco.Authentication.dto.SignInRequest;
import com.shopco.Authentication.dto.SignUpRequest;
import com.shopco.Authentication.service.AuthService;
import com.shopco.Authentication.token.service.Impl.TokenServiceImpl;
import com.shopco.core.exception.*;
import com.shopco.core.response.ApiResponse;
import com.shopco.core.response.ResponseUtil;
import com.shopco.core.security.JwtUtil;
import com.shopco.role.Role;
import com.shopco.role.RoleRepository;
import com.shopco.verification.dto.request.GenerateOtpRequest;
import com.shopco.user.entity.User;
import com.shopco.user.model.UserDto;
import com.shopco.user.repositories.UserRepository;
import com.shopco.verification.service.impl.VerificationServiceImpl;
import jakarta.mail.MessagingException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

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
    private final VerificationServiceImpl verificationService;



    @Autowired
    private JwtUtil jwtUtil;

    public AuthServiceImpl(UserRepository userRepository, PasswordEncoder passwordEncoder, RoleRepository roleRepository, AuthenticationManager authenticationManager, JwtUtil jwtUtil, TokenServiceImpl tokenService, VerificationServiceImpl verificationService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.roleRepository = roleRepository;
        this.authenticationManager = authenticationManager;
        this.tokenService = tokenService;
        this.verificationService = verificationService;
    }

    @Transactional
    @Override
    public ResponseEntity<ApiResponse> registerUser(SignUpRequest request) throws MessagingException {

        // Check if user already exists
        String formatedEmailFromRequest = request.getEmail().toLowerCase();
        Optional<User> existingUserByEmail = userRepository.findByEmail(formatedEmailFromRequest);
        if (existingUserByEmail.isPresent()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ResponseUtil.error(99, "email already exist", "", ""));
        }


        String formatedUsernameFromRequest = request.getUsername().toUpperCase();
        Optional<User> existingUserByUsername = userRepository.findByUsername(formatedUsernameFromRequest);
        if (existingUserByUsername.isPresent()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ResponseUtil.error(99, "username already exist", "", ""));
        }


        // Encode password
        String encodedPassword = passwordEncoder.encode(request.getPassword());

        // Create new user
        User newUser = User.builder()
                .firstname(request.getFirstname())
                .lastname(request.getLastname())
                .email(formatedEmailFromRequest)
                .username(formatedUsernameFromRequest)
                .password(encodedPassword)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .isAccountNonExpired(true)
                .isAccountNonLocked(true)
                .isCredentialsNonExpired(true)
                .isEnabled(true)
                .build();

        //Initialize roles if null
        if(newUser.getRoles() == null){
            newUser.setRoles(new HashSet<>());
        }

        Optional<Role> userRole = roleRepository.findByAuthority("ROLE_USER");
        userRole.ifPresent(role -> newUser.getRoles().add(role));

        // Save user to database
        User savedUser = userRepository.save(newUser);

        UserDto userDto = UserDto.convertUserEntityToUserDto(savedUser);


        //send email activation code to user
        return  verificationService.handleGenerateOtp(
                GenerateOtpRequest.builder().email(newUser.getEmail()).build()
        );

    }



    @Override
    public ResponseEntity<ApiResponse> authenticateUser(SignInRequest signInRequest){
        String formatedEmailFromRequest = signInRequest.getEmail().toLowerCase();

        User user = userRepository.findByEmail(formatedEmailFromRequest).orElseThrow(()-> new ResourceNotFoundException("user not found"));

        if(ObjectUtils.isEmpty(user)){
            log.info("User not found with email: {}", formatedEmailFromRequest);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ResponseUtil.error(99, "user not found", "", null));
        }

        if(!user.isVerified()){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(ResponseUtil.error(99, "email verification is required", null, null));
        }

        try {

            Authentication authentication = authenticationManager.authenticate(

                    new UsernamePasswordAuthenticationToken(formatedEmailFromRequest, signInRequest.getPassword())
            );
            log.info("User authenticated successfully. Generating JWT token...");

            // return jwt token.
            String accessToken = jwtUtil.generateAccessToken(authentication, formatedEmailFromRequest);

            String refreshToken = jwtUtil.generateRefreshToken(authentication, formatedEmailFromRequest);

            tokenService.saveRefreshToken(user, refreshToken);

            UserDto userInfo = UserDto.convertUserEntityToUserDto(user);


            AuthResponse response = AuthResponse.builder()
                    .accessToken(accessToken)
                    .refreshToken(refreshToken)
                    .userDto(userInfo)
                    .build();

            return ResponseEntity.status(HttpStatus.OK).body(ResponseUtil.success(0, "login successful", response, null));

        }catch (AuthenticationException e){
            log.error("Invalid email or Password {}", e.getMessage());
            throw new InvalidCredentialException("invalid email or password");
        }

    }


    @Override
    public ResponseEntity<ApiResponse>  refreshToken(RefreshTokenRequest request) {

        String refreshToken = request.getRefreshToken();


        Jwt decodedJwt = jwtUtil.decodeJwt(refreshToken); // validates structure, signature, expiry

        String email = decodedJwt.getSubject();

        User user = userRepository.findByEmail(email).orElseThrow(()-> new ResourceNotFoundException("resource not found"));


        if (ObjectUtils.isEmpty(user)){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ResponseUtil.error(99, "user not found", "", ""));
        }




        boolean isValidToken = tokenService.isRefreshTokenValid(refreshToken, user);



        // Generate new access token
        Authentication authentication = new UsernamePasswordAuthenticationToken(
                user.getEmail(), null, user.getAuthorities());


        String accessToken = jwtUtil.generateAccessToken(authentication, user.getEmail());

        AuthResponse response =  AuthResponse.builder()
                .accessToken(accessToken)
                .build();

        return ResponseEntity.status(HttpStatus.OK).body(ResponseUtil.success(0, "access token retrieved", response, null));
    }



    @Override
    public ResponseEntity<ApiResponse> logout(HttpServletRequest request) {
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

        return ResponseEntity.status(HttpStatus.OK).body(ResponseUtil.success(0, "success", "", null));

    }

}
