package com.shopco.product.dto.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.shopco.product.entity.ProductVariant;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Builder
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ProductResponse {

    private UUID id;

    private String name;

    private String description;

    private BigDecimal price;

    private Double discountInPercentage;

    private String imageUrl;

    private String category;

    private List<ProductVariantResponse> variants;
}
