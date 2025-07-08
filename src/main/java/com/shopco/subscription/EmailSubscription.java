package com.shopco.subscription;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;

@Setter
@Getter
@Entity
@Table(name = "email_subscriptions")
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EmailSubscription {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(unique = true)
    private String email;

    @CreationTimestamp
    private LocalDateTime subscribedAt;
}
