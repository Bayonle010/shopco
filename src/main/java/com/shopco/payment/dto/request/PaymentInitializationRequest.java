package com.shopco.payment.dto.request;

import lombok.Builder;

import java.math.BigDecimal;
import java.util.ArrayList;

@Builder
public record PaymentInitializationRequest(
        BigDecimal amount,
        String customerName,
        String customerEmail,
        String paymentDescription,
        String paymentReference,
        String currencyCode,
        String contractCode,
        String redirectUrl,
        ArrayList<String> paymentMethods
) {
}
