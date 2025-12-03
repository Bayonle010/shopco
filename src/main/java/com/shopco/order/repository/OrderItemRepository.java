package com.shopco.order.repository;

import com.shopco.order.entity.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

public interface OrderItemRepository extends JpaRepository<OrderItem, UUID> {
}
