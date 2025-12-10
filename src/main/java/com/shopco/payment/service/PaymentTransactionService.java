package com.shopco.payment.service;

import com.shopco.payment.dto.response.MonnifyTransactionStatusResponse;
import com.shopco.payment.entity.PaymentTransaction;

public interface PaymentTransactionService {
    PaymentTransaction resolvePaymentTransaction(String paymentReference);
    void save(PaymentTransaction paymentTransaction);
    void updatePaymentAsPaid(PaymentTransaction paymentTx, MonnifyTransactionStatusResponse.Body body);
}
