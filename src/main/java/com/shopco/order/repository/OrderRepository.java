package com.shopco.order.repository;

import com.shopco.order.entity.Order;
import com.shopco.order.enums.OrderStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface OrderRepository extends JpaRepository<Order, UUID> {
    Page<Order> findByUser_Id(UUID userId, Pageable pageable);
    Page<Order> findByUser_IdAndOrderStatus(UUID userId, OrderStatus orderStatus, Pageable pageable);
    Optional<Order> findByIdAndUser_Id(UUID orderId, UUID userId);
}
