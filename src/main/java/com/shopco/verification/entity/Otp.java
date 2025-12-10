package com.shopco.verification.entity;

import com.shopco.user.entity.User;
import com.shopco.verification.enums.OtpEvent;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.UUID;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
@Table(name = "otp")
@Entity
public class Otp {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;


    @CreationTimestamp
    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime expiresAt;

    @Column(name = "token")
    @ColumnDefault("''")
    private String token; // hashed refresh token

    @Column(name = "expired")
    @ColumnDefault(value = "false")
    private boolean expired;

    @Column(name = "expiry_time")
    private Instant expiryTime;

    @Column(name = "email")
    @ColumnDefault(value = "''")
    private String email;

    @Column(name = "otp_event")
    @Enumerated(EnumType.STRING)
    @ColumnDefault(value = "'NONE'")
    private OtpEvent otpEvent;


}
