package com.shopco.cart.builder;

import com.shopco.cart.dto.response.CartItemResponse;
import com.shopco.cart.dto.response.CartResponse;
import com.shopco.cart.dto.response.CartSummaryResponse;
import com.shopco.cart.entity.Cart;
import com.shopco.cart.entity.CartItem;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;

public class CartResponseBuilder {
    private static CartResponse buildCartResponse(Cart cart){
        List<CartItemResponse> items = cart.getItems().stream()
                .map(CartResponseBuilder::toItemDTO)
                .toList();

        BigDecimal subtotal = items.stream()
                .map(CartItemResponse::getLineTotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        CartSummaryResponse summary = CartSummaryResponse.builder()
                .subtotal(subtotal)
                .discountTotal(BigDecimal.ZERO) // fill when you add coupons
                .shipping(BigDecimal.ZERO)      // fill when you add shipping
                .tax(BigDecimal.ZERO)           // fill when you add tax
                .total(subtotal)
                .build();

        return CartResponse.builder()
                .cartId(cart.getId())
                .userId(cart.getUser().getId())
                .items(items)
                .summary(summary)
                .build();

    }

    private static CartItemResponse toItemDTO(CartItem line) {
        int qty = line.getQuantity() == null ? 1 : line.getQuantity();
        BigDecimal unit = line.getUnitPriceSnapshot() == null ? BigDecimal.ZERO : line.getUnitPriceSnapshot();
        BigDecimal lineTotal = unit.multiply(BigDecimal.valueOf(qty));

        return CartItemResponse.builder()
                .cartItemId(line.getId())
                .productId(line.getProduct().getId())
                .productVariantId(line.getProductVariant() != null ? line.getProductVariant().getId() : null)
                .title(line.getProduct().getName())
                .quantity(qty)
                .unitPrice(unit)
                .lineTotal(lineTotal)
                .currency("NGN")
                .build();
    }
}
