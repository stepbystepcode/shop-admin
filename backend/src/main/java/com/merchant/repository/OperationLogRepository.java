package com.merchant.repository;

import com.merchant.entity.OperationLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface OperationLogRepository extends JpaRepository<OperationLog, Long>, JpaSpecificationExecutor<OperationLog> {
    List<OperationLog> findByCreateTimeBetweenOrderByCreateTimeDesc(LocalDateTime start, LocalDateTime end);
    List<OperationLog> findByUsernameOrderByCreateTimeDesc(String username);
}
