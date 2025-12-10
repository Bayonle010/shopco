package com.shopco.payment.dto.response;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

public record MonnifyTransactionStatusResponse(
        boolean requestSuccessful,
        String responseMessage,
        String responseCode,
        Body responseBody
) {
    public record Body(
            String transactionReference,
            String paymentReference,
            BigDecimal amountPaid,
            BigDecimal totalPayable,
            BigDecimal settlementAmount,
            String paidOn,
            String paymentStatus,      // "PAID", "PENDING", etc.
            String paymentDescription,
            String currency,
            String paymentMethod,
            Product product,
            CardDetails cardDetails,
            Object accountDetails,
            List<Object> accountPayments,
            Customer customer,
            Map<String, Object> metaData
    ) {
        public record Product(
                String type,
                String reference
        ) {}

        public record CardDetails(
                String cardType,
                String last4,
                String expMonth,
                String expYear,
                String bin,
                String bankCode,
                String bankName,
                boolean reusable,
                String countryCode,
                String cardToken,
                boolean supportsTokenization,
                String maskedPan
        ) {}

        public record Customer(
                String email,
                String name
        ) {}
    }
}
