package com.shopco.cart.dto.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Data;

import java.util.List;
import java.util.UUID;

@Builder
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CartResponse {
    private UUID cartId;
    private UUID userId;
    private String currency;
    private List<CartItemResponse> items;
    private CartSummaryResponse summary;
}
