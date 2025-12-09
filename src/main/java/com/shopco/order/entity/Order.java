package com.shopco.order.entity;

import com.shopco.order.enums.OrderStatus;
import com.shopco.product.enums.Status;
import com.shopco.user.entity.User;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Builder
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "orders")
@Entity
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    private User user;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OrderItem> items;

    @Column(name = "cart_id")
    private UUID cartId;

    private OrderStatus orderStatus;

    private String confirmationCode;

    private Instant placedAt;

    private Instant completedAt;

    private Status status;

    @CreationTimestamp
    private Instant createdAt;

}
