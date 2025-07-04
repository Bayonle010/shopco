package com.shopco.user.service;

import com.shopco.user.dto.VerifyOtpRequest;

public interface VerificationService {
    String verifyOtp(VerifyOtpRequest request);
}
