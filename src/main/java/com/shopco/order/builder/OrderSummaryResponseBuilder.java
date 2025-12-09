package com.shopco.order.builder;

import com.shopco.order.dto.response.OrderItemResponse;
import com.shopco.order.dto.response.OrderSummaryResponse;
import com.shopco.order.entity.Order;
import com.shopco.order.entity.OrderItem;

public class OrderSummaryResponseBuilder {
    public static OrderSummaryResponse toOrderSummaryResponse(Order order){
        return OrderSummaryResponse.builder()
                .orderId(order.getId())
                .orderStatus(order.getOrderStatus())
                .confirmationCode(order.getConfirmationCode())
                .placedAt(order.getPlacedAt())
                .completedAt(order.getCompletedAt())
                .amount(order.getAmount())
                .build();
    }

    public static OrderItemResponse toOrderItemResponse(OrderItem orderItem){
        return OrderItemResponse.builder()
                .orderItemId(orderItem.getId())
                .productVariantId(orderItem.getProductVariant().getId())
                .quantity(orderItem.getQuantity())
                .unitPrice(orderItem.getUnitPrice())
                .totalPrice(orderItem.getTotalPrice())
                .build();
    }
}
