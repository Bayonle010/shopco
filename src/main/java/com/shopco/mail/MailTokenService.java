package com.shopco.mail;

import com.shopco.user.User;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.time.LocalDateTime;

@Service
public class MailTokenService {

    private final MailTokenRepository mailTokenRepository;

    public MailTokenService(MailTokenRepository mailTokenRepository) {
        this.mailTokenRepository = mailTokenRepository;
    }

    public void sendEmailVerificationTo(User user){
        var newToken = generateAndSaveVerificationToken(user);
        //send email
    }

    private String generateAndSaveVerificationToken(User user){
        String generatedToken = generateActivationCode(5);
        var token = MailToken.builder()
                .token(generatedToken)
                .user(user)
                .createdAt(LocalDateTime.now())
                .expiresAt(LocalDateTime.now().plusMinutes(15))
                .build();

        mailTokenRepository.save(token);

        return generatedToken;
    }


    private String generateActivationCode(int length){
        String characters = "0123456789";
        StringBuilder codeBuilder = new StringBuilder();
        SecureRandom secureRandom = new SecureRandom();
        for (int i = 0; i<length; i++){
            int randomIndex = secureRandom.nextInt(characters.length()); //0 to 9
            codeBuilder.append(characters.charAt(randomIndex));
        }
        return codeBuilder.toString();
    }

}
