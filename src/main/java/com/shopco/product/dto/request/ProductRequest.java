package com.shopco.product.dto.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class ProductRequest {
    @NotBlank(message = "product name cannot be empty")
    private String name;

    @NotBlank(message = "product description cannot be blank")
    private String description;

    @DecimalMin(value = "0.00", inclusive = true, message = "price must be at least 0.00")
    @NotNull(message = "price cannot blank")
    private BigDecimal price;

    @NotNull(message = "discount cannot be blank, input 0.00 if need be to be blank")
    private double discountInPercentage;

    @NotBlank(message = "product image url")
    private String imageUrl;

    @NotBlank(message = "category is required")
    private String category;

    private List<ProductVariantRequest> variants;
}
