package com.shopco.cart.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import lombok.Data;
import lombok.NonNull;

import java.util.UUID;

@Data
public class CartRequest {
    @NonNull
    private UUID productId;

    @NonNull
    private UUID productVariantId;

    @NonNull
    @Positive
    private Integer quantity;
}
