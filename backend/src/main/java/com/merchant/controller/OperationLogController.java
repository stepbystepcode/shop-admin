package com.merchant.controller;

import com.merchant.entity.OperationLog;
import com.merchant.repository.OperationLogRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/logs")
@CrossOrigin(origins = "*")
public class OperationLogController {

    @Autowired
    private OperationLogRepository operationLogRepository;

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<OperationLog>> getLogs(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startTime,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endTime) {
        
        if (startTime != null && endTime != null) {
            return ResponseEntity.ok(
                operationLogRepository.findByCreateTimeBetweenOrderByCreateTimeDesc(startTime, endTime)
            );
        }
        
        return ResponseEntity.ok(operationLogRepository.findAll());
    }

    @GetMapping("/user/{username}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<OperationLog>> getUserLogs(@PathVariable String username) {
        return ResponseEntity.ok(operationLogRepository.findByUsernameOrderByCreateTimeDesc(username));
    }
}
