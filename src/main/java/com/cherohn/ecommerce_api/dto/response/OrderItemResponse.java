package com.cherohn.ecommerce_api.dto.response;

import lombok.*;

import java.math.BigDecimal;

@Getter
@Builder
@AllArgsConstructor
public class OrderItemResponse {

    private String productName;
    private Integer quantity;
    private BigDecimal unitPrice;
    private BigDecimal subtotal;
}
