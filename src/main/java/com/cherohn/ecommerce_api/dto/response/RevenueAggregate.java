package com.cherohn.ecommerce_api.dto.response;

import lombok.*;

import java.math.BigDecimal;

@Getter
@AllArgsConstructor
public class RevenueAggregate {

    private Long totalOrders;
    private BigDecimal totalRevenue;
}
