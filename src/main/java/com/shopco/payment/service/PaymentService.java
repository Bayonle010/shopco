package com.shopco.payment.service;

import com.shopco.core.response.ApiResponse;
import com.shopco.payment.dto.response.MonnifyTransactionStatusResponse;
import com.shopco.payment.entity.PaymentTransaction;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;

import java.util.UUID;

public interface PaymentService {
    String getAccessTokenFromMonnify();
    ResponseEntity<ApiResponse> initializePayment(UUID cartId, Authentication authentication);
    ResponseEntity<ApiResponse> getTransactionStatus(String transactionReference);
    ResponseEntity<ApiResponse> completeCheckout(String transactionReference);
    ResponseEntity<ApiResponse> completeCheckoutByPaymentReference(String paymentReference);

}
