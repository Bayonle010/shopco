package com.shopco.core.security;

import com.shopco.user.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.jwt.*;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Component
@Slf4j
public class JwtUtil {

    private final static Long JWT_EXPIRATION_TIME = 15 * 60 * 1000L;  // set to 15 minutes
    // NOTE => 1000L = 1000ms = 1s
    public final static long REFRESH_TOKEN_EXPIRATION_TIME = 7 * 24 * 60 * 60 * 1000L; // 7days


    private final JwtEncoder jwtEncoder;
    private final JwtDecoder jwtDecoder;


    public JwtUtil(JwtEncoder jwtEncoder, JwtDecoder jwtDecoder) {
        this.jwtEncoder = jwtEncoder;
        this.jwtDecoder = jwtDecoder;
    }

    public String generateToken(Authentication auth, User user){
        log.info("Generating JWT  access token for user: {}", user);

        List<String> roles = auth.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .map(role -> role.startsWith("ROLE_") ? role : "ROLE_" + role)
                .toList();

        if (roles.isEmpty()) {
            roles.add("ROLE_USER"); // Default role if no authorities are found
        }




        return  jwtEncoder.encode(JwtEncoderParameters.from(
                JwtClaimsSet.builder()
                        .issuer("shopco")
                        .issuedAt(Instant.now())
                        .expiresAt(Instant.now().plusMillis(JWT_EXPIRATION_TIME))
                        .subject(user.getEmail())
                        .claim("roles", roles)
                        .build()
        )).getTokenValue();
    }


    public String generateRefreshToken(Authentication auth, User user){

        log.info("Generating JWT  refresh token for user: {}", user);

        List<String> roles = auth.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .map(role -> role.startsWith("ROLE_") ? role : "ROLE_" + role)
                .toList();


        return  jwtEncoder.encode(JwtEncoderParameters.from(
                JwtClaimsSet.builder()
                        .issuer("shopco")
                        .issuedAt(Instant.now())
                        .expiresAt(Instant.now().plusMillis(REFRESH_TOKEN_EXPIRATION_TIME))
                        .subject(user.getEmail())
                        .claim("roles", roles)
                        .build()
        )).getTokenValue();
    }

    public boolean isTokenValid(String token, User user){
        try{
            return extractUsername(token).equals(user.getEmail()) && !isTokenExpired(token);
        }catch (Exception ex){
            return false;
        }
    }

    public  String extractUsername(String token){
        return jwtDecoder.decode(token).getSubject();
    }

    public boolean isTokenExpired(String token){
        return Objects.requireNonNull(jwtDecoder.decode(token).getExpiresAt()).isBefore(Instant.now());
    }

    public Instant getExpirationTime(String token){
        return jwtDecoder.decode(token).getExpiresAt();
    }

}