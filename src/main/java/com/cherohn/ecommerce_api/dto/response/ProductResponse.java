package com.cherohn.ecommerce_api.dto.response;

import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Builder
@AllArgsConstructor
public class ProductResponse{

    private Long id;
    private String name;
    private String description;
    private BigDecimal price;
    private Integer stockQuantity;
    private boolean active;
    private String categoryName;
    private LocalDateTime createdAt;
}
