package com.merchant.controller;

import com.merchant.annotation.Log;
import com.merchant.entity.Merchant;
import com.merchant.entity.MerchantStatus;
import com.merchant.service.MerchantService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/merchants")
@CrossOrigin(origins = "*")
public class MerchantController {
    @Autowired
    private MerchantService merchantService;

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Log("查询商家列表")
    public ResponseEntity<Map<String, Object>> getAllMerchants(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String businessType,
            @RequestParam(required = false) MerchantStatus status
    ) {
        Page<Merchant> merchantPage = merchantService.getAllMerchants(page, size, name, businessType, status);
        Map<String, Object> response = Map.of(
            "list", merchantPage.getContent(),
            "total", merchantPage.getTotalElements(),
            "page", page,
            "pageSize", size
        );
        return ResponseEntity.ok(response);
    }

    @GetMapping("/status/{status}")
    @PreAuthorize("hasRole('ADMIN')")
    @Log("按状态查询商家")
    public ResponseEntity<List<Merchant>> getMerchantsByStatus(@PathVariable MerchantStatus status) {
        return ResponseEntity.ok(merchantService.getMerchantsByStatus(status));
    }

    @PatchMapping("/{id}/status")
    @PreAuthorize("hasRole('ADMIN')")
    @Log("更新商家状态")
    public ResponseEntity<Merchant> updateMerchantStatus(
            @PathVariable Long id,
            @RequestBody Map<String, String> statusUpdate) {
        MerchantStatus status = MerchantStatus.valueOf(statusUpdate.get("status"));
        String rejectReason = statusUpdate.get("rejectReason");
        return ResponseEntity.ok(merchantService.updateMerchantStatus(id, status, rejectReason));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Log("获取商家详情")
    public ResponseEntity<Merchant> getMerchantDetail(@PathVariable Long id) {
        return ResponseEntity.ok(merchantService.getMerchantById(id));
    }

    @GetMapping("/statistics")
    @PreAuthorize("hasRole('ADMIN')")
    @Log("获取商家统计数据")
    public ResponseEntity<Map<MerchantStatus, Long>> getMerchantStatusStatistics() {
        return ResponseEntity.ok(merchantService.getMerchantStatusStatistics());
    }
}
