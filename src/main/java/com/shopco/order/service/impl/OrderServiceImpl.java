package com.shopco.order.service.impl;

import com.shopco.cart.entity.Cart;
import com.shopco.order.entity.Order;
import com.shopco.order.entity.OrderItem;
import com.shopco.order.enums.OrderStatus;
import com.shopco.order.repository.OrderRepository;
import com.shopco.order.service.OrderService;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

@Service
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;

    public OrderServiceImpl(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    @Override
    public Order createOrderFromCart(Cart cart, String confirmationCode) {
        validateCartNotEmpty(cart);

        Order order = buildOrderHeader(cart, confirmationCode);

        List<OrderItem> orderItems = buildOrderItemsFromCart(cart, order);

        order.setItems(orderItems);

        return orderRepository.save(order);
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
}
