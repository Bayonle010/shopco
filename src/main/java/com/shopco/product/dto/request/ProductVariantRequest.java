package com.shopco.product.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ProductVariantRequest {

    @NotBlank(message = "color cannot be empty")
    private String color;

    @NotBlank(message = "size cannot be empty")
    private String size;

    @Min(value = 0, message = "stock cannot be empty")
    private int stock;
}
