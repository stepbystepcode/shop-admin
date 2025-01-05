package com.merchant.controller;

import com.merchant.annotation.Log;
import com.merchant.dto.MerchantStatisticsDTO;
import com.merchant.dto.MerchantTrendDTO;
import com.merchant.service.StatisticsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/statistics")
@CrossOrigin(origins = "*")
public class StatisticsController {

    @Autowired
    private StatisticsService statisticsService;

    @GetMapping("/merchants")
    @PreAuthorize("hasRole('ADMIN')")
    @Log("获取商家统计数据")
    public ResponseEntity<MerchantStatisticsDTO> getMerchantStatistics() {
        return ResponseEntity.ok(statisticsService.getMerchantStatistics());
    }

    @GetMapping("/merchants/trend")
    @PreAuthorize("hasRole('ADMIN')")
    @Log("获取商家趋势数据")
    public ResponseEntity<List<MerchantTrendDTO>> getMerchantTrend(
            @RequestParam(defaultValue = "7") int days) {
        return ResponseEntity.ok(statisticsService.getMerchantTrend(days));
    }
}
