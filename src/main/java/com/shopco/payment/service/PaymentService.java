package com.shopco.payment.service;

import com.shopco.core.response.ApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;

import java.util.UUID;

public interface PaymentService {
    String getAccessTokenFromMonnify();
    ResponseEntity<ApiResponse> initializePayment(UUID cartId, Authentication authentication);

}
