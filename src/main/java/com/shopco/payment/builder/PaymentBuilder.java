package com.shopco.payment.builder;

import com.shopco.cart.entity.Cart;
import com.shopco.cart.entity.CartItem;
import com.shopco.payment.dto.request.PaymentInitializationRequest;
import com.shopco.payment.enums.PaymentMethods;
import com.shopco.payment.util.PaymentUtil;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;

import java.math.BigDecimal;
import java.util.ArrayList;

@Getter
public class PaymentBuilder {

    public  static PaymentInitializationRequest buildPaymentRequest(Cart cart, String contractCode, String redirectUri){

        ArrayList<String> paymentMethods = new ArrayList<>();
        paymentMethods.add(PaymentMethods.CARD.name());
        paymentMethods.add(PaymentMethods.ACCOUNT_TRANSFER.name());

        return PaymentInitializationRequest.builder()
                .amount(calculateTotalAmount(cart))
                .customerName(cart.getUser().getFirstname())
                .customerEmail(cart.getUser().getEmail())
                .paymentDescription("Order Payment")
                .paymentReference(PaymentUtil.generatePaymentReference())
                .paymentMethods(paymentMethods)
                .contractCode(contractCode)
                .currencyCode("NGN")
                .redirectUrl(redirectUri)
                .build();
    }

    // Method to calculate total amount (you can extract it from the cart)
    private static BigDecimal calculateTotalAmount(Cart cart) {
        // logic to calculate total
        return cart.getItems().stream()
                .map(CartItem::getListPriceSnapshot)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}
