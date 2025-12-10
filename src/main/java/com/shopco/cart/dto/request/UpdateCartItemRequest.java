package com.shopco.cart.dto.request;

import jakarta.validation.constraints.Positive;
import lombok.Data;
import lombok.NonNull;

import java.util.UUID;

public record UpdateCartItemRequest (
        @Positive
        @NonNull
        Integer quantity
){}
