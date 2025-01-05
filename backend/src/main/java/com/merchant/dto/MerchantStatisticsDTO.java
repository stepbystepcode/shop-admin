package com.merchant.dto;

import lombok.Data;
import lombok.Builder;

@Data
@Builder
public class MerchantStatisticsDTO {
    private long total;
    private long pending;
    private long approved;
    private long rejected;
    private long disabled;
    private double approvalRate;
    private double rejectionRate;
}
