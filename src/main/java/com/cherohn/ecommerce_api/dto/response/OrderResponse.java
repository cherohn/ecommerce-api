package com.cherohn.ecommerce_api.dto.response;

import com.cherohn.ecommerce_api.model.OrderStatus;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Builder
@AllArgsConstructor
public class OrderResponse {

    private Long id;
    private String orderNumber;
    private OrderStatus status;
    private BigDecimal totalAmount;
    private String customerName;
    private List<OrderItemResponse> items;
    private LocalDateTime createdAt;
}
