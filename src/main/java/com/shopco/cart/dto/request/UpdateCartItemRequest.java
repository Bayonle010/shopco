package com.shopco.cart.dto.request;

import jakarta.validation.constraints.Positive;
import lombok.Data;
import lombok.NonNull;

import java.util.UUID;

@Data
public class UpdateCartItemRequest {
    @NonNull
    private UUID cartItemId;

    @Positive
    @NonNull
    private Integer quantity;
}
