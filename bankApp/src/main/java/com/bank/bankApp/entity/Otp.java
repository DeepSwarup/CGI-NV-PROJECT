package com.bank.bankApp.entity;


import jakarta.persistence.*;
import lombok.Data;

import java.time.Instant;

@Data
@Entity
@Table(name = "otps", indexes = {
        @Index(name = "idx_user_type", columnList = "userId, type"),
        @Index(name = "idx_expires", columnList = "expiresAt")
})
public class Otp {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long userId;

    @Column(length = 10)
    private String code;

    @Column(length = 30)
    private String type; // e.g. "DEPOSIT", "WITHDRAW", "TRANSFER", "LOGIN"

    private Instant createdAt;
    private Instant expiresAt;

    private boolean consumed = false;
    private int attempts = 0;

    // optional: store lastSentAt for rate-limiting
    private Instant lastSentAt;

}
