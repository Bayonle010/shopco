package com.shopco.payment.dto.response;

import java.util.List;

public record PaymentInitializationResponse(
        boolean requestSuccessful,
        String responseCode,
        PaymentInitializationResponseBody responseBody
) {

    public record PaymentInitializationResponseBody(
            String transactionReference,
            String paymentReference,
            String merchantName,
            List<String> enabledPaymentMethod,
            String checkoutUrl
    ){

    }
}
