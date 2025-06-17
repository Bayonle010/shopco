package com.shopco.Authentication.token;

import com.shopco.user.User;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Service
public class TokenService {
    private final TokenRepository tokenRepository;
    private final JwtDecoder jwtDecoder;

    public TokenService(TokenRepository refreshTokenRepository, JwtDecoder jwtDecoder) {
        this.tokenRepository = refreshTokenRepository;
        this.jwtDecoder = jwtDecoder;
    }

    public void saveUserToken(User user, String jwtToken){
        Jwt decodedJwt = jwtDecoder.decode(jwtToken);
        Token token = Token.builder()
                .user(user)
                .token(jwtToken)
                .tokenType(TokenType.BEARER)
                .createdAt(decodedJwt.getIssuedAt())
                .expiresAt(decodedJwt.getExpiresAt())
                .expired(false)
                .revoked(false)
                .build();

        tokenRepository.save(token);

    }



    public void revokeAllUserTokens(User user){
        List<Token> validUserTokens = tokenRepository.findAllValidTokensByUser(user.getId());

        if(validUserTokens.isEmpty()){
            return;
        }

        for(Token token : validUserTokens){
            if(token.getExpiresAt() != null && token.getExpiresAt().isBefore(Instant.now())){
                continue;
            }

            token.setRevoked(true);
            token.setExpired(true);
        }

        tokenRepository.saveAll(validUserTokens);


    }

    public void findAndRevokeToken(String token){

        Token storedToken = tokenRepository.findByToken(token).orElse(null);
        if(storedToken != null){
            storedToken.setExpired(true);
            storedToken.setRevoked(true);
            tokenRepository.save(storedToken);
        }

    }

    public Optional<Token> getValidRefreshToken(String token){
        return tokenRepository.findByToken(token)
                .filter(t -> !t.isExpired() && !t.isRevoked());
    }

    public void revokeToken(Token token){
        token.setExpired(true);
        token.setRevoked(true);
    }

}
