package com.shopco.order.service.impl;

import com.shopco.cart.entity.Cart;
import com.shopco.core.exception.ResourceNotFoundException;
import com.shopco.core.response.ApiResponse;
import com.shopco.core.response.ResponseUtil;
import com.shopco.core.utils.PaginationUtility;
import com.shopco.order.builder.OrderSummaryResponseBuilder;
import com.shopco.order.dto.request.CompleteOrderRequest;
import com.shopco.order.dto.response.OrderItemResponse;
import com.shopco.order.dto.response.OrderSummaryResponse;
import com.shopco.order.entity.Order;
import com.shopco.order.entity.OrderItem;
import com.shopco.order.enums.OrderStatus;
import com.shopco.order.repository.OrderItemRepository;
import com.shopco.order.repository.OrderRepository;
import com.shopco.order.service.OrderService;
import com.shopco.user.entity.User;
import com.shopco.user.service.UserService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final UserService userService;

    public OrderServiceImpl(OrderRepository orderRepository, OrderItemRepository orderItemRepository, UserService userService) {
        this.orderRepository = orderRepository;
        this.orderItemRepository = orderItemRepository;
        this.userService = userService;
    }

    @Override
    public Order createOrderFromCart(Cart cart, String confirmationCode) {
        validateCartNotEmpty(cart);

        Order order = buildOrderHeader(cart, confirmationCode);

        List<OrderItem> orderItems = buildOrderItemsFromCart(cart, order);

        order.setItems(orderItems);

        return orderRepository.save(order);
    }

    @Override
    public ResponseEntity<ApiResponse> fetchOrdersForAuthenticatedUser(String status, int page, int pageSize, Authentication authentication) {
        User user = userService.getAuthenticatedUser(authentication);

        OrderStatus filterStatus = OrderStatus.fromString(status);

        Pageable pageable = PaginationUtility.createPageRequest(page, pageSize);

        Page<Order> orderPage = (filterStatus == null)
                ? orderRepository.findByUser_Id(user.getId(), pageable)
                : orderRepository.findByUser_IdAndOrderStatus(user.getId(), filterStatus, pageable);

        List<OrderSummaryResponse> orders = orderPage.getContent().stream()
                .map(OrderSummaryResponseBuilder::toOrderSummaryResponse)
                .toList();

        Map<String, Object> meta = PaginationUtility.buildPaginationMetadata(orderPage);

        return ResponseEntity.status(HttpStatus.OK)
                .body(ResponseUtil.success(0, "Orders fetched successfully", orders, meta));
    }

    @Override
    public ResponseEntity<ApiResponse> fetchOrderItemsForAuthenticatedUserOrder(UUID orderId, int page, int pageSize, Authentication authentication) {
        User user = userService.getAuthenticatedUser(authentication);

        Order order = orderRepository.findByIdAndUser_Id(orderId, user.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Order not found for user"));

        Pageable pageable = PaginationUtility.createPageRequest(page, pageSize, Sort.by(Sort.Order.desc("id")));

        Page<OrderItem> itemPage = orderItemRepository.findByOrder_Id(order.getId(), pageable);

        List<OrderItemResponse> items = itemPage.getContent().stream()
                .map(OrderSummaryResponseBuilder::toOrderItemResponse)
                .toList();

        Map<String, Object> meta = PaginationUtility.buildPaginationMetadata(itemPage);

        return ResponseEntity.status(HttpStatus.OK)
                .body(ResponseUtil.success(0, "Order items fetched successfully", items, meta));
    }

    @Override
    public ResponseEntity<ApiResponse> completeOrderAsAdmin(UUID orderId, CompleteOrderRequest request, Authentication authentication) {
        userService.verifyAdmin(authentication);

        Order order = orderRepository.findById(orderId).orElseThrow(() -> new ResourceNotFoundException("Order not found"));

        // 3. Validate that order can be completed
        validateOrderCanBeCompleted(order);

        // 4. Validate confirmation code
        validateConfirmationCode(order, request.confirmationCode());

        // 5. Mark as completed
        markOrderAsCompleted(order);

        orderRepository.save(order);

        // 6. Build response DTO
        OrderSummaryResponse response = OrderSummaryResponseBuilder.toOrderSummaryResponse(order);

        return ResponseEntity.status(HttpStatus.OK)
                .body(ResponseUtil.success(0, "Order marked as completed", response, null));
    }


    private void validateCartNotEmpty(Cart cart) {
        if (cart.getItems() == null || cart.getItems().isEmpty()) {
            throw new com.shopco.core.exception.IllegalArgumentException("Cannot create order from empty cart");
        }
    }

    private Order buildOrderHeader(Cart cart, String confirmationCode) {
        return Order.builder()
                .amount(cart.getTotalAmount())
                .user(cart.getUser())
                .cartId(cart.getId())
                .orderStatus(OrderStatus.PENDING)
                .confirmationCode(confirmationCode)
                .placedAt(Instant.now())
                .build();
    }

    private List<OrderItem> buildOrderItemsFromCart(Cart cart, Order order) {
        return cart.getItems().stream()
                .map(cartItem -> {
                    BigDecimal unitPrice = cartItem.getUnitPriceSnapshot();
                    int qty = cartItem.getQuantity();
                    BigDecimal total = unitPrice.multiply(BigDecimal.valueOf(qty));

                    return OrderItem.builder()
                            .order(order)
                            .productVariant(cartItem.getProductVariant())
                            .quantity(qty)
                            .unitPrice(unitPrice)
                            .totalPrice(total)
                            .build();
                })
                .toList();
    }

    private void validateOrderCanBeCompleted(Order order) {
        if (order.getOrderStatus() == null) {
            throw new IllegalArgumentException("Order status is not set");
        }

        switch (order.getOrderStatus()) {
            case COMPLETED -> throw new IllegalArgumentException(
                    "Order is already completed"
            );
            case CANCELLED -> throw new IllegalArgumentException(
                    "Cancelled order cannot be completed"
            );
            default -> {
                // PENDING, SHIPPED, etc. → allowed
            }
        }
    }

    private void validateConfirmationCode(Order order, String providedCode) {
        if (providedCode == null || providedCode.isBlank()) {
            throw new IllegalArgumentException("Confirmation code is required");
        }

        if (order.getConfirmationCode() == null || order.getConfirmationCode().isBlank()) {
            throw new IllegalArgumentException(
                    "Order does not have a confirmation code set"
            );
        }

        if (!order.getConfirmationCode().equals(providedCode.trim())) {
            throw new IllegalArgumentException("Invalid confirmation code");
        }
    }

    private void markOrderAsCompleted(Order order) {
        order.setOrderStatus(OrderStatus.COMPLETED);
        order.setCompletedAt(Instant.now());
    }


}
