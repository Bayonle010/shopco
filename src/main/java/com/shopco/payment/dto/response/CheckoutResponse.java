package com.shopco.payment.dto.response;

import java.math.BigDecimal;
import java.util.List;

public record CheckoutResponse(
        boolean requestSuccessful,
        String responseMessage,
        String responseCode,
        CheckoutResponseBody responseBody
) {
    public record CheckoutResponseBody(
            String transactionReference,
            String paymentReference,
            String merchantName,
            String apiKey,
            List<String> enabledPaymentMethod,
            String checkoutUrl,
            List<IncomeSplitConfig> incomeSplitConfig
    ){
        public record IncomeSplitConfig(
                String subAccountCode,
                BigDecimal splitAmount,
                BigDecimal feePercentage,
                boolean feeBearer,
                BigDecimal splitPercentage
        ) {}
    }
}
