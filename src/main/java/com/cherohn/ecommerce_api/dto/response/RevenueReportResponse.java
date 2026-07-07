package com.cherohn.ecommerce_api.dto.response;

import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Builder
@AllArgsConstructor
public class RevenueReportResponse {

    private LocalDate startDate;
    private LocalDate endDate;
    private Long totalOrders;
    private BigDecimal totalRevenue;
    private BigDecimal averageOrderValue;
}
