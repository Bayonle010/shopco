package com.shopco.verification.controller;

import com.shopco.core.response.ApiResponse;
import com.shopco.core.response.ResponseUtil;
import com.shopco.user.dto.request.GenerateOtpRequest;
import com.shopco.user.dto.request.VerifyOtpRequest;
import com.shopco.user.dto.response.VerifyOtpResponse;
import com.shopco.verification.service.impl.VerificationServiceImpl;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.mail.MessagingException;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "verification", description = "Verification Api")
@RestController
@RequestMapping("/api/v1/otp")
public class VerificationController {
    private final VerificationServiceImpl verificationService;

    public VerificationController(VerificationServiceImpl verificationService) {
        this.verificationService = verificationService;
    }


    @Operation(
            summary = "Generate otp for verification",
            description = "Generates verification OTP for the provided email. The OTP is valid for 15 minutes"
    )
    @PostMapping("/send")
    public ResponseEntity<ApiResponse> generateOtp(@RequestBody @Valid GenerateOtpRequest generateOtpRequest) throws MessagingException {
        verificationService.handleGenerateOtp(generateOtpRequest);
        return new ResponseEntity<>(ResponseUtil.success(
                HttpStatus.OK.value(), "otp sent to email", null, null
        ), HttpStatus.OK);
    }


    @Operation(
            summary = "Verify Otp",
            description = "The OTP must be valid and not expired (15-minute validity)"
    )
    @PostMapping("/verify")
    public ResponseEntity<ApiResponse> verifyOtp(@RequestBody @Valid VerifyOtpRequest verifyOtpRequest){
        VerifyOtpResponse response = verificationService.verifyOtp(verifyOtpRequest);
        return  new ResponseEntity<>(ResponseUtil.success(
                HttpStatus.OK.value(), "success", response, null
        ), HttpStatus.OK);
    }
}
