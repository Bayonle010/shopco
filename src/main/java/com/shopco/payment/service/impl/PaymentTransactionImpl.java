package com.shopco.payment.service.impl;

import com.shopco.core.exception.ResourceNotFoundException;
import com.shopco.payment.dto.response.MonnifyTransactionStatusResponse;
import com.shopco.payment.entity.PaymentTransaction;
import com.shopco.payment.enums.PaymentStatus;
import com.shopco.payment.repository.PaymentTransactionRepository;
import com.shopco.payment.service.PaymentTransactionService;
import org.springframework.stereotype.Service;

@Service
public class PaymentTransactionImpl implements PaymentTransactionService {
    private final PaymentTransactionRepository paymentTransactionRepository;

    public PaymentTransactionImpl(PaymentTransactionRepository paymentTransactionRepository) {
        this.paymentTransactionRepository = paymentTransactionRepository;
    }

    @Override
    public PaymentTransaction resolvePaymentTransaction(String paymentReference) {
        return paymentTransactionRepository.findByPaymentReference(paymentReference)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Payment transaction not found for paymentReference: " + paymentReference
                ));
    }

    @Override
    public void save(PaymentTransaction paymentTransaction) {
        paymentTransactionRepository.save(paymentTransaction);
    }

    @Override
    public void updatePaymentAsPaid(PaymentTransaction paymentTx, MonnifyTransactionStatusResponse.Body body) {
        paymentTx.setStatus(PaymentStatus.PAID);
        paymentTx.setTransactionReference(body.transactionReference());
        paymentTransactionRepository.save(paymentTx);
    }
}
