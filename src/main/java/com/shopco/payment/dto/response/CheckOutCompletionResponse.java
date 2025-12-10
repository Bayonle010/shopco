package com.shopco.payment.dto.response;

import com.shopco.order.enums.OrderStatus;
import lombok.Builder;

import java.math.BigDecimal;
import java.util.UUID;

@Builder
public record CheckOutCompletionResponse(
        UUID orderId,
        String confirmationCode,
        UUID cartId,
        OrderStatus orderStatus,
        BigDecimal amountPaid
) {
}
