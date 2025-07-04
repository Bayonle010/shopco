package com.shopco.mail;

import com.shopco.user.entity.Otp;
import com.shopco.user.entity.User;
import com.shopco.user.repositories.OtpRepository;
import jakarta.mail.MessagingException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.time.LocalDateTime;

@Slf4j
@Service
public class MailTokenService {

    private final OtpRepository otpRepository;
    private final EmailService emailService;

   // private String activationUrl;

    public MailTokenService(OtpRepository otpRepository, EmailService emailService) {
        this.otpRepository = otpRepository;
        this.emailService = emailService;
    }

    public void sendEmailVerificationTo(User user) throws MessagingException {
        var newToken = generateAndSaveVerificationToken(user);
        log.info("generating a new token {}",newToken);

        //send email
        emailService.sendEmail(
                user.getEmail(),
                user.getFirstname(),
                EmailTemplateName.ACTIVATE_ACCOUNT,
              //  activationUrl,
                newToken,
                "Account Activation"

        );
    }

    private String generateAndSaveVerificationToken(User user){
        String generatedToken = generateActivationCode(5);
        var token = Otp.builder()
                .token(generatedToken)
                .user(user)
                .createdAt(LocalDateTime.now())
                .expiresAt(LocalDateTime.now().plusMinutes(15))
                .build();

        otpRepository.save(token);

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
