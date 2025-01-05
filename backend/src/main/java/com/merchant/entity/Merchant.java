package com.merchant.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "merchants")
public class Merchant {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String contactPerson;

    @Column(nullable = false)
    private String contactPhone;

    @Column(nullable = false)
    private String licenseNumber;

    @Column(length = 1000)
    private String licenseUrl;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private MerchantStatus status = MerchantStatus.PENDING;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
