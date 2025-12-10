package com.shopco.verification;

import com.shopco.core.response.ApiResponse;
import com.shopco.verification.dto.request.GenerateOtpRequest;
import com.shopco.verification.dto.request.VerifyOtpRequest;
import com.shopco.verification.dto.response.OtpValidationResponse;
import com.shopco.verification.enums.OtpEvent;
import jakarta.mail.MessagingException;
import org.springframework.http.ResponseEntity;

public interface VerificationService {
    ResponseEntity<ApiResponse> handleGenerateOtp(GenerateOtpRequest generateOtpRequest) throws MessagingException;
    OtpValidationResponse validateOtp(String otpToken, String email, OtpEvent expectedEvent);
    ResponseEntity<ApiResponse> verifyOtp(VerifyOtpRequest verifyOtpRequest);
}
