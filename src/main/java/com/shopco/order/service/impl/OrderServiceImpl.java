package com.shopco.order.service.impl;

import com.shopco.cart.entity.Cart;
import com.shopco.core.exception.ResourceNotFoundException;
import com.shopco.core.response.ApiResponse;
import com.shopco.core.response.ResponseUtil;
import com.shopco.core.utils.PaginationUtility;
import com.shopco.order.builder.OrderSummaryResponseBuilder;
import com.shopco.order.dto.OrderItemResponse;
import com.shopco.order.dto.OrderSummaryResponse;
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

//    private OrderStatus resolveOrderStatus(String status) {
//        if (status == null || status.isBlank()) {
//            return null;
//        }
//        try {
//            // allow case-insensitive, e.g. "pending", "PENDING"
//            return OrderStatus.valueOf(status.toUpperCase());
//        } catch (com.shopco.core.exception.IllegalArgumentException e) {
//            throw new IllegalArgumentException("Invalid order status: " + status);
//        }
//    }

}
