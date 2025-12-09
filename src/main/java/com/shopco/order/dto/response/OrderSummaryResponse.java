package com.shopco.order.dto.response;

import com.shopco.order.enums.OrderStatus;
import lombok.Builder;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Builder
public record OrderSummaryResponse(
        UUID orderId,
        OrderStatus orderStatus,
        String confirmationCode,
        Instant placedAt,
        Instant completedAt,
        BigDecimal amount
) {
}
