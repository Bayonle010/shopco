package com.shopco.product.dto.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Positive;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class PublicProductListParams {

    @Min(1) @Max(200)
    @Positive
    private int page;

    @Positive
    @Min(1) @Max(35)
    private int pageSize;
    private String category;
    private String search;
    private String colors;
    private String size;
    private String sort;
    private BigDecimal minPrice;
    private BigDecimal maxPrice;
//    long page, long pageSize, String category, String search, String colors, String size, BigDecimal minPrice, Ma
}
