package com.shopco.Authentication.token;

import com.shopco.user.User;
import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;


@Setter
@Getter
@Builder
@AllArgsConstructor
@Entity
@NoArgsConstructor
public class Token {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(unique = true, length = 1000, nullable = false)
    private String token;

    @Enumerated(EnumType.STRING)
    private TokenType tokenType;

    private boolean expired;

    private boolean revoked;

    private Instant createdAt;

    private Instant expiresAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;






}
