package com.merchant.repository;

import com.merchant.entity.Merchant;
import com.merchant.entity.MerchantStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface MerchantRepository extends JpaRepository<Merchant, Long> {
    List<Merchant> findByStatus(MerchantStatus status);
    long countByStatus(MerchantStatus status);

    @Query("SELECT COUNT(m) FROM Merchant m WHERE m.createdAt BETWEEN :startDate AND :endDate AND m.status = :status")
    long countByStatusAndCreatedAtBetween(
        @Param("status") MerchantStatus status,
        @Param("startDate") LocalDateTime startDate,
        @Param("endDate") LocalDateTime endDate
    );

    @Query("SELECT m FROM Merchant m WHERE m.createdAt BETWEEN :startDate AND :endDate ORDER BY m.createdAt")
    List<Merchant> findByCreatedAtBetween(
        @Param("startDate") LocalDateTime startDate,
        @Param("endDate") LocalDateTime endDate
    );
}
