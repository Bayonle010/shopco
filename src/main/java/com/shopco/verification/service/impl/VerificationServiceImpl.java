package com.shopco.verification.service.impl;

import com.shopco.core.exception.ResourceNotFoundException;
import com.shopco.core.response.ApiResponse;
import com.shopco.core.response.ResponseUtil;
import com.shopco.core.utils.NumberUtil;
import com.shopco.mail.EmailService;
import com.shopco.mail.EmailTemplateName;
import com.shopco.user.model.UserDto;
import com.shopco.verification.dto.request.GenerateOtpRequest;
import com.shopco.verification.dto.request.VerifyOtpRequest;
import com.shopco.verification.dto.response.OtpValidationResponse;
import com.shopco.verification.entity.Otp;
import com.shopco.user.entity.User;
import com.shopco.verification.enums.OtpEvent;
import com.shopco.verification.repositories.OtpRepository;
import com.shopco.user.repositories.UserRepository;
import jakarta.mail.MessagingException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.time.Duration;
import java.time.Instant;

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

    @Override
    public ResponseEntity<ApiResponse> handleGenerateOtp(GenerateOtpRequest generateOtpRequest) throws MessagingException {
        String numericOTP = NumberUtil.generateNumericOtp();
        String formattedEmail = generateOtpRequest.getEmail().toLowerCase().trim();

        User user = userRepository.findByEmail(formattedEmail).orElseThrow(()-> new ResourceNotFoundException("user not found"));



        //send email
        emailService.sendEmail(
                formattedEmail,
                user.getUsername(),
                EmailTemplateName.ACTIVATE_ACCOUNT,
                numericOTP,
                "Account Activation"
        );

        String userEmail = user.getEmail();
        Otp otp = otpRepository.findByEmailAndOtpEvent(userEmail, OtpEvent.SIGN_UP);
        if (ObjectUtils.isEmpty(otp)) {
            otp = new Otp();
            otp.setOtpEvent(OtpEvent.SIGN_UP);
            otp.setEmail(userEmail);
        }
        otp.setToken(numericOTP);
        otp.setExpired(false);
        otp.setExpiryTime(Instant.now().plus(Duration.ofSeconds(120)));

        otpRepository.save(otp);

        return ResponseEntity.status(HttpStatus.OK).body(ResponseUtil.success(0, String.format("OTP sent to %s ", formattedEmail), null, null));

    }

    /**
     * @param otpToken 
     * @param email
     * @param expectedEvent
     * @return
     */
    @Override
    public OtpValidationResponse validateOtp(String otpToken, String email, OtpEvent expectedEvent) {
        Otp otp = otpRepository.findByToken(otpToken);
        if(ObjectUtils.isEmpty(otp)){
            return OtpValidationResponse.failure(HttpStatus.NOT_FOUND, "Invalid OTP", "Operation failed");
        }

        if (!otp.getOtpEvent().equals(expectedEvent)) {
            return OtpValidationResponse.failure(HttpStatus.BAD_REQUEST, "Invalid OTP", "Operation failed");
        }

        if(!otp.getEmail().equals(email)){
            return OtpValidationResponse.failure(HttpStatus.BAD_REQUEST, "OTP from invalid email", "Operation failed");
        }

        if(isOtpExpired(otp)){
            otpRepository.delete(otp);
            return OtpValidationResponse.failure(HttpStatus.BAD_REQUEST, "OTP expired. Please request a new one.", "Expired OTP");
        }

        if(isOtpInvalid(otp, otpToken)){
            return OtpValidationResponse.failure(HttpStatus.BAD_REQUEST, "OTP Incorrect", "Invalid OTP");
        }

        return OtpValidationResponse.success(otp);
    }


    @Override
    public ResponseEntity<ApiResponse> verifyOtp(VerifyOtpRequest verifyOtpRequest) {
        OtpValidationResponse result = validateOtp(verifyOtpRequest.getOtp(), verifyOtpRequest.getEmail(), OtpEvent.SIGN_UP);

        if(!result.isValid()){
            return ResponseEntity.status(result.getHttpStatus()).body(ResponseUtil.error(99, result.getErrorMessage(), result.getDetails(), null));
        }

        Otp otp = result.getOtp();
        otpRepository.delete(otp);

        // OTP EVENT HANDLER ( handle what happens after otp is valid for each event is valid here  )
        User user = userRepository
                .findByEmail(otp.getEmail()).orElseThrow(()-> new ResourceNotFoundException("user not found"));


        if (!ObjectUtils.isEmpty(user)) {
            user.setVerified(true); //Marked user as a verified user
            userRepository.save(user);

        }
        UserDto userInfo = UserDto.convertUserEntityToUserDto(user);
        return ResponseEntity.status(HttpStatus.OK).body(ResponseUtil.success(0, "Successful verification", userInfo, null));

    }


    private boolean isOtpExpired(Otp otpEntity) {
        return otpEntity.isExpired() || Instant.now().isAfter(otpEntity.getExpiryTime());
    }


    private boolean isOtpInvalid(Otp otpEntity, String otp) {
        return !otpEntity.getToken().equals(otp);
    }
    
}


