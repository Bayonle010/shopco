package com.shopco.cart.builder;

import com.shopco.cart.dto.response.CartItemResponse;
import com.shopco.cart.dto.response.CartResponse;
import com.shopco.cart.dto.response.CartSummaryResponse;
import com.shopco.cart.entity.Cart;
import com.shopco.cart.entity.CartItem;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@NoArgsConstructor
public class CartResponseBuilder {
    public static CartResponse buildCartResponse(Cart cart){
        List<CartItemResponse> items = cart.getItems().stream()
                .map(CartResponseBuilder::toItemDTO)
                .toList();


        // sum of per-item discounts
        BigDecimal itemDiscountTotal = items.stream().map(CartItemResponse::getItemDiscount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // sub-total after item-level discounts (sum of line total)
        BigDecimal subtotal = items.stream()
                .map(CartItemResponse::getLineTotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        CartSummaryResponse summary = CartSummaryResponse.builder()
                .subtotal(subtotal)
                .discountTotal(itemDiscountTotal)
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
        int qty = line.getQuantity();

        BigDecimal list = nvl(line.getListPriceSnapshot(), line.getUnitPriceSnapshot());
        BigDecimal unit = nvl(line.getUnitPriceSnapshot(), BigDecimal.ZERO);
        BigDecimal perUnitDisc =maxZero(list.subtract(unit));
        BigDecimal itemDiscount = perUnitDisc.multiply(BigDecimal.valueOf(qty));
        BigDecimal lineTotal = unit.multiply(BigDecimal.valueOf(qty));

        return CartItemResponse.builder()
                .cartItemId(line.getId())
                .productId(line.getProduct().getId())
                .productVariantId(line.getProductVariant() != null ? line.getProductVariant().getId() : null)
                .title(line.getProduct().getName())
                .quantity(qty)
                .listPrice(list)
                .discountPercent(line.getDiscountPercentSnapshot())
                .itemDiscount(itemDiscount)
                .unitPrice(unit)
                .lineTotal(lineTotal)
                .currency("NGN")
                .build();
    }

    private static BigDecimal nvl(BigDecimal a, BigDecimal b) { return a != null ? a : b; }
    private static BigDecimal maxZero(BigDecimal v) { return v.compareTo(BigDecimal.ZERO) < 0 ? BigDecimal.ZERO : v; }
}
