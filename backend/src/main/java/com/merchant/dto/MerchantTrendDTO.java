package com.merchant.dto;

import lombok.Data;
import lombok.Builder;
import java.time.LocalDate;

@Data
@Builder
public class MerchantTrendDTO {
    private LocalDate date;
    private long count;
    private String status;
}
