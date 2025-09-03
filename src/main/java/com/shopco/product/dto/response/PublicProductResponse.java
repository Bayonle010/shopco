package com.shopco.product.dto.response;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.util.UUID;

@Builder
@Data
public class PublicProductResponse {
    private UUID id;
    private String name;
    private String description;
    private String imageUrl;
    private String category;
    private BigDecimal price;
    private double discountInPercentage;
    private BigDecimal priceAfterDiscount;
    private double rating;
    private boolean isAvailable;
}
