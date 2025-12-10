package com.shopco.order.entity;

import com.shopco.product.entity.ProductVariant;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.util.UUID;

@Builder
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "order_items")
@Entity
public class OrderItem {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    private Order order;

    @ManyToOne(fetch = FetchType.LAZY)
    private ProductVariant productVariant;

    @Column(nullable = false)
    private int quantity;

    private BigDecimal unitPrice;

    private BigDecimal totalPrice;  //(unit price * quantity)

}
