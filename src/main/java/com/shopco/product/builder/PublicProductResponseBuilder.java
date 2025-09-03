package com.shopco.product.builder;

import com.shopco.product.dto.response.PublicProductResponse;
import com.shopco.product.entity.Product;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class PublicProductResponseBuilder {
    public static PublicProductResponse toProduct(Product product){
        BigDecimal after = (product.getDiscount() <= 0 )
                ? product.getPrice() : product.getPrice()
                .multiply(BigDecimal.valueOf(100 - product.getDiscount()))
                .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);


        boolean isAvailable = product.getTotalSold() > 0;

        return PublicProductResponse.builder()
                .id(product.getId())
                .name(product.getName())
                .description(product.getDescription())
                .imageUrl(product.getImageUrl())
                .category(String.valueOf(product.getCategory()))
                .price(product.getPrice())
                .discountInPercentage(product.getDiscount())
                .priceAfterDiscount(after)
                .build();

    }
}
