package com.shopco.verification.dto.response;

import com.shopco.verification.entity.Otp;
import lombok.Data;
import org.springframework.http.HttpStatus;

@Data
public class OtpValidationResponse {
    boolean isValid;
    private String errorMessage;
    private String details;
    private HttpStatus httpStatus;
    private Otp otp;

    public static OtpValidationResponse success(Otp otp) {
        OtpValidationResponse result = new OtpValidationResponse();
        result.isValid = true;
        result.otp = otp;
        return result;
    }

    public static OtpValidationResponse failure(HttpStatus status, String message, String details) {
        OtpValidationResponse result = new OtpValidationResponse();
        result.isValid = false;
        result.errorMessage = message;
        result.details = details;
        result.httpStatus = status;
        return result;
    }
}