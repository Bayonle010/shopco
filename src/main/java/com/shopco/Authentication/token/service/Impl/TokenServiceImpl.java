package com.shopco.Authentication.token.service.Impl;

import com.shopco.Authentication.token.Token;
import com.shopco.Authentication.token.TokenRepository;
import com.shopco.Authentication.token.TokenType;
import com.shopco.Authentication.token.service.TokenService;
import com.shopco.core.exception.InvalidCredentialException;
import com.shopco.user.entity.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;

@Slf4j
@Service
public class TokenServiceImpl implements TokenService {
    private final TokenRepository tokenRepository;
    private final JwtDecoder jwtDecoder;

    public TokenServiceImpl(TokenRepository tokenRepository, JwtDecoder jwtDecoder) {
        this.tokenRepository = tokenRepository;
        this.jwtDecoder = jwtDecoder;
    }


    @Override
    public void saveRefreshToken(User user, String refreshToken) {
        Jwt decodedJwt = jwtDecoder.decode(refreshToken);
        log.info("token decoded from jwt {}", (Object) decodedJwt.getClaim("tokenId"));

        Token token = Token.builder()
                .user(user)
                .token(decodedJwt.getClaim("tokenId")) //
                .tokenType(TokenType.BEARER)
                .createdAt(decodedJwt.getIssuedAt())
                .expiresAt(decodedJwt.getExpiresAt())
                .revoked(false)
                .expired(false)
                .build();
        tokenRepository.save(token);
    }



    @Override
    public void revokeAllUserTokens(User user) {
        List<Token> validTokens = tokenRepository.findAllValidTokensByUser(user.getId());


        if (validTokens.isEmpty()) {
            throw new InvalidCredentialException("user already logged out");
        }


        validTokens.forEach(token -> {
            token.setRevoked(true);
            token.setExpired(true);
        });

        tokenRepository.saveAll(validTokens);


    }


    @Override
    public boolean isRefreshTokenValid(String refreshToken, User user) {
        String tokenDecodedFromJwt = jwtDecoder.decode(refreshToken).getClaim("tokenId");

        Token token = tokenRepository.findByToken(tokenDecodedFromJwt).orElseThrow(()-> new InvalidCredentialException("token not found"));
        if (token.isExpired() || token.isRevoked()){
            throw new InvalidCredentialException("token is no longer valid");
        }
        return true;
    }


    @Scheduled(fixedRate = 1000 * 60 * 60) //deleting every 1 hour
    @Override
    public void deleteExpiredTokens() {
        Instant now = Instant.now();
        log.info("Cleaning up expired Tokens at {}", now);

        tokenRepository.deleteAllExpiredSince(now);

        log.info("Expired tokens cleaned up completed at {}", now);
    }


}
