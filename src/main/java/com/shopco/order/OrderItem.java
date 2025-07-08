package com.shopco.order;

import com.shopco.product.entity.ProductVariant;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.boot.web.servlet.filter.OrderedRequestContextFilter;

import java.math.BigDecimal;
import java.util.UUID;

@Builder
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "order_items")
public class OrderItem {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    private Order order;

    @ManyToOne(fetch = FetchType.LAZY)
    private ProductVariant productVariant;

    private int quantity;

    private BigDecimal uniPrice;

}
