package com.shopco.Authentication.refreshtoken;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.Instant;

@Component
@RequiredArgsConstructor
@Slf4j
public class TokenCleanUpScheduler {

    private final TokenRepository tokenRepository;

    @Scheduled(fixedRate = 1000 * 60 * 10)
    public void deleteExpiredTokens() {
        Instant now = Instant.now();
        log.info("Cleaning up expired Tokens at {}", now);
        tokenRepository.deleteExpiredTokens(now);
        log.info("Expired tokens cleaned up completed at {}", now);
    }
}
