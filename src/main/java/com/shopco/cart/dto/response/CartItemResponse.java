package com.shopco.cart.dto.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.util.UUID;

@Builder
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CartItemResponse {
    private UUID cartItemId;
    private UUID productId;
    private UUID productVariantId;       // nullable
    private String title;         // snapshot or product name
    private String imageUrl;      // snapshot or product image
    private String attributesJson; // e.g. {"color":"Blue","size":"M"}
    private int quantity;
    private BigDecimal listPrice; //100
    private BigDecimal unitPrice; // snapshot
    private BigDecimal discountPercent;
    private BigDecimal itemDiscount;
    private BigDecimal lineTotal; // unitPrice * quantity
    private String currency;
}
