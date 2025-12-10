package com.shopco.order.service;

import com.shopco.cart.entity.Cart;
import com.shopco.core.response.ApiResponse;
import com.shopco.order.dto.request.CompleteOrderRequest;
import com.shopco.order.entity.Order;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;

import java.util.UUID;

public interface OrderService {
    Order createOrderFromCart(Cart cart, String confirmationCode);
    ResponseEntity<ApiResponse> fetchOrdersForAuthenticatedUser(
            String status,
            int page,
            int pageSize,
            Authentication authentication
    );

    ResponseEntity<ApiResponse> fetchOrderItemsForAuthenticatedUserOrder(
            UUID orderId,
            int page,
            int pageSize,
            Authentication authentication
    );

    ResponseEntity<ApiResponse> completeOrderAsAdmin(
            UUID orderId,
            CompleteOrderRequest request,
            Authentication authentication
    );
}
