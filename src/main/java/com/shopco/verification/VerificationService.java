package com.shopco.verification;

import com.shopco.user.dto.request.GenerateOtpRequest;
import com.shopco.user.dto.request.VerifyOtpRequest;
import com.shopco.user.dto.response.VerifyOtpResponse;
import jakarta.mail.MessagingException;

public interface VerificationService {
    void handleGenerateOtp(GenerateOtpRequest generateOtpRequest) throws MessagingException;
    VerifyOtpResponse verifyOtp(VerifyOtpRequest verifyOtpRequest);
}
