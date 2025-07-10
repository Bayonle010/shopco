package com.shopco.product.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class ProductVariantRequest {

    @NotBlank(message = "color cannot be empty")
    private String color;

    @NotBlank(message = "size cannot be empty")
    private String size;

    @NotBlank(message = "stock cannot be empty")
    private int stock;
}
