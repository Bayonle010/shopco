package com.shopco.order.controller;


import com.shopco.core.response.ApiResponse;
import com.shopco.order.dto.request.CompleteOrderRequest;
import com.shopco.order.service.OrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@Tag(name = "Orders")
@RestController
@RequestMapping("/api/v1/orders")
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @Operation(summary = "Fetch Orders (filter is optional")
    @GetMapping("")
    public ResponseEntity<ApiResponse> getOrdersForAuthenticatedUser(
            @RequestParam(value = "status", required = false) String status,
            @RequestParam(value = "page", defaultValue = "1", required = false) int page,
            @RequestParam(value = "pageSize", defaultValue = "35", required = false) int pageSize,
            Authentication authentication
    ) {
        return orderService.fetchOrdersForAuthenticatedUser(status, page, pageSize, authentication);
    }

    @GetMapping("/{orderId}/items")
    public ResponseEntity<ApiResponse> getOrderItemsForAuthenticatedUserOrder(
            @PathVariable("orderId") UUID orderId,
            @RequestParam(value = "page", defaultValue = "1") int page,
            @RequestParam(value = "pageSize", defaultValue = "35") int pageSize,
            Authentication authentication
    ) {
        return orderService.fetchOrderItemsForAuthenticatedUserOrder(orderId, page, pageSize, authentication);
    }


    @PatchMapping("/{orderId}/complete")
    public ResponseEntity<ApiResponse> completeOrderAsAdmin(
            @PathVariable("orderId") UUID orderId,
            @RequestBody CompleteOrderRequest request,
            Authentication authentication
    ) {
        return orderService.completeOrderAsAdmin(orderId, request, authentication);
    }
}
