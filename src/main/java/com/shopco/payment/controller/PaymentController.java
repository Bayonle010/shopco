package com.shopco.payment.controller;

import com.shopco.core.response.ApiResponse;
import com.shopco.payment.service.PaymentService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@Tag(name = "Payments")
@RestController
@RequestMapping("/api/v1/payments")
public class PaymentController {
    private final PaymentService paymentService;

    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @PostMapping("/check-out")
    public ResponseEntity<ApiResponse> checkoutCart(@RequestParam UUID cartId, Authentication authentication){
        return paymentService.initializePayment(cartId, authentication);
    }

//    @GetMapping("/monnify/redirect")
//    public ResponseEntity<ApiResponse> handleRedirect(@RequestParam("paymentReference") String paymentReference) {
//        // Option A: verify here directly
//        return paymentService.handleMonnifyPaymentCompletion(paymentReference);
//    }
}
