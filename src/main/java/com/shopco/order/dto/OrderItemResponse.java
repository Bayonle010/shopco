package com.shopco.order.dto;

import lombok.Builder;

import java.math.BigDecimal;
import java.util.UUID;

@Builder
public record OrderItemResponse(
        UUID orderItemId,
        UUID productVariantId,
        int quantity,
        BigDecimal unitPrice,
        BigDecimal totalPrice
) {
}
