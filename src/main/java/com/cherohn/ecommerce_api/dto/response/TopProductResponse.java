package com.cherohn.ecommerce_api.dto.response;

import lombok.*;

import java.math.BigDecimal;


@Getter
@Builder
@AllArgsConstructor
public class TopProductResponse {
    private Long productId;
    private String productName;
    private Long totalQuantitySold;
    private BigDecimal totalRevenue;
}

