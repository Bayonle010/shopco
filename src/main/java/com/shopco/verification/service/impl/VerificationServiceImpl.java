package com.shopco.verification.service.impl;

import com.shopco.core.exception.BadRequestException;
import com.shopco.core.exception.ResourceNotFoundException;
import com.shopco.core.utils.NumberUtil;
import com.shopco.mail.EmailService;
import com.shopco.mail.EmailTemplateName;
import com.shopco.user.dto.request.GenerateOtpRequest;
import com.shopco.user.dto.request.VerifyOtpRequest;
import com.shopco.user.dto.response.VerifyOtpResponse;
import com.shopco.verification.entity.Otp;
import com.shopco.user.entity.User;
import com.shopco.verification.repositories.OtpRepository;
import com.shopco.user.repositories.UserRepository;
import jakarta.mail.MessagingException;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.time.LocalDateTime;

@Service
public class VerificationServiceImpl implements com.shopco.verification.VerificationService {

    private final UserRepository userRepository;
    private final OtpRepository otpRepository;
    private final EmailService emailService;

    // private String activationUrl;

    public VerificationServiceImpl(UserRepository userRepository, OtpRepository otpRepository, EmailService emailService) {
        this.userRepository = userRepository;
        this.otpRepository = otpRepository;
        this.emailService = emailService;
    }

    public void handleGenerateOtp(GenerateOtpRequest generateOtpRequest) throws MessagingException {

        User user = userRepository.findByEmail(generateOtpRequest.getEmail()).orElseThrow(()-> new ResourceNotFoundException("user not found"));

        var newToken = generateAndSaveVerificationToken(user);

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


    @Override
    public VerifyOtpResponse verifyOtp(VerifyOtpRequest verifyOtpRequest) {
        Otp otp = otpRepository.findByToken(verifyOtpRequest.getOtp());
        if(ObjectUtils.isEmpty(otp)){
            throw new ResourceNotFoundException("otp not found");
        }

        if(isOtpExpired(otp)){
            otpRepository.delete(otp);
            throw new BadRequestException("otp has expired");
        }

        if(isOtpInvalid(otp, verifyOtpRequest.getOtp())){
            otpRepository.delete(otp);
            throw new BadRequestException("otp is incorrect");
        }
        otpRepository.delete(otp);

        User user = userRepository.findByEmail(otp.getUser().getEmail()).orElseThrow(null);
        user.setVerified(true);
        userRepository.save(user);


        return VerifyOtpResponse.builder()
                .message("user verified")
                .build();
    }

    private String generateAndSaveVerificationToken(User user){
        String generatedToken = NumberUtil.generateActivationCode(6);
        var token = Otp.builder()
                .token(generatedToken)
                .user(user)
                .createdAt(LocalDateTime.now())
                .expiresAt(LocalDateTime.now().plusMinutes(15))
                .build();

        otpRepository.save(token);

        return generatedToken;
    }

    private boolean isOtpExpired(Otp otpEntity) {
        return otpEntity.isExpired() || LocalDateTime.now().isAfter(otpEntity.getExpiresAt());
    }


    private boolean isOtpInvalid(Otp otpEntity, String otp) {
        return !otpEntity.getToken().equals(otp);
    }

}
