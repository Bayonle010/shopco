package com.shopco.core.security;

import com.shopco.user.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.oauth2.jwt.*;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.Objects;

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

    public String generateToken(User user){
        return  jwtEncoder.encode(JwtEncoderParameters.from(
                JwtClaimsSet.builder()
                        .issuer("shopco")
                        .issuedAt(Instant.now())
                        .expiresAt(Instant.now().plusMillis(JWT_EXPIRATION_TIME))
                        .subject(user.getEmail())
                        .claim("roles", user.getRoles())
                        .build()
        )).getTokenValue();
    }


    public String generateRefreshToken(User user){
        return  jwtEncoder.encode(JwtEncoderParameters.from(
                JwtClaimsSet.builder()
                        .issuer("shopco")
                        .issuedAt(Instant.now().plusSeconds(0))
                        .expiresAt(Instant.now().plusMillis(REFRESH_TOKEN_EXPIRATION_TIME))
                        .subject(user.getEmail())
                        .claim("roles", user.getRoles())
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


//    public String generateJwt(Authentication auth, String userEmail) {
//        log.info("Generating JWT for user: {}", userEmail);
//        List<String> roles = auth.getAuthorities().stream()
//                .map(GrantedAuthority::getAuthority)
//                .map(role -> role.startsWith("ROLE_") ? role : "ROLE_" + role)
//                .collect(Collectors.toList());
//
//
//        if (roles.isEmpty()) {
//            roles.add("ROLE_USER"); // Default role if no authorities are found
//        }
//
//        JwtClaimsSet claims = JwtClaimsSet.builder()
//                .issuer("shopco")
//                .issuedAt(Instant.now())
//                .subject(userEmail)
//                .claim("roles", roles)
//                .expiresAt(Instant.now().plusMillis(JWT_EXPIRATION_TIME))
//                .build();
//
//        return jwtEncoder.encode(JwtEncoderParameters.from(claims)).getTokenValue();
//    }

//    public String decodeJwt(String token) {
//        log.info("Decoding JWT token");
//        try {
//            return jwtDecoder.decode(token).getSubject();
//        } catch (Exception e) {
//            log.error("Failed to decode JWT token", e);
//            throw new RuntimeException("Invalid JWT token", e);
//        }
//    }
//
//    public Collection<? extends GrantedAuthority> extractAuthorities(Jwt jwt) {
//        // Extract roles from the JWT and convert them to GrantedAuthority objects
//        List<String> roles = jwt.getClaimAsStringList("roles");
//        return roles.stream()
//                .map(SimpleGrantedAuthority::new)
//                .collect(Collectors.toList());
//    }
//
//    public long getRefreshTokenExpirationMs() {
//        return REFRESH_TOKEN_EXPIRATION_TIME;
//    }
//
//    public String generateRefreshToken(String userEmail) {
//        JwtClaimsSet claims = JwtClaimsSet.builder()
//                .issuer("shopco")
//                .issuedAt(Instant.now())
//                .subject(userEmail)
//                .expiresAt(Instant.now().plusMillis(REFRESH_TOKEN_EXPIRATION_TIME))
//                .build();
//
//        return UUID.randomUUID().toString() + jwtEncoder.encode(JwtEncoderParameters.from(claims)).getTokenValue();
//    }
//
//    public boolean isTokenExpired(Jwt token) {
//        return token.getExpiresAt() != null && token.getExpiresAt().isBefore(Instant.now());
//    }
}