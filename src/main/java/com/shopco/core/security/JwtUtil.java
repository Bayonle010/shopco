package com.shopco.core.security;

import com.shopco.core.exception.InvalidCredentialException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.*;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Collection;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;


@Slf4j
@Service
public class JwtUtil {

    private final static Long ACCESS_TOKEN_DURATION_MS = 15 * 60 * 1000L;  // set to 15 minutes
    // NOTE => 1000L = 1000ms = 1s
    private final static long REFRESH_TOKEN_DURATION_MS =   24 * 60 * 60 * 1000L; // set to 24 hours


    private final JwtEncoder jwtEncoder;
    private final JwtDecoder jwtDecoder;


    public JwtUtil(JwtEncoder jwtEncoder, JwtDecoder jwtDecoder) {
        this.jwtEncoder = jwtEncoder;
        this.jwtDecoder = jwtDecoder;
    }

    public String generateAccessToken(Authentication auth, String email){
        return buildToken(auth, email, ACCESS_TOKEN_DURATION_MS);
    }

    public String generateRefreshToken(Authentication auth, String email){
        String tokenId = UUID.randomUUID().toString();
        log.info("tokenId generated {}", tokenId);
        return buildTokenWithTokenId(auth, email, REFRESH_TOKEN_DURATION_MS, tokenId);
    }

    public String buildToken(Authentication auth, String userEmail, Long expirationTime) {
        log.info("Generating JWT for user: {}", userEmail);

        List<String> roles = auth.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .map(role -> role.startsWith("ROLE_") ? role : "ROLE_" + role)
                .collect(Collectors.toList());


        if (roles.isEmpty()) {
            roles.add("ROLE_USER"); // Default role if no authorities are found
        }

        JwtClaimsSet claims = JwtClaimsSet.builder()
                .issuer("bayfi")
                .issuedAt(Instant.now())
                .subject(userEmail)
                .claim("roles", roles)
                .expiresAt(Instant.now().plusMillis(expirationTime))
                .build();

        return jwtEncoder.encode(JwtEncoderParameters.from(claims)).getTokenValue();
    }

    public String buildTokenWithTokenId(Authentication auth, String userEmail, Long expirationTime, String tokenId) {
        List<String> roles = auth.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList());

        if (roles.isEmpty()) {
            roles.add("ROLE_USER");
        }

        Instant now = Instant.now();

        JwtClaimsSet claims = JwtClaimsSet.builder()
                .issuer("bayfi")
                .issuedAt(now)
                .expiresAt(now.plusMillis(expirationTime))
                .subject(userEmail)
                .claim("roles", roles)
                .claim("tokenId", tokenId) // Important
                .build();

        return jwtEncoder.encode(JwtEncoderParameters.from(claims)).getTokenValue();
    }


    public Jwt decodeJwt(String token) {

        try {
            Jwt decodedToken =  jwtDecoder.decode(token);
            log.info("Decoded Jwt Token, result = {}", decodedToken);
            return decodedToken;
        } catch (Exception e) {
            log.error("Failed to decode JWT token", e);
            throw new InvalidCredentialException("Invalid JWT token");
        }
    }

    public Collection<? extends GrantedAuthority> extractAuthorities(Jwt jwt) {
        // Extract roles from the JWT and convert them to GrantedAuthority objects
        List<String> roles = jwt.getClaimAsStringList("roles");
        return roles.stream()
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());
    }
}