package com.merchant.service;

import com.merchant.dto.MerchantStatisticsDTO;
import com.merchant.dto.MerchantTrendDTO;
import com.merchant.entity.MerchantStatus;
import com.merchant.repository.MerchantRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class StatisticsService {

    @Autowired
    private MerchantRepository merchantRepository;

    public MerchantStatisticsDTO getMerchantStatistics() {
        long total = merchantRepository.count();
        long pending = merchantRepository.countByStatus(MerchantStatus.PENDING);
        long approved = merchantRepository.countByStatus(MerchantStatus.APPROVED);
        long rejected = merchantRepository.countByStatus(MerchantStatus.REJECTED);
        long disabled = merchantRepository.countByStatus(MerchantStatus.DISABLED);

        double approvalRate = total > 0 ? (double) approved / total * 100 : 0;
        double rejectionRate = total > 0 ? (double) rejected / total * 100 : 0;

        return MerchantStatisticsDTO.builder()
                .total(total)
                .pending(pending)
                .approved(approved)
                .rejected(rejected)
                .disabled(disabled)
                .approvalRate(Math.round(approvalRate * 100.0) / 100.0)
                .rejectionRate(Math.round(rejectionRate * 100.0) / 100.0)
                .build();
    }

    public List<MerchantTrendDTO> getMerchantTrend(int days) {
        LocalDate endDate = LocalDate.now();
        LocalDate startDate = endDate.minusDays(days - 1);

        List<MerchantTrendDTO> trends = new ArrayList<>();
        
        // 获取时间范围内的所有商家
        List<LocalDate> dateRange = startDate.datesUntil(endDate.plusDays(1))
                .collect(Collectors.toList());

        for (LocalDate date : dateRange) {
            LocalDateTime dayStart = date.atStartOfDay();
            LocalDateTime dayEnd = date.atTime(LocalTime.MAX);

            // 获取每个状态的数量
            for (MerchantStatus status : MerchantStatus.values()) {
                long count = merchantRepository.countByStatusAndCreatedAtBetween(
                    status, dayStart, dayEnd);
                
                trends.add(MerchantTrendDTO.builder()
                    .date(date)
                    .count(count)
                    .status(status.name())
                    .build());
            }
        }

        return trends;
    }
}
