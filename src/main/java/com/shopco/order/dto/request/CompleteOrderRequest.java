package com.shopco.order.dto.request;

import lombok.Builder;

@Builder
public record CompleteOrderRequest(
        String confirmationCode
) {
}
